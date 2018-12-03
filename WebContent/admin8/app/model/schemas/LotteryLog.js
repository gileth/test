Ext.define('app.model.schemas.LotteryLog', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.LotteryLog',
    name: '开奖记录',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'string', name: '期号',allowBlank:false,queryable:true},
        {id: 'luckyNumber', type: 'string', name: '开奖号码'},
        {id: 'createDate', type: 'datetime', name: '抢包时间'}

    ],
    singleton: true
});