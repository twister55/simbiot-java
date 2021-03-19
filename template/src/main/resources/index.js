import App from './components/App.svelte';

const app = new App({
    target: document.body,
    props: {
        name: 'world',
        link: 'https://svelte.dev'
    }
});

export default app;
