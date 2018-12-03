Ext.define('app.model.SchemaUtils', {

    alternateClassName: 'SchemaUtils',


    statics: {
        schemas: new Ext.util.MixedCollection(),
        getSchema:function(sc){
            var schema =  this.schemas.get(sc);
            if(schema == null){
                Ext.syncRequire(sc);
                var schema = eval(sc);
                this.schemas.add(sc,schema);
            }
            return schema;
        }
    }
});