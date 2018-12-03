Ext.define('app.ux.GMapPanelNew', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.gmappanelnew',

    requires: ['Ext.window.MessageBox'],

    markersArray : [],

    initComponent : function(){
        Ext.applyIf(this,{
            plain: true,
            gmapType: 'map',
            border: false
        });

        this.callParent();
    },

    onBoxReady : function(){
        var center = this.center;
        this.callParent(arguments);

        if (center) {
            if (center.geoCodeAddr) {
                this.lookupCode(center.geoCodeAddr, center.marker);
            } else {
                this.createMap(center);
            }
        } else {
            Ext.raise('center is required');
        }

    },

    createMap: function(center, marker) {
        var me = this;
        var options = Ext.apply({}, this.mapOptions);

        options = Ext.applyIf(options, {
            zoom: 14,
            center: center,
            mapTypeId: google.maps.MapTypeId.HYBRID
        });
        this.gmap = new google.maps.Map(this.body.dom, options);
        if (marker) {
            this.addMarker(Ext.applyIf(marker, {
                position: center
            }));
        }
        //add by chzhxiang
        google.maps.event.addListener(this.gmap,'click', function (event) {
            for(i in me.markersArray){
                me.markersArray[i].setMap(null);
            }
            var marker = {};
            marker.position = event.latLng;
            me.addMarker(marker);
            me.getAddress(event.latLng);
            me.fireEvent('getPosition',event.latLng);
        });
        Ext.each(this.markers, this.addMarker, this);
        this.fireEvent('mapready', this, this.gmap);
    },

    addMarker: function(marker) {

        marker = Ext.apply({
            map: this.gmap
        }, marker);

        if (!marker.position) {
            marker.position = new google.maps.LatLng(marker.lat, marker.lng);
        }
        var o =  new google.maps.Marker(marker);
        Ext.Object.each(marker.listeners, function(name, fn){
            google.maps.event.addListener(o, name, fn);
        });
        this.markersArray.push(o);
        return o;
    },

    lookupCode : function(addr, marker) {
        this.geocoder = new google.maps.Geocoder();
        this.geocoder.geocode({
            address: addr
        }, Ext.Function.bind(this.onLookupComplete, this, [marker], true));
    },

    onLookupComplete: function(data, response, marker){
        if (response != 'OK') {
            Ext.MessageBox.alert('Error', 'An error occured: "' + response + '"');
            return;
        }
        this.createMap(data[0].geometry.location, marker);
    },

    afterComponentLayout : function(w, h){
        this.callParent(arguments);
        this.redraw();
    },

    redraw: function(){
        var map = this.gmap;
        if (map) {
            google.maps.event.trigger(map, 'resize');
        }
    },

    getAddress : function(location){
        var me = this;
        this.geocoder = new google.maps.Geocoder();
        this.geocoder.geocode({'location': location}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                if (results[0]) {
                   me.fireEvent('getAddress',results[0].formatted_address);
                }
            } else {
                return '';
            }
        });
    }

});
