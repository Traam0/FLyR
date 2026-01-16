package core.mvvm;


import java.util.ArrayList;
import java.util.List;

public final class CollectionProperty<T> {

    private final List<T> value;
    private final List<PropertyChangeListener> listeners;

    public CollectionProperty() {
        this.value = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }


    public CollectionProperty(List<T> value) {
        this.value = value;
        this.listeners = new ArrayList<>();
    }

    public List<T> get() {
        return this.value;
    }

    public void add(T value) {
        if (this.value.contains(value)) return;
        this.listeners.forEach(l -> l.onPropertyChange(this.value, value));
        this.value.add(value);
    }

    public void remove(T value) {
        if(!this.value.contains(value)) return;
        this.value.remove(value);
        this.listeners.forEach(l -> l.onPropertyChange(value, this.value));
    }

    public void addListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }
}
