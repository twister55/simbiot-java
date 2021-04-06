import endorphin from 'endorphin';
import * as MyComponent from './components/my-component.html';

endorphin('my-component', MyComponent, {
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
