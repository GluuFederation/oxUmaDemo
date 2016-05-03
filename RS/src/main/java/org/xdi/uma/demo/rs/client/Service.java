package org.xdi.uma.demo.rs.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.xdi.uma.demo.common.gwt.Msg;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2016
 */
@RemoteServiceRelativePath("rsService")
public interface Service extends RemoteService {

    List<Msg> getMessageList();

    String obtainNewPat();

    void clearLogs();
}
