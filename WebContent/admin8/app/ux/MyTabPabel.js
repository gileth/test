Ext.define('app.ux.MyTabPabel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.mytabpanel',
    modules: new Ext.util.MixedCollection(),
    hasModule: function(moduleId){
        return this.modules.containsKey(moduleId);
    },
    getModule: function(moduleId){
        return this.modules.get(moduleId);
    },
    addModule: function(moduleId, panel){
        if(!this.modules.containsKey(moduleId)){
            this.modules.add(moduleId, panel);
        }
    },
    removeModule: function(module){
        this.modules.remove(module);
    },
    config: {
        defaults: {
            closable: true
        }
    }
});