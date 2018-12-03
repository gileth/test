Ext.define('app.model.schemas.Bet', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.lottery.entity.BetRecord',
    name: '下注记录',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true},
        {id: 'userId', type: 'int', name: '用户ID',display:1, queryable:true},
        {id: 'userIdText', type: 'string', name: '账号',width:140, allowBlank:false, queryable:true},
        {id: 'sscNum', type: 'string', name: '下注期号',width:140, queryable:true},
        {id: 'gameType', type: 'string', name: '游戏种类',dic:'dic.lottery.gameType'},
        {id: 'betType', type: 'string', name: '游戏类型',dic:'dic.lottery.betType', queryable:true},
        {id: 'bet', type: 'string', name: '下注内容',width:200},
        {id: 'num', type: 'int', name: '注数'},
        {id: 'price', type: 'double', name: '单注价格'},
        {id: 'total', type: 'double', name: '总价'},
        {id: 'betTime', type: 'datetime', name: '下注时间',width:160},

        {id: 'status', type: 'string', name: '状态'},
        {id: 'freeze', type: 'double', name: '冻结金额'},
        {id: 'luckyNum', type: 'string', name: '中奖号码'},
        {id: 'userInout', type: 'double', name: '本轮输赢'},
        {id: 'bonus', type: 'double', name: '中奖奖金'},
        {id: 'openNum', type: 'string', name: '开奖号码'},
        {id: 'openTime', type: 'datetime', name: '开奖时间'}
    ],
    singleton: true
});