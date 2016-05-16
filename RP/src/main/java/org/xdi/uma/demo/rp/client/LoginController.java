package org.xdi.uma.demo.rp.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.xdi.gwt.client.GwtUtils;
import org.xdi.uma.demo.rp.client.events.LoginEvent;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2013
 */

public class LoginController {

    private static String aat = null;

    /**
     * Sets login cookie.
     *
     * @param accessToken access token
     */
    public static void storeAat(String accessToken) {
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

    public static void clientAuthentication() {
        if (!hasAccessToken()) {
            RP.getService().obtainNewAatViaClientAuthentication(new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    GwtUtils.showError("Unable to obtain AAT via client authentication.");
                }

                @Override
                public void onSuccess(String result) {
                    aat = result;
                    RP.getEventBus().fireEvent(new LoginEvent(true));
                }
            });
        }
    }

    public static void userLogin() {
        if (!hasAccessToken()) {
            final String accessToken = parseAccessToken();
            if (accessToken != null && accessToken.length() > 0 && !LoginController.hasAccessToken()) {
                LoginController.storeAat(accessToken);
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
        return aat;
    }

    /**
     * Returns whether access token is saved in cookie.
     *
     * @return whether access token is saved in cookie
     */
    public static boolean hasAccessToken() {
        return aat != null && aat.length() > 0;
    }

    public static void logout() {
        aat = null;
    }
}
