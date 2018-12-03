Ext.define('app.model.schemas.ControlModel4Robots', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.ControlModel',
    name: '监控记录',
    items: [
        {id: 'id', type: 'long',readOnly:true, name: 'id',display:1},
        {id: 'roomId', type: 'string',readOnly:true, name: '房间ID',queryable:true},
        {id: 'roomName', type: 'string',display:1, name: '房间名称'},
        {id: 'uid', type: 'int',display:1, name: '用户ID',queryable:true},
        {id: 'userId', type: 'string',readOnly:true, name: '账号'},
        {id: 'win', type: 'double',display:1, name: '赢'},
        {id: 'lose', type: 'double', display:1,name: '输'},
        {id: 'inoutNum', type: 'double',display:1, name: '输赢'},
        {id: 'playTimes', type: 'int',display:1, name: '游戏次数'},
        {id: 'targetRateText', type: 'double', name: '目标赢率(%)'},
        {id: 'currentRateText', type: 'double',display:1, name: '当前盈率'},
        {id: 'lastModifyDate', type: 'date',display:1, name: '最后游戏时间',width:140},
        {id: 'suggests', type: 'string', name: '控制下次开包金额',width:300,colspan:3}
    ],
    singleton: true
});