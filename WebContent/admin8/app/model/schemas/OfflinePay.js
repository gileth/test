Ext.define('app.model.schemas.OfflinePay', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubOfflinepay',
    name: '手动充值设置',
    items: [
        {id: 'payWay', type: 'string', name: '支付方式',width:120,allowBlank:false,pkey:true},
        {id: 'payName', type: 'string', name: '支付名称',width:120,allowBlank:false},
        {id: 'subName', type: 'string', name: '支行名称',width:160},
        {id: 'inName', type: 'string', name: '收款户名',width:200,allowBlank:false},
        {id: 'inAccount', type: 'string', name: '收款帐号',width:200,allowBlank:false,},
        {id: 'logo', type: 'string', name: 'logo',hidden:true},
        {id: 'qrcode', type: 'string', name: '二维码',width:120}
    ],
    singleton: true
});