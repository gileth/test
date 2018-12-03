Ext.define('app.model.schemas.LoginIp', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.LoginLog',
    name: '登录日志',
    items: [
        {id: 'id', type: 'string', name: 'Id',pkey:"true",strategy:"assigned",allowBlank:false},
        {id: 'userId', type: 'string', name: '用户Id',allowBlank:false,queryable:true},
        {id: 'userName', type: 'string', name: '账号',allowBlank:false,queryable:true},
        {id: 'country', type: 'string', name: '国家',allowBlank:false},
        {id: 'province', type: 'string', name: '省份',allowBlank:false},
        {id: 'city', type: 'string', name: '市',allowBlank:false},
        {id: 'area', type: 'string', name: '区域',allowBlank:false},
        {id: 'ip', type: 'string', name: 'ip地址',allowBlank:false,queryable:true},
        {id: 'loginTime', type: 'date', name: '登录时间',width:140}
    ],
    singleton: true
});