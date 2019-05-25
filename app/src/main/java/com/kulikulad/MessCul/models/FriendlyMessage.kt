package com.kulikulad.MessCul.models

class FriendlyMessage() {
    var messId:String? = null;
    var id: String? = null;
    var text: String? = null;
    var name: String? = null;
    var type: String? = "text";
    var textForFile: String? = "fileName";

    var recipientId: String? = null;
    var dialogId: String? = null;

    constructor(id: String, text: String, name: String, recipientId: String?, type: String? ) : this() {
        this.id = id;
        this.text = text;
        this.name = name;
        this.type = type;

        this.recipientId = recipientId;

        dialogId = getDialogId(id, recipientId);
    }

    constructor(id: String, text: String, name: String, recipientId: String?, type: String? ,fileName:String?) : this() {
        this.id = id;
        this.text = text;
        this.name = name;
        this.type = type;
        this.textForFile = fileName;

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

fun getFileName(fileWay:String):String
{
    var result: String? = null;

    var arr =fileWay.split("/");
    result = arr.last();

    return result!!;
}