"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const fs = require("fs");
const path = require("path");
const rollup_pluginutils_1 = require("rollup-pluginutils");
const source_map_1 = require("source-map");
const acorn_walk_1 = require("acorn-walk");
const defaultScriptType = 'text/javascript';
const defaultStyleType = 'text/css';
const defaultOptions = {
    extensions: ['.html', '.end'],
    types: {
        [defaultScriptType]: '.js',
        [defaultStyleType]: '.css',
        'typescript': '.ts',
        'ts': '.ts',
        'javascript': '.js',
        'sass': '.sass',
        'scss': '.scss'
    }
};
const defaultCSSOptions = {
    scope(filePath) {
        // A simple function for calculation of has (Adler32) from given string
        let s1 = 1, s2 = 0;
        for (let i = 0, len = filePath.length; i < len; i++) {
            s1 = (s1 + filePath.charCodeAt(i)) % 65521;
            s2 = (s2 + s1) % 65521;
        }
        return 'e' + ((s2 << 16) + s1).toString(36);
    }
};
const BASE_PATH = 'src/main/resources/';

/**
 * @param {string} filename
 * @param {object} ast
 * @return {EmittedAsset}
 */
function astFile(filename, ast) {
    return {
        name: filename.replace(BASE_PATH, 'ast/').replace('.html', '.json'),
        type: 'asset',
        source: JSON.stringify(ast)
    };
}

export default function endorphin(options) {
    options = Object.assign(Object.assign({}, defaultOptions), options);
    if (!options.css) {
        options.css = defaultCSSOptions;
    }
    else {
        options.css = Object.assign(Object.assign({}, defaultCSSOptions), options.css);
    }
    const filter = rollup_pluginutils_1.createFilter(options.include, options.exclude);
    const jsResources = {};
    const componentStyles = new Map();
    const endorphin = require('endorphin/compiler');
    return {
        name: 'endorphin',
        buildStart() {
            if (options.template && Array.isArray(options.template.helpers)) {
                // Resolve helpers symbols, defined in given list of helper files
                const helpers = {};
                options.template.helpers.forEach(helper => {
                    const absPath = path.resolve(helper);
                    const contents = fs.readFileSync(absPath, 'utf8');
                    this.addWatchFile(absPath);
                    const ast = this.parse(contents, { sourceType: 'module' });
                    const symbols = [];
                    acorn_walk_1.simple(ast, {
                        ExportNamedDeclaration(node) {
                            if (node.declaration.type === 'FunctionDeclaration' || node.declaration.type === 'VariableDeclaration') {
                                symbols.push(node.declaration.id.name);
                            }
                        }
                    });
                    helpers[helper] = symbols;
                });
                options.template.helpers = helpers;
            }
        },
        load(id) {
            return id in jsResources ? jsResources[id] : null;
        },
        resolveId(id) {
            return id in jsResources ? id : null;
        },
        async transform(source, id) {
            if (!filter(id) || !options.extensions.includes(path.extname(id))) {
                return null;
            }
            const cssScope = options.css.scope(id);
            // Parse Endorphin template into AST
            const componentName = options.componentName ? options.componentName(id) : '';
            const parsed = endorphin.parse(source, id, options.template);
            const { scripts, stylesheets } = parsed.ast;

            const filename = path.relative(process.cwd(), id);
            this.emitFile(astFile(filename, parsed.ast));

            // For inline scripts, emit source code as external module so we can
            // hook on it???s processing
            scripts.forEach((script, i) => {
                if (script.content) {
                    let assetUrl = createAssetUrl(parsed.url, options.types[script.mime] || options.types[defaultScriptType], i);
                    if (assetUrl[0] !== '.' && assetUrl[0] !== '/' && assetUrl[0] !== '@') {
                        assetUrl = `./${assetUrl}`;
                    }
                    jsResources[assetUrl] = script.transformed || script.content;
                    script.url = assetUrl;
                    script.transformed = script.content = void 0;
                }
            });
            // Process stylesheets: apply custom transform (if required) and scope
            // CSS selectors
            componentStyles.set(id, []);
            await Promise.all(stylesheets.map(async (stylesheet) => {
                const isExternal = stylesheet.url !== id;
                // XXX when resolved via `this.resolveId()`, a file name could lead
                // to Node module resolve, e.g. `my-file.css` can be resolved as
                // `node_modules/my-file.css/index.js`
                // let fullId = await this.resolveId(stylesheet.url, id);
                let fullId = path.resolve(path.dirname(id), stylesheet.url);
                let content = '';
                if (isExternal) {
                    // Watch for external stylesheets
                    this.addWatchFile(fullId);
                    content = fs.readFileSync(fullId, 'utf8');
                }
                else {
                    content = stylesheet.content;
                }
                let transformed = { code: content };
                // Apply custom CSS preprocessing
                if (typeof options.css.preprocess === 'function') {
                    const result = await transformResource(stylesheet.mime, content, fullId, options.css.preprocess);
                    if (result != null) {
                        transformed = result;
                    }
                }
                // Isolate CSS selector with scope token
                if (cssScope) {
                    let filename = fullId;
                    let map = transformed.map;
                    if (typeof map === 'string') {
                        map = JSON.parse(map);
                    }
                    if (map && map.file) {
                        filename = map.file;
                    }
                    const scoped = await endorphin.scopeCSS(transformed.code, cssScope, { filename, map });
                    transformed = typeof scoped === 'string' ? { code: scoped } : scoped;
                }
                const node = await nodeFromTransformed(transformed, content, fullId);
                componentStyles.get(id).push(node);
            }));
            // Generate JavaScript code from template AST
            return endorphin.generate(parsed, Object.assign({ module: 'endorphin', cssScope, warn: (msg, pos) => this.warn(msg, pos), component: componentName }, options.template));
        },
        async generateBundle(outputOptions) {
            // Sort stylesheets to preserve contents across builds
            const entries = getEntries(this, options.entries);
            for (const entry of entries) {
                const output = new source_map_1.SourceNode();
                const modulesList = getTopologicalModuleList(this, entry);
                for (const moduleId of modulesList) {
                    if (componentStyles.has(moduleId)) {
                        output.add(componentStyles.get(moduleId));
                    }
                }
                let code, map;
                if (outputOptions.sourcemap) {
                    const result = output.toStringWithSourceMap();
                    code = result.code;
                    map = result.map;
                }
                else {
                    code = output.toString();
                }
                if (typeof options.css.bundle === 'function') {
                    return options.css.bundle(code, map);
                }
                const fileName = path.basename(entry.id, path.extname(entry.id)) + '.css';
                if (map) {
                    const sourceMapName = fileName + '.map';
                    code += `\n/*# sourceMappingURL=${sourceMapName} */`;
                    this.emitFile({
                        type: 'asset',
                        fileName: sourceMapName,
                        source: map.toString()
                    });
                }
                this.emitFile({
                    type: 'asset',
                    name: 'client/' + fileName,
                    source: code
                });
            }
        }
    };
}

async function transformResource(type, content, url, transformer) {
    let transformed = await transformer(type, content, url);
    if (transformer == null) {
        transformed = content;
    }
    const result = typeof transformed === 'string' || Buffer.isBuffer(transformed)
        ? { code: transformed }
        : transformed;
    let code = result.css || result.code;
    let map = result.map;
    if (Buffer.isBuffer(code)) {
        code = code.toString();
    }
    if (Buffer.isBuffer(map)) {
        map = map.toString();
    }
    if (map && map.addMapping) {
        // Source map is a SourceMapGenerator
        result.map = result.map.toJSON();
    }
    return { code, map };
}
async function nodeFromTransformed(data, source, fileName) {
    let node;
    if (typeof data === 'object' && data.map) {
        const consumer = await new source_map_1.SourceMapConsumer(data.map);
        node = source_map_1.SourceNode.fromStringWithSourceMap(data.code, consumer);
    }
    else {
        node = new source_map_1.SourceNode();
        node.add(typeof data === 'string' ? data : data.code);
    }
    node.setSourceContent(fileName, source);
    return node;
}
function createAssetUrl(baseUrl, ext, index = 0) {
    const baseName = baseUrl.slice(0, -path.extname(baseUrl).length);
    return `${baseName}_${index}${ext}`;
}
function getEntries(plugin, entries = []) {
    const entryModules = [];
    const lookup = new Set();
    if (entries.length) {
        for (const entry of entries) {
            const moduleId = path.resolve(entry);
            if (moduleId) {
                addEntry(plugin.getModuleInfo(moduleId), lookup, entryModules);
            }
        }
    }
    else {
        for (const moduleId of plugin.getModuleIds()) {
            const mod = plugin.getModuleInfo(moduleId);
            if (mod.isEntry && !lookup.has(moduleId)) {
                addEntry(mod, lookup, entryModules);
            }
        }
    }
    return entryModules;
}
function getTopologicalModuleList(plugin, entry) {
    const lookup = new Set();
    walkModule(entry, plugin, lookup);
    return Array.from(lookup);
}
function addEntry(mod, lookup, list) {
    if (mod) {
        lookup.add(mod.id);
        list.push(mod);
    }
}
function walkModule(mod, plugin, lookup) {
    for (const dep of mod.importedIds) {
        // NB: use `.has()` check to prevent recursive module loops
        if (!lookup.has(dep)) {
            lookup.add(dep);
            walkModule(plugin.getModuleInfo(dep), plugin, lookup);
        }
    }
}
//# sourceMappingURL=index.js.map
