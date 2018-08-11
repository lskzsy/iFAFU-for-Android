package com.qb.xrealsys.ifafu.User.delegate;

import com.qb.xrealsys.ifafu.Base.model.Response;

public interface ModifyPasswordDelegate {

    Boolean     cancelClick();

    void        submitClick(String newPassword);

    void        successModify();
}
