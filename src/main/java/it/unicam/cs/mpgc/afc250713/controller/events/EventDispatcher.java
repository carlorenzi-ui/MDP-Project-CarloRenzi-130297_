package it.unicam.cs.mpgc.afc250713.controller.events;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple property change dispatcher that also keeps a history of game events
 * (the student's diary of the fuoricorso year).
 */
public class EventDispatcher {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final List<String> eventLog = new ArrayList<>();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Appends a message to the internal log and fires an event so the UI can update.
     */
    public void logEvent(String message) {
        String log = "- " + message;
        this.eventLog.add(log);
        support.firePropertyChange("eventLogAdded", null, log);
    }

    /**
     * Returns a read-only view of the past events.
     */
    public List<String> getEventLog() {
        return Collections.unmodifiableList(this.eventLog);
    }
}
