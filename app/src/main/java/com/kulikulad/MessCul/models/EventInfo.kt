package com.kulikulad.MessCul.models

class EventInfo() {

    var year:String? = null;
    var month:String? = null;
    var day:String? = null;
    var hour:String? = null;
    var minute:String? = null;
    var meetLat:Double? = null;
    var meetLng:Double? = null;
    var dayOfWeek:String? = null;

    constructor(year: String,month:String, day:String, hour:String, minute:String, lat:Double, lng:Double, dayOfWeek:String ):this()
    {
        this.year = year
        this.month = month
        this.day = day
        this.hour = hour
        this.minute = minute
        this.meetLat = lat
        this.meetLng = lng
        this.dayOfWeek = dayOfWeek
    }
}