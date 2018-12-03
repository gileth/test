Ext.define('app.chat.UserListView', {
    extend: 'app.ux.MyListView',
    export:false,

    beforeStoreLoad: function (st, op) {
        for(var i=0;i<this.dicColumns.length;i++){
            this.getDicStore(this.dicColumns[i])
        }

        var p = st.proxy;
        var parameters = {};
        var cfg = op.getConfig();
        parameters[p.pageParam] = cfg[p.pageParam];
        //parameters[p.pageParam] = this.export;
        parameters[p.startParam] = cfg[p.startParam];
        parameters[p.limitParam] = cfg[p.limitParam];
        parameters['entityName'] = this.schema.mapping || this.schema.id;
        if (this.cnd) {
            parameters['cnd'] = this.cnd;
        }
        if(this.schema.orderInfo){
            parameters['orderInfo'] = this.schema.orderInfo;
        }
        if(this.orderInfo){
            parameters['orderInfo'] = this.orderInfo;
        }
        p.setExtraParam('parameters', parameters);
    },

    doExport:function(){

    },

    doMoneyOrder:function(){
        this.orderInfo ="money desc";
        this.doQuery();
        //this.orderInfo =null;
    },

    doScoreOrder:function(){
        this.orderInfo ="score desc";
        this.doQuery();
        //this.orderInfo =null;
    },

    doPointOrder:function(){
        this.orderInfo ="point desc";
        this.doQuery();
        //this.orderInfo =null;
    },

    doQq:function(){
        this.orderInfo ="qq desc";
        this.doQuery();
        //this.orderInfo =null;
    },

    doAlipay:function(){
        this.orderInfo ="alipay desc";
        this.doQuery();
        //this.orderInfo =null;
    },

    doChargeAmountOrder:function(){
        this.orderInfo ="chargeAmount desc";
        this.doQuery();
        //this.orderInfo =null;
    },

    doRegistDateOrder:function(){
        this.orderInfo ="registDate desc";
        this.doQuery();
        //this.orderInfo =null;
    },

    doLoginDateOrder:function(){
        this.orderInfo ="lastLoginDate desc";
        this.doQuery();
        //this.orderInfo =null;
    }
})