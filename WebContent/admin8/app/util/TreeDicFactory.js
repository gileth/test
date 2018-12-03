Ext.define('app.util.TreeDicFactory', {
    alternateClassName: 'TreeDicFactory',
    statics: {
        createDic: function (dic) {
            var dicId;
            var filterStr;
            if (typeof dic.dic == 'object') {
                dicId = dic.dic.id;
                filterStr = dic.dic.filter;
                if (typeof filterStr == 'object') {
                    filterStr = Ext.encode(filterStr);
                }
            }
            else {
                dicId = dic.dic;
            }
            var url = dicId + ".dc";
            if (filterStr) {
                url += "?filter=" + filterStr;
            }
            var dicTree = {
                xtype: 'treepicker',
                displayField : dic.displayField || 'text',
                valueField : dic.valueField || 'key',
                minPickerHeight : dic.minPickerHeight || 200,
                autoScroll : true,
                animate : true,
                containerScroll : false,
                forceSelection : true,
                emptyText: dic.emptyText || '请选择...',
                store : Ext.create('Ext.data.TreeStore',{
                    rootVisible : false,
                    proxy: {
                        type: 'ajax',
                        url : url,
                        reader: {
                            type: 'json'
                        }
                    },
                    root: {
                        text: '',
                        expanded: true
                    },
                    clearOnLoad: true,
                    listeners: {
                        beforeload: {
                            fn: this.beforeExpand,
                            scope: this
                        },
                        load :function(scope, records, successful, operation, node, eOpts){
                            for(var i=0;i<records.length;i++){
                                records[i].setId(records[i].data.key);
                            }
                        }
                    }
                }),
                listeners : {
                    select : function(scope, record){
                        scope.value = record.data.key;
                    }
                }
            }
            Ext.apply(dicTree, dic);
            return dicTree;
        },

        beforeExpand : function(st, op){
            var p = st.proxy;
            p.setExtraParam('node', op.node.data.key ? op.node.data.key : op._id);
            //p.setExtraParam('sliceType', '0');
        }
    }
});