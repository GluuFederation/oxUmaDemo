package org.xdi.uma.demo.rp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.xdi.uma.demo.rp.shared.Conf;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.gwt.Phones;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 20/05/2013
 */
@RemoteServiceRelativePath("rpService")
public interface Service extends RemoteService {

    List<Msg> getMessageList();

    String obtainNewAatViaClientAuthentication();

    String obtainNewRpt();

    void clearLogs();

    Phones demo();

    Phones getPhoneList();

    boolean removePhone(String p_phone);

    boolean addPhone(String p_phone);

    void clearState();

    String getLoginUrl();

    void storeAat(String p_accessToken);

    Conf getConf();

    Phones viewWithGat();
}
