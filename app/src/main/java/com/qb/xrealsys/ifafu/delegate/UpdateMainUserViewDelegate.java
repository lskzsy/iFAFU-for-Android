package com.qb.xrealsys.ifafu.delegate;

import com.qb.xrealsys.ifafu.model.User;

/**
 * Created by sky on 14/02/2018.
 */

public interface UpdateMainUserViewDelegate {

    void updateMainUser(User user);

    void updateError(String error);
}
