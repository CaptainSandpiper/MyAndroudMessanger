package com.kulikulad.MessCul.models

class FriendlyMessage() {
    var id: String? = null;
    var text: String? = null;
    var name: String? = null;

    var recipientId: String? = null;
    var dialogId: String? = null;

    constructor(id: String, text: String, name: String, recipientId: String?) : this() {
        this.id = id;
        this.text = text;
        this.name = name;

        this.recipientId = recipientId;

        dialogId = getDialogId(id, recipientId);
    }
}

fun getDialogId(id: String?, recipientId: String?):String?
{
    var arrayIds = arrayOf(id!!, recipientId!!);
    arrayIds = arrayIds.sortedArray();

    var dialogId = arrayIds[0] + arrayIds[1];

    return dialogId;
}