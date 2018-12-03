Ext.define('app.model.schemas.PubExchangeLog', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubExchangeLog',
    name: '商品兑换记录',
    items: [
        {id: 'id', type: 'int', name: 'Id',pkey:"true",hidden:true},
        {id: 'shopId', type: 'int', name: '商品Id',hidden:true},

        {id: 'shopName', type: 'string', name: '商品名称',colspan:2,width:200,readOnly:true},
        {id: 'money', type: 'double', name: '金币',readOnly:true},

        {id: 'uid', type: 'int', name: '用户ID',display:1,queryable:true},

        {id: 'nickName', type: 'string', name: '账号',readOnly:true,queryable:true},
        {id: 'name', type: 'string', name: '收货人姓名',queryable:true},
        {id: 'mobile', type: 'string', name: '收货人电话',queryable:true},

        {id: 'address', type: 'string', name: '收件地址',width:200,colspan:3},
        {id: 'dealInfo', type: 'string', name: '发货信息(快递)',width:200,colspan:2},
        {id: 'status', type: 'string', name: '处理状态',dic:"dic.chat.dealStatus"},

        {id: 'exchangeTime', type: 'date', name: '兑换时间',display:1},
        {id: 'dealTime', type: 'date', name: '处理时间',display:1},
        {id: 'admin', type: 'string', name: '管理员',display:1},
    ],
    singleton: true
});