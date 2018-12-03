Ext.define('app.model.schemas.Bank', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubBank',
    name: '银行卡信息',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true},
        {id: 'userId', type: 'int', name: '用户ID',display:1, queryable:true},
        {id: 'userIdText', type: 'string', name: '账号',width:140, allowBlank:false, queryable:true},
        {id: 'bankName', type: 'string', name: '银行名称',width:140},
        {id: 'branch', type: 'string', name: '分支名称',width:200},
        {id: 'account', type: 'string', name: '账号',width:200},
        {id: 'name', type: 'string', name: '姓名'},
        {id: 'mobile', type: 'string', name: '手机号码'},
        {id: 'createTime', type: 'datetime', name: '绑定事件',width:160}
    ],
    singleton: true
});