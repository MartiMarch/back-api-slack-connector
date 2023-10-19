package com.valentiasoft.backapislackconnector.po

enum MessageType {
    SCRIPT('script'),
    MICRO('micro'),
    PIPELINE('pipeline'),
    TEST('test'),
    UNKNOWN('unknown')

    private final String msgType

    private MessageType(String msgType){
        this.msgType = msgType
    }

    static MessageType getMsgType(String msgType){
        return values().find {msg ->
            msg.toString().equalsIgnoreCase(msgType)
        } ?: UNKNOWN
    }
}
