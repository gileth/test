Ext.define('app.model.schemas.Summary', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.Summary',
    name: '充值记录',
    items: [

        {id: 'startTime', type: 'date', name: '开始', hidden:true,queryable:true},
        {id: 'endTime', type: 'date', name: '结束', hidden:true,queryable:true},
        //充值数 赠送余额数 提款数
        {id: 'name', type: 'string', name: '项目',width:200},
        //新增积分总数  消费积分总数
        {id: 'value', type: 'string', name: '统计',width:800},
        //新增数 登录数 下注数 充值个数
        //吃进  赔付
    ],
    singleton: true
});