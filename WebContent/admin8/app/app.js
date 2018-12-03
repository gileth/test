Ext.application({
    name: 'app',

    extend: 'app.Application',

    requires: [
        'app.view.main.Main'
    ],
    mainView: 'app.view.main.Main'
});