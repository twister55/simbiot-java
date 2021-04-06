import resolve from '@rollup/plugin-node-resolve';
import endorphin from './plugin';

export default {
	input: 'src/main/resources/index.js',
	output: {
		format: 'iife',
		name: 'app',
		dir: 'build',
		entryFileNames: 'client/index.js',
		assetFileNames: '[name].[ext]'
	},
	plugins: [
        endorphin({
            template: {
                moduleVars: true,
                mangleNames: true,
            }
        }),
		resolve({
			browser: true
		})
	],
	watch: {
		clearScreen: false
	}
};
