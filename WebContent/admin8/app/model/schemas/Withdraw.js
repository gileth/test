Ext.define('app.model.schemas.Withdraw', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubWithdraw',
    name: '提现记录',
    orderInfo:'status , id desc',
    items: [
        {id: 'id', type: 'int', name: 'ID', display:1},
        {id: 'status', type: 'string', name: '订单状态',dic:'dic.chat.withdrawStatus'},
        {id: 'userIdText', type: 'string', name: '账号',width:140, allowBlank:false, queryable:true,readOnly:"true"},
        {id: 'uid', type: 'string', name: '用户ID',width:140, allowBlank:false,display:1,queryable:true},
        {id: 'fee', type: 'double', name: '提现金额',allowBlank:false},

        {id: 'bankName', type: 'string', name: '银行名称',readOnly:"true"},
        {id: 'account', type: 'string', name: '银行卡号/账号',readOnly:"true",width:300},
        {id: 'branch', type: 'string', name: '分支',readOnly:"true"},
        {id: 'ownerName', type: 'string', name: '姓名',readOnly:"true"},
        {id: 'mobile', type: 'string', name: '手机号码',readOnly:"true"},

        {id: 'tradetime', type: 'datetime', name: '申请提现时间',width:160,queryable:true,readOnly:"true"},
        {id: 'finishtime', type: 'datetime', name: '提现成功时间',width:160,queryable:true,value:new Date()},
        {id: 'payno', type: 'string', name: '提现流水号',hidden:true},

        {id: 'descpt', type: 'string', name: '后台备注',width:300}
    ],
    singleton: true
});