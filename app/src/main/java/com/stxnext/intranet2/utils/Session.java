package com.stxnext.intranet2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

/**
 * Created by Lukasz Ciupa on 2015-05-20.
 */
public class Session {

    private static Session instance = null;

    private String authorizationCode = null;
    private SharedPreferences preferences = null;
    private String userId = null;
    private CookieManager cookieManager = null;
    private Integer absenceDaysLeft = null;
    private Integer daysMandated = null;

    private static final String PREFERENCES_NAME = "com.stxnext.intranet2";
    private static final String SUPERHERO_MODE_PREFERENCE = "com.stxnext.intranet2";
    private static final String CODE_PREFERENCE = "code";
    private static final String USER_ID_PREFERENCE = "user_id";

    private Session(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        cookieManager = new CookieManager(new com.stxnext.intranet2.utils.PersistentCookieStore(context.getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        if (isLogged()) {
            initializeOkHttpCookieHandler();
        }
    }

    public static Session getInstance(Context context) {
        if (instance == null) {
            instance = new Session(context);
        }
        return instance;
    }

    public void logout() {
        userId = null;
        preferences.edit()
                .remove(USER_ID_PREFERENCE)
                .remove(SUPERHERO_MODE_PREFERENCE)
                .remove(CODE_PREFERENCE)
                .commit();
        clearManagerCookieStore();
    }

    public void clearManagerCookieStore() {
        CookieStore managerCookieStore = cookieManager.getCookieStore();
        managerCookieStore.removeAll();
    }

    /**
     * Has to be executed after initialization of webView (best before starting loading page).
     * When executed on logout() then there is a strange error where webKitCookieManager is not null
     * but causes crash of whole appliaction on executing of its methods.
     */
    public void clearWebKitCookieStore() {
        android.webkit.CookieManager webKitCookieManager = android.webkit.CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webKitCookieManager.removeAllCookies(null);
        } else {
            if (webKitCookieManager.hasCookies()) {
                webKitCookieManager.removeAllCookie();
            }
        }
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
        preferences.edit().putString(CODE_PREFERENCE, authorizationCode).commit();
    }

    public String getAuthorizationCode() {
        if (authorizationCode == null) {
            authorizationCode = preferences.getString(CODE_PREFERENCE, null);
        }
        return authorizationCode;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    /**
     * Sets globally cookie manager (with cookie store) for okhttp clients.
     */
    public void initializeOkHttpCookieHandler() {
        CookieHandler.setDefault(getCookieManager());
    }

    public void setUserId(String userId) {
        this.userId = userId;
        preferences.edit().putString(USER_ID_PREFERENCE, userId).apply();
    }

    public String getUserId() {
        if (userId == null) {
            userId = preferences.getString(USER_ID_PREFERENCE, null);
        }
        return userId;
    }


    public void enableSuperHeroMode(boolean enabled) {
        preferences.edit().putBoolean(SUPERHERO_MODE_PREFERENCE, enabled).apply();
    }

    public boolean isSuperHeroModeEnabled() {
        return preferences.getBoolean(SUPERHERO_MODE_PREFERENCE, false);
    }

    public void setAbsenceDaysLeft(int absenceDaysLeft) {
        this.absenceDaysLeft = absenceDaysLeft;
    }

    public Integer getAbsenceDaysLeft() {
        return absenceDaysLeft;
    }

    public void setDaysMandated(int daysMandated) {
        this.daysMandated = daysMandated;
    }

    public Integer getDaysMandated() {
        return daysMandated;
    }

    public boolean isLogged() {
        return getUserId() != null;
    }
}
