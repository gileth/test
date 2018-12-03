Ext.define('app.model.schemas.GameReport', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.GameReport',
    name: '游戏统计信息',
    orderInfo: 'id desc',
    items: [

        {id: 'betType', type: 'string', name: '玩法',dic:"lottery.betType", allowBlank:false},

        {id: 'betCount', type: 'string', name: '下注量',display:"1" },
        {id: 'game300', type: 'string', name: '牛牛',display:"1" },
        {id: 'groupNum', type: 'string', name: '组三组六',display:"1" },
        {id: 'special', type: 'string', name: '特殊号码',display:"1" },
        {id: 'catchTime', type: 'datetime', name: '抓取时间',display:"1",width:200}

    ],
    singleton: true
});