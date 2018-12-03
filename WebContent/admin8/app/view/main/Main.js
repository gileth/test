Ext.define('app.view.main.Main', {
    extend: 'Ext.container.Container',
    requires: [
        'Ext.plugin.Viewport',
        'Ext.layout.container.Center',
        'app.view.main.MainController',
        'app.view.main.MainModel',
        'Ext.data.*',
        'Ext.form.*',
        'Ext.grid.*',
        'Ext.tree.*',
        'Ext.chart.*'
    ],
    uses: [
        'app.view.main.region.Top',
        'app.view.main.region.Bottom',
        'app.ux.MyTabPabel',
        'Ext.ux.TabCloseMenu',
        'app.view.main.dashboard'
    ],
    xtype: 'app-main',
    initComponent: function () {
        var lf = Ext.create('Ext.form.Panel', {
            mainApp:this,
            id: 'nwLogonForm',
            frame: false,
            border:false,
            width:832,
            height:726,
            bodyStyle: 'background: url("resources/css/images/loginform.png") no-repeat;',
            defaults: {
                //anchor: '100%',
                labelWidth: 80,
                height: 40,
                width: 320,
                x: 288
            },
            defaultType: 'textfield',
            layout: 'absolute',
            items: [{
                y: 131,
                allowBlank: false,
                //fieldLabel: '用户名',
                name: 'account',
                emptyText: '用户名',
                blankText: '用户名不能为空',
                value: Ext.util.Cookies.get('username') || '',
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == e.ENTER) {
                            field.up('form').getForm().findField('password').focus(true, true);
                        }
                    },
                    afterrender: function (field) {
                        field.focus(true, true);
                    }
                }
            }, {
                y:197,
                allowBlank: false,
                //fieldLabel: '密码',
                name: 'password',
                emptyText: '密码',
                inputType: 'password',
                blankText: '密码不能为空',
                //value:'123',
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == e.ENTER) {
                            field.up('form').getForm().findField('verifycode').focus(true, true);
                        }
                    }
                }
            }, {
                y:262,
                width:146,
                allowBlank: false,
                //fieldLabel: '验证码',
                name: 'verifycode',
                emptyText: '验证码',
                blankText: '验证码不能为空',
                listeners: {
                    specialkey: function (field, e) {
                        if (e.getKey() == e.ENTER) {
                            Ext.getElementById('nwLoginBtn').click();
                        }
                    }
                }
            },{
                id:'nwLoginImage',
                x:458,
                y:262,
                xtype: 'image',
                src:'/code/image?'+new Date().getTime(),
                width: 150,
                focusable:true
            },{
                x:618,
                y:272,
                xtype:'button',
                width:60,
                height:25,
                text:'换一张',
                listeners: {
                    click: function(){
                        Ext.getElementById('nwLoginImage').src='/code/image?'+new Date().getTime();
                    }
                }
            },{
                id:'nwLoginBtn',
                y:314,
                xtype:'button',
                width:140,
                height:52,
                border:false,
                style: 'background: url("resources/css/images/btn.png") no-repeat;',
                handler: 'onLogon'
            },{
                y:314,
                x:458,
                xtype:'button',
                width:140,
                height:52,
                border:false,
                style: 'background: url("resources/css/images/btn-2.png") no-repeat;',
                handler: 'onReset'
            }]
        });
        var D = document;
        this.items = {
            xtype:'panel',
            width: Math.max(D.body.scrollWidth, D.documentElement.scrollWidth),
            height: Math.max(D.body.scrollHeight, D.documentElement.scrollHeight),
            frame: false,
            border:false,
            layout: 'center',
            items:lf
        };
        this.callParent();
    },
    controller: 'main',
    viewModel: {
        type: 'main'
    },
    layout: {
        type: 'border'
    },
    defaults: {
        //hidden: true,
        //frame: true
    },
    loadMainAppItems:function(apps){

        return [{
            id: 'nwTopPanel',
            xtype: 'maintop',
            region: 'north'
        }, {
            id: 'nwBottomPanel',
            xtype: 'mainbottom',
            region: 'south'
        }, {
            id: 'nwWestPanel',
            xtype: 'treepanel',
            collapsible: true,
            useArrows: true,
            bind: {
                title: '{leftName}'
            },
            region: 'west',
            width: 250,
            split: true,
            rootVisible: false,
            store: new Ext.data.TreeStore({
                root: {
                    text: 'root',
                    expanded: true,
                    children: apps
                }
            }),
            listeners: {
                //itemdblclick: 'onItemDBClick',
                itemclick: 'onItemDBClick',
                afterrender:function(tr){
                    tr.expandAll();
                }
            }
        }, {
            id: 'nwMainTabPanel',
            region: 'center',
            xtype: 'mytabpanel',
            plugins: {
                ptype: 'tabclosemenu'
            },
            items: [{
                title: '首页',
                closable: false,
                padding: 10,
                html: $user.username+', 欢迎您使用后台管理系统,当前时间为 '+new Date().toLocaleString()
                //xtype: 'app-dashboard'
            }]
        }];
    }
});