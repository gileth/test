Ext.define('app.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'Ext.window.MessageBox',
        'app.view.main.MD5'
        //'app.view.frame.dashboard.DashBoard'
    ],

    alias: 'controller.main',

    onItemDBClick: function (p, r, i, idx, e) {
        if(r.data['leaf'] != true){
            return;
        }
        if(r.data['level'] != 'module'){
            return;
        }
        this.loadModule(r.data['id']);
    },

    loadModule :function(moduleId){
        Ext.Ajax.request({
            url: '*.app?moduleId=' + moduleId,
            success: function(response, opts) {
                var obj = Ext.decode(response.responseText);
                if(obj){
                    var script = obj['script'];
                    if(script){
                        Ext.require(script, function(){
                            var tabpanel = this.getView().down('tabpanel');
                            var panel = tabpanel.getModule(moduleId);
                            if(panel){
                                tabpanel.setActiveTab(panel);
                            }else{
                                var cfg = {
                                    title: obj['name'],
                                    actions: obj['actions'],
                                    listeners: {
                                        destroy: {
                                            fn: function(p){
                                                tabpanel.removeModule(p);
                                                if(p.onDestroy){
                                                    p.onDestroy();
                                                }
                                            }
                                        }
                                    }
                                };
                                Ext.apply(cfg, obj['properties']);
                                panel = Ext.create(script, cfg);
                                tabpanel.add(panel);
                                tabpanel.addModule(moduleId, panel);
                            }
                            tabpanel.setActiveTab(panel);
                        }, this);
                    }
                }
            },
            scope: this
        });
    },

    onLogon: function () {
        var lf = Ext.getCmp('nwLogonForm');
        if (lf.isValid()) {
            var data = {}
            data.account =lf.getForm().getValues().account;
            data.password =Ext.create('app.view.main.MD5').hex_md5(lf.getForm().getValues().password);
            data.verifycode =lf.getForm().getValues().verifycode;
            Ext.Ajax.request({
                url: '/logon/loadRoles',
                method: 'post',
                //headers: {'Content-Type': 'application/json'},
                jsonData: data,
                callback: function (p, b, r) {
                    var rt = Ext.decode(r.responseText);
                    if (rt.code != 200) {
                        Ext.Msg.alert('提醒', rt.msg);
                    } else {
                        var token = rt.body;
                        Ext.Ajax.request({
                            url: '/logon/loadApps',
                            method: 'post',
                            callback: function (p, b, r) {
                                var rp = Ext.decode(r.responseText);
                                if (rp.code != 200) {
                                    Ext.Msg.alert('提醒', rp.msg);
                                }else{
                                    $user = token;
                                    var apps = rp.body;
                                    var mainApp = lf.mainApp;
                                    mainApp.down('panel').destroy();
                                    mainApp.items = mainApp.loadMainAppItems(apps);
                                    mainApp.superclass.initComponent.call(mainApp);
                                    Ext.util.Cookies.set('username', data.account);

                                    mainApp.viewModel.data.system.name = $user.system;
                                    mainApp.viewModel.data.system.version = $user.version;
                                    mainApp.viewModel.data.user.username = $user.username;
                                    mainApp.viewModel.data.user.organname = $user.organname;
                                    mainApp.viewModel.data.user.company = $user.company;
                                    mainApp.viewModel.data.service.phonenumber = $user.telephone;
                                    mainApp.viewModel.data.service.email = $user.email;
                                }
                            }
                        })
                        //if(body && 9 == body.accounttype){
                        //    $user = body;
                        //    console.log($user);
                        //    var mainApp = lf.mainApp;
                        //    mainApp.down('panel').destroy();
                        //    mainApp.items = mainApp.loadMainAppItems();
                        //    mainApp.superclass.initComponent.call(mainApp);
                        //    Ext.util.Cookies.set('username', data.account);
                        //}else{
                        //    //Ext.Msg.alert('check again', 'permission denied');
                        //    Ext.Msg.alert('提醒', '没有权限');
                        //}
                    }
                }
            });
        }
    },
    onReset: function(){
        Ext.getCmp('nwLogonForm').getForm().reset();
    },
    onExit: function () {

    }

});
