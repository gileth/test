Ext.define('app.model.schemas.PcRateConfig', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PcRateConfig',
    name: 'PC蛋蛋赔率',
    orderInfo:'alias',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true},
        {id: 'param', type: 'string', name: '参数',width:140, allowBlank:false,update:false,queryable:true,hidden:true},
        {id: 'alias', type: 'string', name: '参数分类',width:180,allowBlank:false,queryable:true,readOnly:true},
        {id: 'val', type: 'string', name: '赔率',width:200,flex:'1',colspan:2,allowBlank:false},
        {id: 'info', type: 'string', name: '说明',width:400,flex:'1',colspan:2,allowBlank:false}
    ],
    singleton: true
});