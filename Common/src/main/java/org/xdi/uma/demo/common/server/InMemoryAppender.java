package org.xdi.uma.demo.common.server;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.server.ref.ILogList;
import org.xdi.util.InterfaceRegistry;

import java.util.Date;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 14/05/2013
 */

public class InMemoryAppender extends AppenderSkeleton {

    private volatile long id = 0;
    private final LogList logList = new LogList();

    public InMemoryAppender() {
        InterfaceRegistry.put(ILogList.class, logList);
    }

    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
    }

    public boolean requiresLayout() {
        return true;
    }

    protected boolean checkEntryConditions() {
        if (this.closed) {
            LogLog.warn("Not allowed to write to a closed appender.");
            return false;
        }

        if (this.layout == null) {
            errorHandler.error("No layout set for the appender named [" + name + "].");
            return false;
        }
        return true;
    }

    public void setSize(int size) {
        logList.setSize(size);
    }

    public void append(LoggingEvent event) {
        if (!checkEntryConditions()) {
            return;
        }
        subAppend(event);
    }

    protected void subAppend(LoggingEvent event) {
        int index = event.getLoggerName().lastIndexOf('.');
        String loggerName;

        if (index > -1) {
            loggerName = event.getLoggerName().substring(index + 1);
        } else {
            loggerName = event.getLoggerName();
        }

        final Level level = event.getLevel();
        if (level != Level.TRACE) {
            Msg log = new Msg();
            log.setId(id++);

            log.setLoggerName(loggerName);
            log.setMessage((String) event.getMessage());
            log.setDate(new Date(event.getTimeStamp()));
            log.setLogLevel(level.toString());
            log.setFormattedMessage(getLayout().format(event));

            final String[] throwableStrRep = event.getThrowableStrRep();
            if (level == Level.ERROR && ArrayUtils.isNotEmpty(throwableStrRep)) {
                if (layout.ignoresThrowable()) {
                    final StringBuilder buf = new StringBuilder(log.getFormattedMessage());
                    for (String aThrowableStrRep : throwableStrRep) {
                        buf.append(aThrowableStrRep);
                        buf.append("\r\n");
                    }

                    log.setFormattedMessage(buf.toString());
                }
            }

            logList.insert(log);
        }
    }
}
