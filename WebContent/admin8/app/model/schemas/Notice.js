Ext.define('app.model.schemas.Notice', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.Notice',
    name: '群发消息',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'int', name: 'ID', display:"1"},
        //{id: 'title', type: 'string', name: '标题', allowBlank: false, width: 180, colspan: 3},
        {id: 'createDate', type: 'datetime', name: '时间', width: 160, value: new Date(),display:"1"},
        {id: 'content', type: 'string', name: '内容', allowBlank: false, width: 280, colspan: 2}
        //{id: 'content', type: 'string', name: '内容', allowBlank: false, width: 280, colspan: 3, xType: 'htmleditor',hidden: true}
    ],
    singleton: true
});