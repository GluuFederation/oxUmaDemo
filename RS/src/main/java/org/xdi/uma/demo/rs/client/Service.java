package org.xdi.uma.demo.rs.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.xdi.uma.demo.common.gwt.Msg;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2013
 */
@RemoteServiceRelativePath("rsService")
public interface Service extends RemoteService {

    public List<Msg> getMessageList();

    public String obtainNewPat();

    public void clearLogs();
}
