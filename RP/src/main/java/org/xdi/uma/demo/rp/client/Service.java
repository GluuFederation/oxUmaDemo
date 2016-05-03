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
    public List<Msg> getMessageList();

    public String obtainNewAat();

    public String obtainNewRpt();

    public void clearLogs();

    public Phones demo();

    public Phones getPhoneList();

    public boolean removePhone(String p_phone);

    public boolean addPhone(String p_phone);

    public void clearState();

    public String getLoginUrl();

    public void storeAat(String p_accessToken);

    public Conf getConf();
}
