Ext.define('app.model.schemas.WebsiteUser4Online', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubUser',
    name: '网站用户',
    orderInfo: 'money desc',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true,queryable:true},
        {id: 'userId', type: 'string', name: '帐号',width:120,allowBlank:false,readOnly:true,queryable:true},
        {id: 'nickName', type: 'string', name: '昵称'},
        {id: 'mobile', type: 'string', name: '手机号码',width:120,queryable:true},
        {id: 'pwd', type: 'string', name: '密码',allowBlank:false,hidden:true},
        {id: 'moneyCode', type: 'string', name: '支付密码',hidden:true},
        {id: 'headImg', type: 'string', name: '头像'},
        {id: 'signture', type: 'string', name: '签名'},
        {id: 'money', type: 'double', name: '余额',readOnly:true},

        {id: 'point', type: 'double', name: '返点数'},
        {id: 'subPoint', type: 'double', name: '默认下级点数'},
        {id: 'alipay', type: 'int', name: '支付宝账户'},
        {id: 'wx', type: 'string', name: '微信号'},
        {id: 'score', type: 'int', name: '积分'},
        {id: 'chargeAmount', type: 'double', name: '最高提现金额'},
        {id: 'email', type: 'string', name: '电子邮件',width:140},
        {id: 'qq', type: 'string', name: 'QQ'},
        {id: 'invitor', type: 'string', name: '邀请人Id'},
        {id: 'registIp', type: 'string', name: '注册IP'},
        {id: 'registDate', type: 'datetime', name: '注册时间',width:160,queryable:true},
        {id: 'lastLoginIp', type: 'string', name: '最后登录IP'},
        {id: 'lastLoginDate', type: 'datetime', name: '最后登录时间',width:160},
        {id: 'userType', type: 'string', name: '用户类型',dic:'dic.chat.userType'},
        {id: 'status', type: 'string', name: '是否可用',dic:'dic.accountStatus'}
    ],
    singleton: true
});