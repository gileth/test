Ext.define('app.model.schemas.WebsiteUser4Pwd', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.chat.entity.PubUser',
    name: '密码修改',
    orderInfo: 'id desc',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true},
        {id: 'userId', type: 'string', name: '帐号',width:120,allowBlank:false,readOnly:true,queryable:true,},
        {id: 'pwd', type: 'string', name: '密码',allowBlank:false,display:"2"},
        {id: 'nickName', type: 'string', name: '昵称',display:"1",},
        {id: 'mobile', type: 'string', name: '手机号码',width:120,readOnly:true,display:"1",queryable:true,},
        {id: 'money', type: 'double', name: '余额',display:"1"},
        {id: 'point', type: 'double', name: '返点数',display:"1"},
        {id: 'subPoint', type: 'double', name: '默认下级点数',display:"1"},
        {id: 'wx', type: 'string', name: '微信号'},
        {id: 'score', type: 'int', name: '积分',display:"1"},
        {id: 'chargeAmount', type: 'double', name: '最高提现金额',display:"1"},
        {id: 'email', type: 'string', name: '电子邮件',width:140,display:"1"},
        {id: 'qq', type: 'string', name: 'QQ',display:"1"},
        {id: 'invitor', type: 'string', name: '邀请人Id',display:"1"},
        {id: 'registIp', type: 'string', name: '注册IP',display:"1"},
        {id: 'registDate', type: 'datetime', name: '注册时间',width:160,queryable:true,display:"1"},
        {id: 'lastLoginIp', type: 'string', name: '最后登录IP',display:"1"},
        {id: 'lastLoginDate', type: 'datetime', name: '最后登录时间',width:160,display:"1"},
        {id: 'userType', type: 'string', name: '用户类型',dic:'dic.lottery.userType',display:"1"},
        {id: 'status', type: 'string', name: '是否可用',dic:'dic.accountStatus',display:"1"}
    ],
    singleton: true
});