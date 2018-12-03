Ext.define('app.model.schemas.Transfer', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.TransferLog',
    name: '转账记录',
    items: [
        {id: 'id', type: 'int', name: 'Id',pkey:"true",strategy:"assigned",allowBlank:false},
        {id: 'fromUid', type: 'int', name: '出账人ID',allowBlank:false,queryable:true},
        {id: 'fromNickName', type: 'string', name: '出账人账号',allowBlank:false},
        {id: 'toUid', type: 'int', name: '入账人ID',queryable:true},
        {id: 'toNickName', type: 'email', name: '入账人账号'},

        {id: 'money', type: 'int', name: '转账金额'},

        {id: 'transferDate', type: 'string', name: '转账时间',width:120}
    ],
    singleton: true
});