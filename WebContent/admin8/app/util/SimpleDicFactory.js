Ext.define('app.util.SimpleDicFactory', {
    alternateClassName: 'SimpleDicFactory',
    statics: {
        createDic: function (cfg) {
            var dicId;
            var filterStr;
            if (typeof cfg.dic == 'object') {
                dicId = cfg.dic.id;
                filterStr = cfg.dic.filter;
                if (typeof filterStr == 'object') {
                    filterStr = Ext.encode(filterStr);
                }
            }
            else {
                dicId = cfg.dic;
            }
            var url = dicId + ".dc";
            if (filterStr) {
                url += "?filter=" + filterStr;
            }
            if(cfg.sliceType != null && cfg.sliceType != undefined){
                url += "?sliceType=" + cfg.sliceType;
            }
            var dic = {
                xtype: 'combobox',
                valueField: 'key',
                displayField: 'text',
                store: Ext.create('Ext.data.JsonStore', {
                    proxy: {
                        type: 'ajax',
                        url: url,
                        reader: {
                            type: 'json'
                        }
                    },
                    fields: ['key', 'text'],
                    autoLoad: true
                })
            };
            Ext.apply(dic, cfg);
            return dic;
        }
    }

});