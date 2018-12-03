Ext.define('app.util.renderComboUtils', {

    alternateClassName: 'renderComboUtils',
    render:function(v,data){
        if (data instanceof Array) {
            for (var i = 0; i < data.length; i++) {
                if (data[i] instanceof Array && data[i].length > 1) {
                    if (v == data[i][0]) {
                        return data[i][1];
                    }
                } else if (data[i] instanceof Object) {
                    if (v == data[i].value) {
                        return data[i].text;
                    }
                }
            }
        }
        return v;
    },

    renderJson:function(v,obj){
        for (var i = 0; i < obj.length; i++) {
                var data = obj[i];
                if (v == data.data.key) {
                    return data.data.text;
                }
            }
        return v;
    }
});