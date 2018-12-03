Ext.define('app.model.schemas.RoomRobot', {
    extend: 'app.model.schemas.Base',
    name: '房间属性',
    orderInfo: 'createTime desc',
    items: [
        {id: 'id', type: 'string', name: '房间Id',readonly:"true",queryable:"true"},
        {id: 'roomName', type: 'string', name: '房间名称',readonly:"true"},
        {id: 'robotNum', type: 'int', name: '机器人数量'},
    ],
    singleton: true
});