Ext.define('app.model.schemas.GcWaterLog', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.GcWaterLog',
    name: '红包抽水日志',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'string', name: 'ID', display:0},
        {id: 'uid', type: 'int',allowBlank:false, name: '用户Id',queryable:true},
        {id: 'userId', type: 'string',allowBlank:false, name: '账号',queryable:true},
        {id: 'parentId', type: 'int',allowBlank:false, name: '上级ID',queryable:true},
        {id: 'parentUserId', type: 'string',allowBlank:false, name: '上级账号',queryable:true},
        {id: 'roomId', type: 'string', name: '房间号'},
        {id: 'lotteryId', type: 'string', name: '红包ID'},
        {id: 'gameType', type: 'string',name: '游戏类型',dic:'dic.chat.gameType'},
        {id: 'fullWater', type: 'double', name: '抽水金额'},
        {id: 'water', type: 'double', name: '返点金额'},
        {id: 'createDate', type: 'date', name: '抽水时间',queryable:true},
    ],
    singleton: true
});