Ext.define('app.model.schemas.ManualMoneyLog', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.ManualMoneyLog',
    name: '手工加钱记录',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true},
        {id: 'userId', type: 'int', name: '用户ID', display:1, queryable:true},
        {id: 'userIdText', type: 'string', name: '账号',width:140, allowBlank:false, queryable:true},
        {id: 'money', type: 'double', name: '金额'},
        {id: 'des', type: 'string', name: '说明'},
        {id: 'operator', type: 'string', name: '操作人',display:"1"},
        {id: 'createTime', type: 'datetime', name: '操作时间',display:"1",width:200}
    ],
    singleton: true
});