Ext.define('app.ux.MyFormPanel', {

    extend: 'Ext.form.Panel',
    xtype: 'myListView',
    requires: [
        'app.util.SimpleDicFactory',
        'app.util.jsonRequest'
    ],
    uses: [
        'app.ux.DateTime'
    ],
    frame: true,
    layout: {
        type: 'table',
        columns: this.columnCount || 3,
        tableAttrs: {
            style: {
                width: '100%'
            }
        }
    },
    bodyPadding: '10 10',
    defaults: {
        labelWidth:90,
        labelAlign:'right',
        selectOnFocus: true,
        width: 250
    },
    defaultType: 'textfield',
    //bodyPadding: 10,
    width: 830,
    //height:350,
    initDataId: null,
    cmd: 'create',

    initComponent: function () {
        Ext.tip.QuickTipManager.init();
        var sc = this.schema;
        var items = sc.getItems();
        this.items = [];
        for (var i = 0; i < items.length; i++) {
            var it = Ext.apply({}, items[i]);

            if(it.display && it.display == 1){
                it.hidden = true;
            }

            if(it.pkey && it.strategy == "identity"){
                it.hidden = true;
            }
            it.fieldLabel = it.name;
            it.name = it.id;
            delete it.id;
            delete it.width;
            var f = this.createField(it);
            this.items.push(f);
        }

        this.buttons = [
            {text: '保存', handler: this.doSave, scope: this},
            {text: '取消', handler: this.doCloseWin, scope: this}
        ];

        this.callParent();

    },

    createField: function (cfg) {
        if (cfg.dic) {
            return this.createDicField(cfg);
        }
        var type = cfg.type || 'string';
        switch (type) {
            case 'int':
                cfg.decimalPrecision = 0;
                cfg.allowDecimals = false;
            case 'double':
            case 'bigDecimal':
                cfg.xtype = "numberfield";
                cfg.decimalPrecision = cfg.decimalPrecision || 2;
                break;
            case 'date':
                cfg.xtype = 'datefield';
                cfg.emptyText = "请选择日期";
                cfg.format = 'Y-m-d';
                break;
            case 'datetime':
                cfg.xtype = 'datetimefield';
                cfg.emptyText = "请选择日期时间";
                cfg.format = 'Y-m-d H:i:s';
                break;
            case 'ueditor':
                cfg.xtype = 'ueditor';
                this.width = 850;
                break;
            case 'image':
                cfg.xtype = 'image';
                cfg.width = cfg.width || 160;
                cfg.height = cfg.height || 150;
                cfg.rowspan = cfg.rowspan || 4;
                cfg.style = cfg.style || {
                            width: '60%',
                            marginRight: '10px',
                            float : 'right'
                        };
                break;
        }
        var xtype = cfg.xType;
        if(xtype){
            cfg.xtype = xtype;
            switch (xtype){
                case 'htmleditor':
                    cfg.height = 240;
                    break;
                case 'textarea':
                    //cfg.height = 120;
                    cfg.rowspan = cfg.rowspan || 4;
                    break;
                case 'ueditor':
                    cfg.height = 520;
                    cfg.width = this.width - 50;
                    break;
            }
        }
        if(cfg.colspan){
            cfg.width = cfg.width || '98%';
        }
        return cfg;
    },

    createDicField: function (cfg) {
        return SimpleDicFactory.createDic(cfg);
    },

    doSave: function () {
        if (!this.getForm().isValid()) {
            return;
        }
        jsonRequest.execute({
            service: this.loadService || 'myListService',
            method: 'save',
            parameters: {
                cmd:this.cmd,
                entityName: this.schema.mapping,
                data: this.getForm().getValues()
            }
        }, function (code, msg, json) {
            if (200 == code) {
                this.fireEvent('save', json);
            } else {
                Ext.MessageBox.alert('提示', msg);
            }
        }, this);
    },

    doCloseWin: function () {
        if (this.win) {
            this.win.close();
        }
    },

    loadData: function () {
        jsonRequest.execute({
            service: this.loadService || 'myListService',
            method: 'load',
            parameters: {
                entityName: this.schema.mapping,
                id: this.initDataId
            }
        }, function (code, msg, json) {
            this.el.unmask();
            if (200 == code) {
                var data = json.body;
                for (var k in data) {
                    var d = data[k];
                    if (typeof d == 'string') {
                        if (data[k].indexOf(' 00:00:00') != -1) {
                            data[k] = data[k].replace(' 00:00:00', '');
                        }
                    }
                }
                this.getForm().setValues(data);
            }

        }, this);
    },

    reset: function () {
        this.cmd = 'create';
        this.initDataId = null;
        this.callParent();
    },

    onBeforeshow: function () {
        if (this.cmd == 'read') {
            this.hideButtons();
        } else {
            this.showButtons();
        }
        if (this.initDataId) {
            this.loadData();
        }
    },

    showButtons: function () {
        var btns = this.query('button');
        if (btns) {
            for (var btn in btns) {
                btns[btn].show();
            }
        }
    },

    hideButtons: function () {
        var btns = this.query('button');
        if (btns) {
            for (var btn in btns) {
                btns[btn].hide();
            }
        }
    }
    ,

    getWin: function () {
        var win = this.win;
        if (!win) {
            win = Ext.create('Ext.window.Window', {
                title: this.title || this.schema.name,
                width: this.width,
                height: this.height,
                html: 'hello',
                layout: 'fit',
                closeAction: 'hide',
                modal: true,
                items: this,
                listeners: {
                    beforeshow: {
                        fn: this.onBeforeshow,
                        scope: this
                    }
                }
            });
            this.win = win;
        }
        return win;
    }

});