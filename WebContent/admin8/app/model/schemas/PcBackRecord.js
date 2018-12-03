Ext.define('app.model.schemas.PcBackRecord', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PcBackRecord',
    name: 'PC蛋蛋回水记录',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'string', name: 'ID', display:0},
        {id: 'uid', type: 'int',allowBlank:false, name: '用户Id',queryable:true},
        {id: 'userId', type: 'string',allowBlank:false, name: '账号',queryable:true},
        {id: 'money', type: 'double', name: '回水金额'},
        {id: 'userInout', type: 'double', name: '当天盈亏'},
        {id: 'backDate', type: 'datetime', name: '下注时间',queryable:true},
    ],
    singleton: true
});