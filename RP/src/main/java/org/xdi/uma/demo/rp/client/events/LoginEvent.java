package org.xdi.uma.demo.rp.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2013
 */

public class LoginEvent extends GwtEvent<LoginEvent.Handler> {

    /**
     * Handler of event.
     */
    public interface Handler extends EventHandler {

        /**
         * Handles state based on event object.
         *
         * @param p_event event object
         */
        void update(LoginEvent p_event);
    }

    /**
     * Type of event
     */
    public static final Type<Handler> TYPE = new Type<Handler>();

    private boolean login;

    public LoginEvent(boolean login) {
        this.login = login;
    }

    public boolean isLogin() {
        return login;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Dispatches handling to handler object.
     *
     * @param handler handler object
     */
    @Override
    protected void dispatch(Handler handler) {
        handler.update(this);
    }
}
