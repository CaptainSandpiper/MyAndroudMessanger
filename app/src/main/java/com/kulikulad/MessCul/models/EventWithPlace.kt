package com.kulikulad.MessCul.models

import com.jonaswanke.calendar.BaseEvent

class EventWithPlace() {

    var event:BaseEvent? = null;
    var lat:Long? = null;
    var lng: Long? = null;

    constructor(event: BaseEvent, lat:Long, lng: Long):this()
    {
        this.event = event;
        this.lat = lat;
        this.lng = lng;
    }
}

