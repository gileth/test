Ext.define('app.view.main.dashboard', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.app-dashboard',

    bodyPadding: 5,
    bodyCls: 'app-dashboard',

    requires: [
        'Ext.data.reader.Json',
        'Ext.chart.CartesianChart',
        'Ext.chart.PolarChart',
        'app.ux.EChartsComponent',
        'app.util.jsonRequest'
    ],

    layout:{
        type:'table',//表格布局
        columns:2 //设置表格布局默认列数为4列
    },
    frame:true,

    initComponent: function () {

        var me = this;
        me.chartData = {};
        me.getChart();

        var width = (Ext.getBody().getWidth() - 320)/2 ;
        var height = (Ext.getBody().getHeight() - 220)/2;

        this.items = [{
            margin : '0 0 0 5',
            xtype: 'panel',
            title : '个人信息',
            iconCls : 'fa fa-user',
            width : width,
            height : height,
            border :  '1 1 1 1',
            items:[{
                xtype : 'grid',
                columnLines : true,
                store: {
                    autoLoad : true,
                    fields: ['title','content'],
                    proxy : {
                        type : 'ajax',
                        url: '*.jsonRequest',
                        actionMethods: {read: 'POST'},
                        headers: {
                            'Content-Type': "application/json"
                        },
                        extraParams: {
                            service: 'dashboardService',
                            method:  'info'
                        },
                        paramsAsJson: true,
                        reader : {
                            type : 'json',
                            rootProperty : 'body.body'
                        }
                    }
                },
                columns : [{width: 140,dataIndex: 'title',renderer:function(val){return '<span><strong>'+val+'</strong></span>';}},{flex:1,dataIndex: 'content'}]
            }]
        },{
            margin : '0 0 0 5',
            xtype: 'panel',
            title : '待办事项',
            iconCls : 'fa fa-bell',
            width : width,
            height : height,
            border :  '1 1 1 1',
            items:[{
                xtype : 'grid',
                columnLines : true,
                store: {
                    autoLoad : true,
                    fields: ['title','content'],
                    proxy : {
                        type : 'ajax',
                        url: '*.jsonRequest',
                        actionMethods: {read: 'POST'},
                        headers: {
                            'Content-Type': "application/json"
                        },
                        extraParams: {
                            service: 'dashboardService',
                            method:  'job'
                        },
                        paramsAsJson: true,
                        reader : {
                            type : 'json',
                            rootProperty : 'body.body'
                        }
                    }
                },
                columns : [{ xtype: 'rownumberer', text:'序号'},{width: 120,dataIndex: 'title'},{flex:1,dataIndex: 'content',renderer:function(val){return '<span style="color:green;"><strong>'+val+'</strong></span>';}}]
            }]
        },{
            xtype: 'panel',
            title : '数据统计',
            iconCls : 'fa fa-bullhorn',
            margin : '20 0 0 5',
            width : width,
            height : height,
            border :  '1 1 1 1',
            items:[{
                xtype : 'grid',
                columnLines : true,
                store: {
                    autoLoad : false,
                    fields: ['title','content'],
                    proxy : {
                        type : 'ajax',
                        url: '*.jsonRequest',
                        actionMethods: {read: 'POST'},
                        headers: {
                            'Content-Type': "application/json"
                        },
                        extraParams: {
                            service: 'dashboardService',
                            method:  'statistics'
                        },
                        paramsAsJson: true,
                        reader : {
                            type : 'json',
                            rootProperty : 'body.body'
                        }
                    }
                },
                columns : [{flex:1,dataIndex: 'title'},{flex:1,dataIndex: 'content'}]
            }]
        },{
            margin : '20 0 0 5',
            xtype: 'panel',
            title : '统计分析',
            iconCls : 'fa fa-line-chart',
            width : width,
            height : height,
            layout: 'fit',
            border :  '1 1 1 1',
            items : [{

                xtype:'echartscmp',
                height : height,
                width : width,
                option : {
                    tooltip : {
                        trigger: 'axis'
                    },
                    calculable : true,
                    legend: {
                        data:['注册量','新增充值']
                    },
                    xAxis : [
                        {
                            type : 'category'
                        }
                    ],
                    yAxis : [
                        {
                            type : 'value',
                            name : '新注册',
                            axisLabel : {
                                formatter: '{value} 人'
                            }
                        },
                        {
                            type : 'value',
                            name : '新增充值（万）',
                            axisLabel : {
                                formatter: '{value} 万元'
                            }
                        }
                    ],
                    series : [

                        {
                            name:'注册量',
                            type:'bar'
                        },
                        {
                            name:'新增充值',
                            type:'line',
                            yAxisIndex: 1
                        }
                    ]
                }
                }]
            }];

        this.callParent();
    },

    getChart : function () {
        var me = this;
        jsonRequest.execute({
            service:'dashboardService',
            method:'chart'
        },function(code,msg,json) {
            if(code == 200){
                me.chartData = json.body
                var chart = me.items.items[3].items.items[0];
                var option = chart.option;
                option.xAxis[0].data = json.body.xAxis;
                option.series[0].data = json.body.yAxis1[0];
                option.series[1].data = json.body.yAxis2[0];
                chart.option = option;
                chart.fireEvent('fillData');
            }else{
                Ext.Msg.alert('提示', msg);
            }
        })
    }


})