Ext.define('app.model.schemas.Message', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubMessage',
    name: '站内短信',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true},
        {id: 'userIdText', type: 'string', name: '用户',width:140, allowBlank:false, queryable:true,update:false},
        {id: 'userId', type: 'string', name: '用户ID',width:140,hidden:true},
        {id: 'title', type: 'string', name: '标题',width:200,allowBlank:false,colspan:2},
        {id: 'content', type: 'string', name: '内容',width:400,allowBlank:false,colspan:3},
        {id: 'createTime', type: 'datetime', name: '发送时间',width:160,display:"1"},
        {id: 'createUser', type: 'string', name: '管理员',readOnly:"true",display:"1"},
        {id: 'status', type: 'string', name: '消息状态',dic:'dic.lottery.msgStatus',display:"1"},
    ],
    singleton: true
});