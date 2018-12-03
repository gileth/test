Ext.define('app.model.schemas.ProxyRecharge', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubRecharge',
    name: '充值记录',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'int', name: 'ID'},
        {id: 'uid', type: 'int', name: '用户ID',width:140, queryable:true},
        {id: 'userIdText', type: 'string', name: '账号',width:140, allowBlank:false},
        {id: 'fee', type: 'double', name: '充值金额',allowBlank:false},
        {id: 'tradetime', type: 'datetime', name: '申请时间',width:160},
        {id: 'rechargeType', type: 'string', name: '操作类型',allowBlank:false,dic:'dic.chat.rechargeType'},
        {id: 'operator', type: 'string', name: '代理ID',width:160}
    ],
    singleton: true
});