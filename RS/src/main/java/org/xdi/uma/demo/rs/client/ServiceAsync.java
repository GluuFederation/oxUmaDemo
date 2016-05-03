package org.xdi.uma.demo.rs.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.xdi.uma.demo.common.gwt.Msg;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2016
 */

public interface ServiceAsync {

    void getMessageList(AsyncCallback<List<Msg>> async);

    void obtainNewPat(AsyncCallback<String> async);

    void clearLogs(AsyncCallback<Void> async);
}
