Ext.define('app.chat.RechargeListView', {
    extend: 'app.ux.MyListView',

    beforeStoreLoad: function (st, op) {
        for(var i=0;i<this.dicColumns.length;i++){
            this.getDicStore(this.dicColumns[i])
        }

        var p = st.proxy;
        var parameters = {};
        var cfg = op.getConfig();
        parameters[p.pageParam] = cfg[p.pageParam];
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
        var cnds = this.down('toolbar').items.items;
        var myQuires = {};
        for(var i = 0 ; i < cnds.length ; i++) {
            var cndItem = cnds[i];
            if (cndItem.name == "startTime") {
                myQuires["startTime"] = Ext.util.Format.date(cnds[i].value, 'Y-m-d');
            } else if (cndItem.name == "endTime") {
                myQuires["endTime"] = Ext.util.Format.date(cnds[i].value, 'Y-m-d');
            } else if (cndItem.name == "uid") {
                myQuires["uid"] = cnds[i].value;
            } else if (cndItem.name == "chargeTimes") {
                myQuires["chargeTimes"] = cnds[i].value;
            }else if (cndItem.name == "queryFee") {
                myQuires["queryFee"] = cnds[i].value;
            }
        }
        parameters['myQuires']=myQuires;
        p.setExtraParam('parameters', parameters);
    },

    doMoneyOrder:function(){
        this.orderInfo ="fee desc";
        this.doQuery();
        this.orderInfo =null;
    },

    doTimeOrder:function(){
        this.orderInfo ="finishtime desc";
        this.doQuery();
        this.orderInfo =null;
    },
})