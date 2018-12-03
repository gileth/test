Ext.define('app.chat.RedDetailListView', {
    extend: 'app.ux.MyListView',
    export:false,

    doClear: function () {
        Ext.MessageBox.confirm('确认清除缓存', '将清除所有的红包记录!确认平台任何房间在进行游戏，否则将导致数据出错!', function (txt) {
            if ('yes' == txt) {
                jsonRequest.execute({
                    service:this.listService,
                    method:'clear',
                    parameters:{
                    }
                },function(code,msg,json){
                    if(code ==200) {
                        Ext.MessageBox.alert("提示","数据清理成功！");
                        this.store.reload();
                    }
                },this);
            }
        }, this);
    },

    doClear2: function () {
        Ext.MessageBox.confirm('确认清除缓存', '确定要清除2天前的红包游戏数据吗？', function (txt) {
            if ('yes' == txt) {
                jsonRequest.execute({
                    service:this.listService,
                    method:'clear2',
                    parameters:{
                    }
                },function(code,msg,json){
                    if(code ==200) {
                        Ext.MessageBox.alert("提示","数据清理成功！");
                        this.store.reload();
                    }
                },this);
            }
        }, this);
    },

    doClear5: function () {
        Ext.MessageBox.confirm('确认清除缓存', '确定要清除5天前的红包游戏数据吗？', function (txt) {
            if ('yes' == txt) {
                jsonRequest.execute({
                    service:this.listService,
                    method:'clear5',
                    parameters:{
                    }
                },function(code,msg,json){
                    if(code ==200) {
                        Ext.MessageBox.alert("提示","数据清理成功！");
                        this.store.reload();
                    }
                },this);
            }
        }, this);
    },
})