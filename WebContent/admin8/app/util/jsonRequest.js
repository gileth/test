Ext.define('app.util.jsonRequest', {

    alternateClassName: 'jsonRequest',

    statics: {
        execute: function (jsonData, callback, scope) {
            Ext.Ajax.request({
                url: '*.jsonRequest',
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                jsonData: jsonData,
                callback: complete,
                scope: scope || this
            });
            function complete(ops, sucess, response) {
                var json = {};
                var code = 200;
                var msg = "";

                if (sucess) {
                    try {
                        json = Ext.decode(response.responseText);
                        code = json["code"];
                        msg = json["msg"];
                    }
                    catch (e) {
                        code = 500;
                        msg = "ParseResponseError";
                    }
                }
                else {
                    code = 400;
                    msg = "ConnectionError";
                }
                if (typeof callback == "function") {
                    var ctx = typeof scope == "object" ? scope : this;
                    callback.call(ctx, code, msg, json, response);
                }
            }
        }
    }
});