Ext.define('app.model.schemas.ValueControl', {
    extend: 'app.model.schemas.Base',
    name: '控制',
    items: [
        {id: 'roomId', type: 'string', name: '房间ID',width:140, allowBlank:false, queryable:true},
        {id: 'uid', type: 'int', name: '用户ID',width:140},
        {id: 'value', type: 'string', name: '数值',width:200}
    ],
    singleton: true
});