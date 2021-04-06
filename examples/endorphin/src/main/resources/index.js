import endorphin from 'endorphin';
import * as App from './components/app-root.html';

endorphin('app-root', App, {
    target: document.body,
    props: {
        user: {
            name: 'Vadim',
            links: ['https://tt.me/vadim', 'https://ok.ru/vadim'],
            address: '1428 Elm Street'
        },
        access: true
    }
});
