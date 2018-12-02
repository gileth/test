<?xml version="1.0" encoding="UTF-8"?>
<app name="系统管理" iconCls="fa fa-desktop">
	<catagory id="CATA_USER" name="后台用户管理" iconCls="fa fa-users">
        <module id="system_user" name="系统用户" script="app.ux.MyListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">User</p>
                <p name="listService">authorityService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="read" name="查看" iconCls="fa fa-search-plus"/>
            <action id="create" name="增加" iconCls="fa fa-plus"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
            <action id="delete" name="删除" iconCls="fa fa-trash-o"/>
        </module>
        <module id="system_token" name="权限管理" script="app.ux.MyListView" iconCls="fa fa-key">
            <properties>
                <p name="entityName">UserRoleToken</p>
                <p name="enableRowNumber">true</p>
            </properties>
            <action id="read" name="查看" iconCls="fa fa-search-plus"/>
            <action id="create" name="增加" iconCls="fa fa-plus"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
            <action id="delete" name="删除" iconCls="fa fa-trash-o"/>
        </module>
	</catagory>
</app>
