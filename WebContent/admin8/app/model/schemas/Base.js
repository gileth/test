Ext.define('app.model.schemas.Base', {
    items:[],
    getFields:function(){
        var fields = [];
        for (var i = 0; i < this.items.length; i++) {
            var it = this.items[i];
            fields.push(it.id);
        }
        return fields;
    },
    getItems:function(){
        return this.items;
    },
    getItem: function (id) {
        for (var i = 0; i < this.items.length; i++) {
            var it = this.items[i];
            if (it.name == id) {
                return it;
            }
        }
        return null;
    },
    getPkey: function(){
        for (var i = 0; i < this.items.length; i++) {
            var it = this.items[i];
            if (it.pkey) {
                return it;
            }
        }
        return null;
    }
});