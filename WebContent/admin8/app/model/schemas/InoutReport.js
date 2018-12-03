Ext.define('app.model.schemas.InoutReport', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.InoutReport',
    name: '盈亏统计',
    orderInfo: 'id desc',
    items: [

        {id: 'startDate', type: 'date', name: '开始时间', hidden:true ,queryable:true},
        {id: 'endDate', type: 'date', name: '结束时间', hidden:true ,queryable:true},
        {id: 'userId', type: 'string', name: '用户名',queryable:true},

        {id: '12', type:'string',name:'下注'},
        {id: '13', type:'string',name:'充值'},
        {id: '14', type:'string',name:'提现'},
        
        {id: '16', type:'string',name:'余额宝利息'},
        {id: '17', type:'string',name:'代理下注返点'},
        {id: '21', type:'string',name:'自己下注返点'},
        {id: '18', type:'string',name:'下级首充返现'},
        {id: '19', type:'string',name:'奖金'},
        {id: '20', type:'string',name:'盈亏'},
        {id: '30', type:'string',name:'注册赠送活动'},
        {id: '31', type:'string',name:'充值返点活动'},
        
        {id: '51', type:'string',name:'签到'},
        {id: '52', type:'string',name:'登录'},
        {id: '53', type:'string',name:'兑换'},
        {id: '54', type:'string',name:'抽奖'},
        {id: '55', type:'string',name:'首次充值赠送积分'},
        {id: '56', type:'string',name:'下注积分'},
        {id: '57', type:'string',name:'充值积分返点'},

    ],
    singleton: true
});