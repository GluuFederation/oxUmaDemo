package org.xdi.uma.demo.common.gwt;

import java.util.Date;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2013
 */

public class Msg implements java.io.Serializable {

    private long m_id;
    private String m_message;
    private String m_loggerName;
    private String m_logLevel;
    private Date m_date = new Date();
    private String m_formattedDate;
    private String m_formattedMessage;

    public Msg() {
    }

    public Msg(String p_message) {
        m_message = p_message;
    }

    public String getLogLevel() {
        return m_logLevel;
    }

    public void setLogLevel(String p_logLevel) {
        m_logLevel = p_logLevel;
    }

    public String getLoggerName() {
        return m_loggerName;
    }

    public void setLoggerName(String p_loggerName) {
        m_loggerName = p_loggerName;
    }

    public long getId() {
        return m_id;
    }

    public void setId(long p_id) {
        m_id = p_id;
    }

    public Date getDate() {
        return m_date;
    }

    public void setDate(Date p_date) {
        m_date = p_date;
    }

    public String getMessage() {
        return m_message;
    }

    public void setMessage(String p_message) {
        m_message = p_message;
    }

    public String getFormattedDate() {
        return m_formattedDate;
    }

    public void setFormattedDate(String p_formattedDate) {
        m_formattedDate = p_formattedDate;
    }

    public String getFormattedMessage() {
        return m_formattedMessage;
    }

    public void setFormattedMessage(String p_formattedMessage) {
        m_formattedMessage = p_formattedMessage;
    }
}
