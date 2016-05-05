package org.xdi.uma.demo.rp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.xdi.uma.demo.rp.shared.Conf;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.gwt.Phones;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 20/05/2013
 */

public interface ServiceAsync {
    void getMessageList(AsyncCallback<List<Msg>> async);

    void obtainNewAatViaClientAuthentication(AsyncCallback<String> async);

    void clearLogs(AsyncCallback<Void> p_asyncCallback);

    void clearState(AsyncCallback<Void> p_asyncCallback);

    void demo(AsyncCallback<Phones> async);

    void addPhone(String p_phone, AsyncCallback<Boolean> async);

    void getPhoneList(AsyncCallback<Phones> async);

    void obtainNewRpt(AsyncCallback<String> async);

    void removePhone(String p_phone, AsyncCallback<Boolean> async);

    void getLoginUrl(AsyncCallback<String> async);

    void storeAat(String p_accessToken, AsyncCallback<Void> async);

    void getConf(AsyncCallback<Conf> async);
}
