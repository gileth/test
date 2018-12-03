Ext.define('app.model.schemas.UserRoleToken', {
    extend: 'app.model.schemas.Base',
    mapping: 'org.takeback.core.user.UserRoleToken',
    name: '用户',
    items: [
        {id: 'id', type: 'int', name: 'ID', hidden:true},
        {id: 'userid', type: 'string', name: '用户',dic:'dic.users'},
        {id: 'roleid', type: 'string', name: '角色',dic:'dic.roles'},
        {id: 'organid', type: 'string', name: '机构',dic:'dic.units'}
    ],
    singleton: true
});