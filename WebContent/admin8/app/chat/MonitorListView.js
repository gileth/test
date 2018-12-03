Ext.define('app.chat.MonitorListView', {
    extend: 'app.ux.MyListView',
    export:false,


    doClear: function () {
        Ext.MessageBox.confirm('确认清除缓存', '确认要清理所有的监控数据吗？', function (txt) {
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

    doClear10: function () {
        Ext.MessageBox.confirm('确认清除缓存', '确认要清楚10分钟前的数据？', function (txt) {
            if ('yes' == txt) {
                jsonRequest.execute({
                    service:this.listService,
                    method:'clear10',
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
    }
})