/**
 * 系统主页的底部区域，主要放置用户单位信息，服务单位和服务人员信息
 */
Ext.define('app.view.main.region.Bottom', {

    extend: 'Ext.toolbar.Toolbar',

    alias: 'widget.mainbottom',
    uses : ['app.ux.ButtonTransparent'],

    defaults : {
        xtype : 'buttontransparent'
    },

    style : 'background-color : #f6f5ec;',

    items : [{
        bind : {
            text : '{user.username}'
        },
        iconCls : 'fa fa-user'
    }, {
        bind : {
            text : '{user.organname}'
        },
        iconCls : 'fa fa-sitemap'
    }, '->',{
        bind : {
            //text : '{service.phonenumber}'
			text : ''
        },
        iconCls : 'fa fa-phone-square'
    }, {
        bind : {
            hidden : '{!service.email}', // 绑定值前面加！表示取反，如果有email则不隐藏，如果email未设置，则隐藏
			text : ''
            //text : '{service.email}'
        },
        iconCls : 'fa fa-envelope-o',
        handler : ''
		/**
		handler : function(button) {
            // 发送邮件
            var vm = button.up('app-main').getViewModel();
            var link = "mailto:" + vm.get('service.email')
                + "?subject=" + vm.get('user.username') + " 关于 "
                + vm.get('system.name') + " 的咨询";
            window.location.href = link;
        }
		 */
    }, {
        bind : {
            text : '©'
        }
    }]
});