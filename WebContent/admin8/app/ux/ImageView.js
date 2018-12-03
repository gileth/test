Ext.define('app.ux.ImageView', {
    extend: 'Ext.Component',
    alias: 'widget.imageview',
    autoEl: {
        tag: 'img',
        src: Ext.BLANK_IMAGE_URL,
        cls: 'my-managed-image'
    },

    onRender: function() {
        this.autoEl = Ext.apply({}, this.initialConfig, this.autoEl);
        this.callParent(arguments);
        this.el.on('load', this.onLoad, this);
    },

    onLoad: function() {
        this.fireEvent('load', this);
    },

    setSrc: function(src) {
        this.autoEl.src = src;
    },

    getSrc: function(src) {
        return this.el.dom.src || this.src;
    }
});