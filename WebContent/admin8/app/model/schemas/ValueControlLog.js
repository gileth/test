Ext.define('app.model.schemas.ValueControlLog', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.ValueControlLog',
    name: '控制',
    orderInfo: 'id desc',
    items: [
        {id: 'roomId', type: 'string', name: '房间ID',width:140, allowBlank:false, queryable:true},
        {id: 'roomName', type: 'string', name: '房间名称'},
        {id: 'uid', type: 'int', name: '用户ID',queryable:true},
        {id: 'nickName', type: 'string', name: '账号',queryable:true},
        {id: 'val', type: 'string', name: '数值'},
        {id: 'createDate', type: 'date', name: '时间',width:200},
        {id: 'admin', type: 'string', name: '操作人'}
    ],
    singleton: true
});