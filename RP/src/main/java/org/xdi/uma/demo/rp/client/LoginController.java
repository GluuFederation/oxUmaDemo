package org.xdi.uma.demo.rp.client;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.xdi.gwt.client.GwtUtils;
import org.xdi.uma.demo.rp.client.events.LoginEvent;

import java.util.Date;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2013
 */

public class LoginController {

    /**
     * One day
     */
    public static long ONE_DAY_IN_MILIS = (long) 1000.0 * 60 * 60 * 24;

    /**
     * Sets login cookie.
     *
     * @param accessToken access token
     */
    public static void setLoginCookie(String accessToken) {
        final long tomorrowMilis = new Date().getTime() + ONE_DAY_IN_MILIS;
        Cookies.setCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken, new Date(tomorrowMilis));
        RP.getService().storeAat(accessToken, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable p_throwable) {
                //do nothing
            }

            @Override
            public void onSuccess(Void p_void) {
                //do nothing
            }
        });
    }

    public static void userLogin() {
        if (!hasAccessToken()) {
            final String accessToken = parseAccessToken();
            if (accessToken != null && accessToken.length() > 0 && !LoginController.hasAccessToken()) {
                LoginController.setLoginCookie(accessToken);
                RP.getEventBus().fireEvent(new LoginEvent(true));
                return;
            }

            RP.getService().getLoginUrl(new AsyncCallback<String>() {
                public void onFailure(Throwable caught) {
                    GwtUtils.showError("Unable to login.");
                }

                public void onSuccess(String result) {
                    if (result != null && result.length() > 0) {
                        Window.Location.assign(result);
                    } else {
                        GwtUtils.showInformation("Unable to login.");
                    }
                }
            });
        }
    }

    private static String parseAccessToken() {
        String hashString = Window.Location.getHash();

        if (hashString != null && hashString.trim().length() > 0) {
            if (hashString.startsWith("#")) {
                hashString = hashString.substring(1); // cut # sign
            }
            final String[] split = hashString.split("&");
            if (split != null && split.length > 0) {
                for (String keyValue : split) {
                    final String[] splitKeyValue = keyValue.split("=");
                    if (splitKeyValue != null && splitKeyValue.length == 2 && splitKeyValue[0].trim().equals("access_token")) {
                        return splitKeyValue[1];
                    }
                }
            }
        }
        return "";
    }

    public static String getAccessToken() {
        return Cookies.getCookie(ACCESS_TOKEN_COOKIE_NAME);
    }

    /**
     * Returns whether access token is saved in cookie.
     *
     * @return whether access token is saved in cookie
     */
    public static boolean hasAccessToken() {
        final String token = Cookies.getCookie(ACCESS_TOKEN_COOKIE_NAME);
        return token != null && token.length() > 0;
    }

    public static void logout() {
        Cookies.removeCookie(ACCESS_TOKEN_COOKIE_NAME);
    }
}
