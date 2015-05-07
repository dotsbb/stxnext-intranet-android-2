package com.stxnext.intranet2.middle.api.request;

import com.stxnext.intranet2.middle.api.callback.UserApiCallback;

/**
 * Created by Tomasz Konieczny on 2015-05-07.
 */
public abstract class UserApi {

    private final UserApiCallback apiCallback;

    public UserApi(UserApiCallback callback) {
        this.apiCallback = callback;
    }

    abstract void requestForUser(String userId);

}
