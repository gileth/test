Ext.define('app.ux.MyListView', {

    extend: 'Ext.grid.Panel',
    requires: [
        'Ext.grid.column.Action',
        'Ext.toolbar.Paging',
        //'Ext.ux.ProgressBarPager',
        'app.model.SchemaUtils',
        'app.util.jsonRequest',
        'app.util.SimpleDicFactory'
    ],
    uses: [
        'app.ux.MyFormPanel'
    ],
    xtype: 'myListView',
    frame: true,
    enablePaging: true,
    enableRowNumber: false,
    listService: 'myListService',
    listMethod: 'list',
    entityPackage: 'app.model.schemas',
    entityName: null,
    cnd: null,
    searchFieldsBar: null,
    columnLines : true,
    orderInfo:null,
    dicStore : {},

    getDicStore : function (dicId) {
        var me = this;

        var dicname = dicId.split('.')[1];
        me.dicStore[dicname] = {};
        Ext.Ajax.request({
            url: dicId + ".dc",
            method: "GET",
            params : {sliceType : 0},
            success: function (response, opts) {
                var data = eval('('+ response.responseText+ ')');
                for(var i=0; i<data.length;i++){
                    var item = data[i];
                    me.dicStore[dicname][item.key] = item.text;
                }
            }
        });
    },
    getDicText : function(dicId,key){
        var me = this;
        var dicname = dicId.split('.')[1];
        return me.dicStore[dicname][key];
    },

    //selModel: {
    //    selType: 'checkboxmodel'
    //},

    selModel: {
        selType: 'rowmodel', // rowmodel is the default selection model
        mode: 'MULTI' // Allows selection of multiple rows
    },

    initComponent: function () {
        var me = this;
        var sc = SchemaUtils.getSchema(this.entityPackage + "." + this.entityName);
        this.schema = sc;
        this.columns = [];
        var queryItems = [];
        this.dicColumns = [];

        for (var i = 0; i < sc.getItems().length; i++) {
            var it = Ext.apply({}, sc.getItems()[i]);
            if(it.pkey && it.strategy == "identity"){
                it.hidden = true;
            }
            it.dataIndex = it.id;
            it.text = it.name;

            if(it.display && it.display == '3'){
                it.hidden = true;
            }
            //add by chzhxiang 2015-10-27
            if(it.dic){
                this.dicColumns.push(it.dic);
                it.renderer = function(val,cellmeta,record,rowIndex,columnIndex,store){
                    var dicname = this.columns[columnIndex].dic.split('.')[1];
                    return me.dicStore[dicname][val];
                }
            }

            if(it.queryable){
                namelen = it.name ? it.name.length : 0;
                if(it.dic){
                    var dic = {};
                    dic = SimpleDicFactory.createDic({
                        fieldLabel: it.name,
                        name: it.id,
                        width : namelen * 15 + 120,
                        labelWidth: namelen * 15,
                        dic:it.dic
                    });
                    queryItems.push(dic);
                }else{
                    var cfg = {}
                    cfg.fieldLabel = it.name;
                    cfg.labelWidth = namelen * 15;
                    cfg.name = it.id;
                    cfg.width = namelen * 15 + 120;

                    switch (it.type){
                        case 'string' :
                            cfg.xtype = 'textfield';
                            break;
                        case 'date' :
                            cfg.xtype = 'datefield';
                            cfg.format = "Y-m-d";
                            break;
                        case 'datetime' :
                            cfg.xtype = 'datefield';
                            cfg.format = "Y-m-d";
                            break;
                        case 'int' :
                            cfg.xtype = 'numberfield';
                            break;
                        case 'email' :
                            cfg.xtype = 'textfield';
                            break;
                    }
                    queryItems.push(cfg);
                }

            }
            if(it.renderer){
                it.renderer = eval(it.renderer);
            }
            if(it.display){
                if(it.display == '-1' || it.display == '0' || it.display == '2'){
                    continue;
                }
            }
            delete it.id;
            this.columns.push(it);
        }



        if (this.enableRowNumber) {
            this.columns = [{xtype: 'rownumberer'}].concat(this.columns);
        }
        this.store = this.getStore();
        this.callParent();

        this.addDocked({
            xtype: 'toolbar',
            items: queryItems
        });

        var actions = this.actions;
        if (actions) {
            var items = [];
            for (var i = 0; i < actions.length; i++) {
                var ac = actions[i];
                var cfg = {
                    text: ac.name,
                    handler: this.doAction,
                    cmd: ac.id,
                    scope: this
                };
                Ext.apply(cfg, ac);
                delete cfg.id;
                items.push(cfg);
            }
            if(this.down('toolbar')){
                this.down('toolbar').add({
                    xtype: 'toolbar',
                    items: items
                });
            }else{
                this.addDocked({
                    xtype: 'toolbar',
                    items: items
                });
            }

        }
        if (this.enablePaging == true || this.enablePaging == 'true') {
            this.addDocked({
                xtype: 'pagingtoolbar',
                dock: 'bottom',
                store: this.store,
                emptyMsg: '<b>暂无记录</b>',
                displayMsg: '显示 {0} - {1} 总共 {2} 条记录',
                displayInfo: true
            });
        }
        this.addListener('rowdblclick', this.onDBClick, this);
    },

    onDestroy: function () {
        if (this.form) {
            this.form.destroy();
        }
    },

    hasAction: function(aid){
        if(!this.actions){
            return false;
        }
        var actions = this.actions;
        for (var i = 0; i < actions.length; i++) {
            var ac = actions[i];
            if(ac.id == aid){
                return true;
            }
        }
        return false;
    },

    onDBClick: function (grid, r, tr, i, e, op) {
        if(this.hasAction("update")){
            this.loadRecord(this.getPkeyValueFromRecord(r), "update");
        }else if(this.hasAction("read")){
            this.loadRecord(this.getPkeyValueFromRecord(r));
        }
    },

    getStore: function () {
        if(this.store){
            return this.store;
        }
        var proxy = Ext.create('Ext.data.proxy.Ajax', {
            type: 'ajax',
            url: '*.jsonRequest',
            actionMethods: {read: 'POST'},
            headers: {
                'Content-Type': "application/json"
            },
            extraParams: {
                service: this.listService,
                method: this.listMethod
            },
            paramsAsJson: true,
            enablePaging: this.enablePaging,
            reader: {
                type: 'json',
                rootProperty: 'body.body',
                totalProperty: 'body.totalSize'
            }
        });
        var pkey = this.schema.getPkey();
        if(pkey){
            pkey = pkey.id
        }else{
            pkey = 'id';
        }
        var store = Ext.create('Ext.data.JsonStore', {
            proxy: proxy,
            model: Ext.create('Ext.data.Model',{
                idProperty: pkey,
                fields: this.schema.getFields()
            }),
            listeners: {
                beforeload: {
                    fn: this.beforeStoreLoad,
                    scope: this
                }
            },
            autoLoad: true
        });
        return store;
    },

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
        p.setExtraParam('parameters', parameters);
    },

    doAction: function (item, e) {
        var cmd = item.cmd;
        cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1);
        var action = this["do" + cmd];
        if (action) {
            action.apply(this, [item, e]);
        }
    },

    getForm: function () {
        var form = this.form;
        if (!form) {
            form = Ext.create('app.ux.MyFormPanel', {
                schema: this.schema,
                loadService:this.listService,
                listeners: {
                    save:{
                        fn:this.onSave,
                        scope:this
                    }
                }
            });
            this.form = form;
        }
        return form;
    },

    onSave:function(data){
        this.form.getWin().hide();
        this.store.reload();
    },

    loadRecord: function (pkey, cmd) {
        var form = this.getForm();
        form.reset();
        form.cmd = cmd || 'read';
        form.initDataId = pkey;
        form.getWin().show();
        form.el.mask('正在加载中...');
    },

    getPkeyValueFromRecord:function(r){
        var pkey = this.schema.getPkey();
        if(pkey){
            pkey = r.get(pkey.id)
        }else{
            pkey = r.getId();
        }
        return pkey;
    },

    doRead: function (item, e) {
        var s = this.getSelection();
        if (s && s.length > 0) {
            this.loadRecord(this.getPkeyValueFromRecord(s[0]));
        }
    },
    doUpdate: function (item, e) {
        var s = this.getSelection();
        if (s && s.length > 0) {
            this.loadRecord(this.getPkeyValueFromRecord(s[0]), 'update');
        }
    },
    doCreate: function (item, e) {
        var form = this.getForm();
        form.reset();
        form.getWin().show();
    },
    doDelete: function (item, e) {
        var s = this.getSelection();
        if (s && s.length > 0) {
            this.deleteRecord(this.getPkeyValueFromRecord(s[0]));
        }
    },

    deleteRecord: function (pkey) {
        Ext.MessageBox.confirm('确认', '删除记录[' + pkey + ']?', function (txt) {
            if ('yes' == txt) {
                jsonRequest.execute({
                    service:this.listService,
                    method:'delete',
                    parameters:{
                        entityName:this.schema.mapping,
                        id:pkey
                    }
                },function(code,msg,json){
                    this.store.reload();
                },this);
            }
        }, this);
    },
    
    doQuery : function () {
        this.cnd =null;
        var cnds = this.down('toolbar').items.items;
        for(var i = 0 ; i < cnds.length ; i++){
            if(cnds[i].xtype){
                var cnditem = "";
                switch (cnds[i].xtype){
                    case 'textfield' :
                        if(cnds[i].value){
                            cnditem = "['eq', ['$', '"+cnds[i].name+"'],['s', '"+cnds[i].value+"']]";
                        }
                        break;
                    case 'datefield' :
                        if(cnds[i].value){
                            cnditem = "['ge', ['$', '"+cnds[i].name+"'],['s', '"+Ext.util.Format.date(cnds[i].value, 'Y-m-d')+"']]";
                            var enddate = Ext.Date.add(cnds[i].value, Ext.Date.DAY, 1);
                            var seconditem = "['lt', ['$', '"+cnds[i].name+"'],['s', '"+Ext.util.Format.date(enddate, 'Y-m-d')+"']]";
                            cnditem = "['and',"+cnditem+","+seconditem+"]"
                        }
                        break;
                    case 'numberfield' :
                        if(cnds[i].value){
                            cnditem = "['eq', ['$', '"+cnds[i].name+"'],['s', '"+cnds[i].value+"']]";
                        }
                        break;
                    case 'combobox' :
                        if(cnds[i].value){
                            cnditem = "['eq', ['$', '"+cnds[i].name+"'],['s', '"+cnds[i].value+"']]";
                        }
                        break;
                }
                if(cnditem && cnditem.length > 0){
                    if(this.cnd){
                        this.cnd = "['and', "+this.cnd+", "+cnditem+"]";
                    }else{
                        this.cnd = cnditem;
                    }
                }
            }
        }
        this.store.loadPage(1);
        //this.cnd = oldcnd;
    }

});