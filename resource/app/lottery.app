<?xml version="1.0" encoding="UTF-8"?>
<app name="网站管理" iconCls="fa fa-desktop">
    <catagory id="CONFIG" name="网站管理" iconCls="fa fa-users">

        <module id="config" name="系统配置" script="app.ux.MyListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">PubConfig</p>
                <p name="listService">configService</p>
            </properties>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
        </module>
        <module id="proxyVote" name="代理赚金" script="app.ux.MyListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">ProxyVote</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
        </module>


        <module id="realUsers" name="用户管理" script="app.chat.UserListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">WebsiteUser</p>
                <p name="listService">userManageService</p>
                <p name="cnd">['or',['eq',['$','userType'],['s','1']],['eq',['$','userType'],['s','3']]]</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="新建" iconCls="fa fa-create"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
            <action id="delete" name="删除" iconCls="fa fa-delete"/>
            <action id="moneyOrder" name="↓余额" />
            <action id="scoreOrder" name="↓积分"/>
            <action id="chargeAmountOrder" name="↓提款"/>
            <action id="registDateOrder" name="↓注册"/>
            <action id="loginDateOrder" name="↓登陆"/>
        </module>


        <module id="proxyUsers" name="代理管理" script="app.chat.UserListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">WebsiteUser</p>
                <p name="listService">userManageService</p>
                <p name="cnd">['eq',['$','userType'],['s','2']]</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="新建" iconCls="fa fa-create"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
            <action id="delete" name="删除" iconCls="fa fa-delete"/>
            <action id="moneyOrder" name="↓余额" />
            <action id="scoreOrder" name="↓积分"/>
            <action id="chargeAmountOrder" name="↓提款"/>
            <action id="registDateOrder" name="↓注册"/>
            <action id="loginDateOrder" name="↓登陆"/>
        </module>

        <module id="robotUsers" name="机器人用户" script="app.chat.UserListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">WebsiteUser</p>
                <p name="listService">userManageService</p>
                <p name="cnd">['eq',['$','userType'],['s','9']]</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="新建" iconCls="fa fa-create"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
            <action id="delete" name="删除" iconCls="fa fa-delete"/>
            <action id="moneyOrder" name="↓余额" />
            <action id="scoreOrder" name="↓积分"/>
        </module>



        <module id="notice" name="群发消息" script="app.ux.MyListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">Notice</p>
                <p name="listService">noticeService</p>
            </properties>
            <action id="create" name="新增" iconCls="fa fa-create"/>
        </module>

        <module id="user_ip" name="登录日志" script="app.ux.MyListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">LoginIp</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="read"  name="查看" iconCls="fa fa-search-plus"/>
        </module>


    </catagory>

    <catagory id="shopCatalog" name="商城管理" iconCls="fa fa-users">
        <module id="shop" name="商品管理" script="app.ux.MyListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">PubShop</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="新建" iconCls="fa fa-create"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
            <action id="delete" name="删除" iconCls="fa fa-delete"/>
        </module>

        <module id="exchangeLog" name="商品兑换管理" script="app.ux.MyListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">PubExchangeLog</p>
                <p name="listService">exchangeAdminService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="update" name="处理" iconCls="fa fa-edit"/>
        </module>
    </catagory>

    <catagory id="RECHARGE_WITHDRAW" name="充值提现管理" iconCls="fa fa-users">
        <module id="userAddMoney" name="人工上下分" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">ManualMoneyLog</p>
                <p name="listService">manualMoneyService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="上下分" iconCls="fa fa-edit"/>
        </module>
        <module id="recharge" name="充值查询" script="app.chat.RechargeListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">Recharge</p>
                <p name="listService">rechargeService</p>
                <p name="cnd">['ne',['$','rechargeType'],['s','1']]</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="read" name="查看" iconCls="fa fa-search-plus"/>
            <action id="timeOrder" name="时间排序" iconCls="fa fa-search-plus"/>
            <action id="moneyOrder" name="金额排序" iconCls="fa fa-search-plus"/>
        </module>

        <module id="proxyRecharge" name="代理上下分" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">ProxyRecharge</p>
                <p name="listService">myListServiceInt</p>
                <p name="cnd">['or',['eq',['$','rechargeType'],['s','2']],['eq',['$','rechargeType'],['s','3']]]</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
        </module>

        <module id="withdraw" name="提现管理" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">Withdraw</p>
                <p name="listService">withdrawAdminService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="read" name="查看" iconCls="fa fa-search-plus"/>
            <action id="update" name="审核" iconCls="fa fa-edit"/>
            <action id="delete" name="删除" iconCls="fa fa-edit"/>
        </module>
        <module id="bankinfo" name="客户提现账号" script="app.ux.MyListView" iconCls="fa fa-cogs">
            <properties>
                <p name="entityName">Bank</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="read" name="查看" iconCls="fa fa-search-plus"/>
        </module>
        <module id="transfer" name="转账记录" script="app.ux.MyListView" iconCls="fa fa-cogs">
            <properties>
                <p name="entityName">Transfer</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
        </module>
    </catagory>

    <catagory id="room" name="房间管理" iconCls="fa fa-users">
        <module id="roomList" name="房间列表" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">Room</p>
                <p name="listService">roomAdminService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="新增" iconCls="fa fa-search-plus"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
            <action id="delete" name="删除" iconCls="fa fa-edit"/>
        </module>
        <module id="roomPropList" name="房间属性管理" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">RoomProp</p>
                <p name="listService">roomPropAdminService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="新增" iconCls="fa fa-search-plus"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
        </module>

        <module id="roomFee" name="服务费下分日志" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">RoomFeeLog</p>
                <p name="listService">roomFeeService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="下服务费" iconCls="fa fa-search-plus"/>
        </module>

        <module id="roomApply" name="开房申请" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">RoomApply</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="delete" name="删除" iconCls="fa fa-edit"/>
        </module>

        <module id="robotList" name="机器人管理" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">RoomRobot</p>
                <p name="listService">robotAdminService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>

            <action id="update" name="修改" iconCls="fa fa-edit"/>
        </module> -->
    </catagory>

    <catagory id="BET" name="红包游戏" iconCls="fa fa-users">

        <module id="lottery" name="发包记录" script="app.chat.RedDetailListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">LotteryRecord</p>
                <p name="listService">redDetailService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="read" name="查看" iconCls="fa fa-search-plus"/>
            <action id="clear" name="一键清理" iconCls="fa fa-delete"/>
            <action id="clear2" name="清理2天前数据" iconCls="fa fa-delete"/>
            <action id="clear5" name="清理5天前数据" iconCls="fa fa-delete"/>
        </module>

        <module id="detail" name="抢包明细" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">LotteryDetail</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="read" name="查看" iconCls="fa fa-search-plus"/>
        </module>

        <module id="gameMonitor1" name="游戏监控（玩家）" script="app.chat.MonitorListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">ControlModel</p>
                <p name="listService">monitorService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="read" name="查看" iconCls="fa fa-edit"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
            <action id="clear" name="重新统计（清理）" iconCls="fa fa-delete"/>
        </module>

        <module id="gameMonitor2" name="游戏监控（机器人）" script="app.chat.MonitorListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">ControlModel4Robots</p>
                <p name="listService">monitorService4Robots</p>
            </properties>
            <action id="read" name="查看" iconCls="fa fa-edit"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
        </module>
<!--
        <module id="lotteryControl" name="点数控制" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">ValueControl</p>
                <p name="listService">controlService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
            <action id="create" name="添加" iconCls="fa fa-create"/>
        </module>
-->
        <module id="controlLog" name="点数控制日志" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">ValueControlLog</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
        </module>
    </catagory>

    <catagory id="PCDD" name="PC蛋蛋" iconCls="fa fa-users">
        <module id="betLog" name="下注明细" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">GameLog</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
        </module>
        <module id="rateControl" name="赔率控制" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">PcRateConfig</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
        </module>
        <module id="pcConfig" name="蛋蛋配置" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">PcParams</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
        </module>
    </catagory>

    <catagory id="caculate" name="统计" iconCls="fa fa-users">
        <!--
        <module id="summaryInfo" name="站点统计" script="app.lottery.SummaryListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">Summary</p>
                <p name="listService">summaryService</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
        </module>
        -->


        <module id="config_online_users" name="在线用户" script="app.ux.MyListView" iconCls="fa fa-user">
            <properties>
                <p name="entityName">WebsiteUser4Online</p>
                <p name="listService">onlineUserService</p>
            </properties>
            <action id="read"  name="查看" iconCls="fa fa-search-plus"/>
            <action id="update" name="修改" iconCls="fa fa-edit"/>
        </module>


<!--
        <module id="game" name="游戏汇总报表" script="app.ux.MyListView" iconCls="fa fa-gift">
            <properties>
                <p name="entityName">GameReport</p>
                <p name="listService">myListServiceInt</p>
            </properties>
            <action id="query" name="搜索" iconCls="fa fa-search"/>
        </module>  -->
    </catagory>

</app>
