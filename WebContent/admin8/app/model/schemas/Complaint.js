Ext.define('app.model.schemas.Complaint', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubComplaint',
    name: '系统公告',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'long', name: 'ID', hidden: true},
        {id: 'uid', type: 'string', name: '用户ID', hidden: true},
        {id: 'userIdText', type: 'string', name: '用户',queryable:true, readonly:true},
        {id: 'contact', type: 'string', name: '联系方式', readonly:true},
        {id: 'qq', type: 'string', name: 'QQ', readonly:true},
        {id: 'createTime', type: 'datetime', name: '时间',width:120, value: new Date(),readonly:true},

        {id: 'title', type: 'string', name: '标题',colspan: 2,width:120, readonly:true},
        {id: 'content', type: 'string', name: '内容',colspan: 3,width:200, readonly:true},
        {id: 'replay', type: 'string', name: '回复',width:150,colspan: 3}
    ],
    singleton: true
});