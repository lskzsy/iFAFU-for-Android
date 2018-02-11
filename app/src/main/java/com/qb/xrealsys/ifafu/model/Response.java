package com.qb.xrealsys.ifafu.model;

import android.content.Context;

/**
 * Created by sky on 10/02/2018.
 */

public class Response {

    private boolean success;

    private int     code;

    private String  message;

    private int     msgId;

    public Response(boolean inSuc, int inCode, String inMessage) {
        success = inSuc;
        code    = inCode;
        message = inMessage;
        msgId   = -1;
    }

    public Response(boolean inSuc, int inCode, int inMsgId) {
        success = inSuc;
        code    = inCode;
        msgId   = inMsgId;
        message = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(Context context) {
        if (message == null && msgId != -1) {
            return context.getResources().getString(msgId);
        } else {
            return message;
        }
    }

    public int getMsgId() {
        return msgId;
    }
}
