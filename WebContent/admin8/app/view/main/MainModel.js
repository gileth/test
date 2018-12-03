Ext.define('app.view.main.MainModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.main',

    data: {
        name: '后台管理系统',
        leftName: '系统菜单',
        // 系统信息
        system : {
            name : '后台管理系统',
            version : 'V1.0',
            iconUrl : ''
        },

        // 用户单位信息和用户信息
        user : {
            company : '',
            organname : '',
            username : ''
        },

        // 服务单位和服务人员信息
        service : {
            company : '',
            name : '',
            phonenumber : '',
            email : '',
            copyright : ''
        }
    }
});