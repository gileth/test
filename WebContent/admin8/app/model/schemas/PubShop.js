Ext.define('app.model.schemas.PubShop', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubShop',
    name: '用户',
    items: [
        {id: 'id', type: 'int', name: 'Id',pkey:"true",hidden:true},

        {id: 'name', type: 'string', name: '商品名称',colspan:2,allowBlank:false,width:200},
        {id: 'money', type: 'double', name: '金币价格',allowBlank:false},

        {id: 'storage', type: 'int', name: '库存',allowBlank:false},
        {id: 'sortNum', type: 'int', name: '排序',allowBlank:false},
        {id: 'exchanged', type: 'int', name: '已兑换数',allowBlank:false},

        {id: 'summary', type: 'string', name: '商品概述',allowBlank:false,colspan:3,width:200},
        {id: 'listImg', type: 'string', name: '列表图标(160*160)',allowBlank:false,colspan:3,width:200},
        {id: 'img1', type: 'string', name: '图片1(300*200)',colspan:3,width:200},
        {id: 'img2', type: 'string', name  : '图片2(300宽度)',colspan:3,width:200},
        {id: 'img3', type: 'string', name: '图片3(300宽度)',colspan:3,width:200},
        {id: 'createUser', type: 'string', name: '管理员',display:1},
        {id: 'createDate', type: 'date', name: '时间',display:1},
        {id: 'detail', type: 'string', name: '详细说明',width:280,colspan:"3",xType: 'htmleditor'}
    ],
    singleton: true
});