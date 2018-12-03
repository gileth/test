Ext.define('app.model.schemas.RoomFeeLog', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.RoomFeeLog',
    name: '下分日志',
    orderInfo: 'id desc',
    items: [
        {id: 'roomId', type: 'string', name: '房间ID',width:140, allowBlank:false, queryable:true},
        {id: 'roomName', type: 'string', name: '房间名字',width:200,display:1},
        {id: 'val', type: 'double', name: '数值',width:200},
        {id: 'createDate', type: 'date', name: '日期',width:200,display:1},
        {id: 'admin', type: 'string', name: '操作人',width:200,display:1}
    ],
    singleton: true
});