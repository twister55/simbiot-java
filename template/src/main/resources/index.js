import App from './components/App.svelte';

const app = new App({
    target: document.body,
    hydrate: true,
    props: {
        user: {
            name: 'Vadim',
            links: ['https://tt.me/vadim', 'https://ok.ru/vadim'],
            address: '1428 Elm Street'
        },
        access: true
    }
});

export default app;
