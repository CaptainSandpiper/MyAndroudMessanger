package com.kulikulad.MessCul.models

import android.location.Location

class UserLocation() {

    var lat: Double? = null;
    var lon: Double? = null;

    constructor(latLng:Location):this()
    {
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
    }


}