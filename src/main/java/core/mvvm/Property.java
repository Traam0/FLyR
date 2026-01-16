package core.mvvm;

import java.util.ArrayList;
import java.util.List;

public class Property<T> {
    private T value;
    private final List<PropertyChangeListener> subscribers;

    public Property(T value) {
        this.value = value;
        this.subscribers = new ArrayList<>();
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        if (this.value == value) return;
        this.subscribers.forEach(sub -> sub.onPropertyChange(this.value, value));
        this.value = value;
    }

    public void subscribe(PropertyChangeListener subscriber) {
        this.subscribers.add(subscriber);
    }

    public void unsubscribe(PropertyChangeListener subscriber) {
        this.subscribers.remove(subscriber);
    }
}
