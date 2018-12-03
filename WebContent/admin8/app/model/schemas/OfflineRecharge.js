Ext.define('app.model.schemas.OfflineRecharge', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubRecharge',
    name: '手动充值',
    orderInfo: 'status asc,finishtime desc ',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true},
        {id: 'uid', type: 'int', name: '用户ID',width:140, hidden:true},
        {id: 'userIdText', type: 'string', name: '用户',width:140, allowBlank:false,  queryable:true,readOnly:true},
        {id: 'tradeno', type: 'string', name: '支付订单号',width:200,allowBlank:false, queryable:true,display:1},
        {id: 'rechargetype', type: 'string', name: '充值类型',allowBlank:false,dic:'dic.lottery.rechargeType',display:"0"},
        {id: 'rechargecode', type: 'string', name: '随机码',allowBlank:false, queryable:true,readOnly:true},
        {id: 'goodsname', type: 'string', name: '支付方式',allowBlank:false,readOnly:true},
        {id: 'fee', type: 'double', name: '充值金额',allowBlank:false},
        {id: 'gift', type: 'double', name: '赠送金额'},
        {id: 'descpt', type: 'string', name: '活动描述'},
        {id: 'tradetime', type: 'datetime', name: '支付时间',width:160,queryable:true,display:1},
        {id: 'finishtime', type: 'datetime', name: '完成时间',width:160,value:new Date(),display:1},
        {id: 'status', type: 'string', name: '订单状态',dic:'dic.lottery.rechargeStatus',display:1},
        {id: 'operator', type: 'string', name: '操作员',display:1}
    ],
    singleton: true
});