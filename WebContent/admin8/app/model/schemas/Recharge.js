Ext.define('app.model.schemas.Recharge', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubRecharge',
    name: '充值记录',
    orderInfo: 'id desc',
    items: [
        {id: 'startTime', type: 'date', name: '开始', hidden:true,queryable:true},
        {id: 'endTime', type: 'date', name: '结束', hidden:true,queryable:true},
        {id: 'chargeTimes', type: 'string', name: '次数范围', hidden:true,queryable:true},
        {id: 'queryFee', type: 'string', name: '金额范围', hidden:true,queryable:true},
        {id: 'id', type: 'int', name: 'ID'},
        {id: 'uid', type: 'int', name: '用户ID',width:140, queryable:true},
        {id: 'userIdText', type: 'string', name: '账号',width:140, allowBlank:false},

        {id: 'tradeno', type: 'string', name: '订单号',width:200,allowBlank:false},
        {id: 'fee', type: 'double', name: '充值金额',allowBlank:false},
        {id: 'tradetime', type: 'datetime', name: '申请时间',width:160},
        {id: 'finishtime', type: 'datetime', name: '充值时间',width:160},
        {id: 'payno', type: 'string', name: '支付流水号',width:160},
        {id: 'status', type: 'string', name: '订单状态',dic:'dic.chat.rechargeStatus'}
    ],
    singleton: true
});