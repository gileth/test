Ext.define('app.model.schemas.GameLog', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PcGameLog',
    name: '游戏记录',
    orderInfo: 'id desc',
    items: [

        {id: 'id', type: 'string', name: 'ID', display:0},
        {id: 'num', type: 'string', name: '期号',allowBlank:false,queryable:true},
        {id: 'betType', type: 'string', name: '下注类型',allowBlank:false,display:0,dic:'dic.pc.betType'},
        {id: 'bet', type: 'string', name: '下注内容',allowBlank:false,dic:'dic.pc.betKey'},
        {id: 'luckyNumber', type: 'string',allowBlank:false, name: '开奖号码'},
        {id: 'uid', type: 'int',allowBlank:false, name: '用户Id',queryable:true},
        {id: 'userId', type: 'string',allowBlank:false, name: '账号',queryable:true},

        {id: 'freeze', type: 'double', name: '冻结金额'},
        {id: 'bonus', type: 'double', name: '奖金'},
        {id: 'userInout', type: 'double', name: '盈亏'},
        {id: 'addBack', type: 'double', name: '回家金额',display:0},
        {id: 'backMoney', type: 'double', name: '回水'},

        {id: 'betTime', type: 'datetime', name: '下注时间',queryable:true},
        {id: 'openTime', type: 'datetime', name: '开奖时间',queryable:true},
        {id: 'status', type: 'string', name: '状态',dic:'dic.pc.lotteryStatus',queryable:true}
    ],
    singleton: true
});