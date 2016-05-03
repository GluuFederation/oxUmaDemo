package org.xdi.uma.demo.common.server;

import com.google.common.collect.Maps;
import org.xdi.uma.demo.common.gwt.Msg;

import java.util.*;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 14/05/2013
 */

public class LogList {

    /**
     * Specifies the number of log messages appended. Is incremented each time, a log is added to the linked list
     */
    private int usedSize = 0;
    /**
     * Size of the hash map
     */
    private int capacity;
    private Map<Long, Msg> map;
    private LinkedList<Msg> linkedList = new LinkedList<Msg>();
//    private static final DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LogList() {
    }

    public void setSize(int hashMapSize) {
        this.capacity = hashMapSize;
        map = Maps.newHashMapWithExpectedSize(hashMapSize);
    }

    /**
     * Inserts log messages to the linked list and the hash map.
     * If the number of log messages exceed the hashMapSize, new logs will be added after removing, the oldest log from the linked list and the hash map.
     *
     * @param p_msg the log entries
     */
    public synchronized void insert(Msg p_msg) {
        if (usedSize > capacity) {
            map.remove(linkedList.removeFirst().getId());
            map.put(p_msg.getId(), p_msg);
            linkedList.add(p_msg);
        } else {
            linkedList.add(p_msg);
            map.put(p_msg.getId(), p_msg);
            usedSize++;
        }
    }

//    public List<Msg> formatFields(List<Msg> list) {
//        int i = 0;
//        Msg entry;
//        String formattedString;
//
//        while (i < list.size()) {
//            entry = list.get(i);
//            if (entry.getFormattedDate() == null) {
//                synchronized (dfm) {
//                    formattedString = dfm.format(entry.getDate());
//                    entry.setFormattedDate(formattedString);
//                }
//            }
//
//            formattedString = entry.getMessage().replaceAll("( ){2}?", "  ");
//            entry.setFormattedMessage(formattedString);
//            i++;
//        }
//        return list;
//    }

    /**
     * When called returns the entire list of logs
     *
     * @return linkedList the list containing the logs
     */
    public List<Msg> getAll() {
        if (linkedList != null) {
//            return formatFields(linkedList);
            return linkedList;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * When called returns a sub list of the logs starting from the specified log id
     *
     * @param start starting id
     * @return list the list containing the sub list
     */
    public List<Msg> getListFrom(long start) {
        if (map.get(start) != null) {
            final List<Msg> list = new ArrayList<Msg>();
            long i = start;
            while (map.get(i) != null) {
                list.add(map.get(i));
                i++;
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    public synchronized void clear() {
        map.clear();
        linkedList.clear();
    }
}
