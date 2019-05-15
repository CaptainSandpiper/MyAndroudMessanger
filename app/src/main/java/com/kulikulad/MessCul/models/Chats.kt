package com.kulikulad.MessCul.models

class Chats() {
    var message: FriendlyMessage? = null;

    constructor(mess:FriendlyMessage):this()
    {
        this.message = mess;
    }
}