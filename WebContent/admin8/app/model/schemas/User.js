Ext.define('app.model.schemas.User', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.core.user.User',
    name: '用户',
    items: [
        {id: 'id', type: 'string', name: '用户名',pkey:"true",strategy:"assigned",allowBlank:false,queryable:true},
        {id: 'password', type: 'string', name: '密码',allowBlank:false},
        {id: 'name', type: 'string', name: '昵称',allowBlank:false,queryable:true},
        {id: 'phonenumb', type: 'string', name: '手机号码',width:120,queryable:true},
        {id: 'email', type: 'email', name: '电子邮件',width:140,queryable:true},
        {id: 'status', type: 'string', name: '状态',dic:'dic.accountStatus'}
    ],
    singleton: true
});