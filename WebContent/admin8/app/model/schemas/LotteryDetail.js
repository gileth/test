Ext.define('app.model.schemas.LotteryDetail', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.GcLotteryDetail',
    name: '抢包明细',
    orderInfo: 'id desc',
    items: [
        {id: 'uid', type: 'int', name: '用户Id',allowBlank:false,queryable:true},
        {id: 'lotteryid', type: 'string', name: '红包ID', allowBlank:false,queryable:true},
        {id: 'coin', type: 'string', name: '金额', allowBlank:false},

        {id: 'roomId', type: 'string', name: '房间号', allowBlank:false},
        {id: 'gameType', type: 'string', name: '游戏类型', allowBlank:false, dic:'dic.chat.gameType'},
        {id: 'masterId', type: 'string', name: '庄家ID', allowBlank:false},

        {id: 'deposit', type: 'string', name: '押金', allowBlank:false},
        {id: 'addback', type: 'string', name: '回加金额', allowBlank:false},
        {id: 'inoutNum', type: 'string', name: '输赢', allowBlank:false},

        {id: 'createDate', type: 'datetime', name: '抓取时间',display:"1",width:200}

    ],
    singleton: true
});