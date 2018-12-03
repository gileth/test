Ext.define('app.ux.EChartsComponent', {
    extend: 'Ext.Component',
    alias: 'widget.echartscmp',
    border: false,
    config: {
        option: null
    },
    initComponent: function () {
        var me = this;
        if (!me.height) {
            throw new Error("图表组件需显式设置高度!");
        }

        me.on("boxready", function () {
            me.echarts = echarts.init(me.getEl().dom);
            if (me.option) {
                me.echarts.setOption(me.option);
            }
        });

        me.on("fillData", function () {
            if (me.echarts) {
                if (me.option) {
                    me.echarts.hideLoading();
                    me.echarts.setOption(me.option,true);
                }
            }
        });

        me.callParent();
    }
});  