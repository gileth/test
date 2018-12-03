Ext.define('app.model.schemas.LotteryRecord', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.GcLottery',
    name: '发包记录',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'string', name: '红包ID', allowBlank:false,width:200},
        {id: 'sender', type: 'int', name: '用户Id',allowBlank:false,queryable:true},
        {id: 'money', type: 'string', name: '金额', allowBlank:false},
        {id: 'number', type: 'int', name: '包个数', allowBlank:false},
        {id: 'roomId', type: 'string', name: '房间号', allowBlank:false},
        {id: 'createTime', type: 'date', name: '发包时间',width:200},
        {id: 'description', type: 'string', name: '描述'},
        {id: 'title', type: 'string', name: '结果'},
        {id: 'status', type: 'string', name: '状态', dic:'dic.chat.lotteryStatus'}
    ],
    singleton: true
});