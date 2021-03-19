import commonjs from '@rollup/plugin-commonjs';
import resolve from '@rollup/plugin-node-resolve';
import css from 'rollup-plugin-css-only';
import svelte from './plugin';

const production = !process.env.ROLLUP_WATCH;

export default {
	input: 'src/main/resources/index.js',
	output: {
		format: 'iife',
		name: 'app',
		dir: 'build',
		entryFileNames: 'client/[name].js',
		assetFileNames: '[name].json'
	},
	plugins: [
		svelte({
			preprocess: true,
			compilerOptions: {
				format: 'cjs',
				// enable run-time checks when not in production
				dev: !production
			}
		}),
		css({ output: 'bundle.css' }),
		resolve({
			browser: true,
			dedupe: ['svelte']
		}),
		commonjs()
	],
	watch: {
		clearScreen: false
	}
};
