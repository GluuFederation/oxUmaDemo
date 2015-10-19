package org.xdi.uma.demo.rp.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2013
 */

public class LogoutEvent extends GwtEvent<LogoutEvent.Handler> {

    /**
     * Handler of event.
     */
    public static interface Handler extends EventHandler {

        /**
         * Handles state based on event object.
         *
         * @param p_event event object
         */
        void update(LogoutEvent p_event);
    }

    /**
     * Type of event
     */
    public static final GwtEvent.Type<Handler> TYPE = new GwtEvent.Type<Handler>();


    @Override
    public GwtEvent.Type<Handler> getAssociatedType() {
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