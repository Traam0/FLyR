package core.mvvm;

import java.util.*;

public class Property<T> {
    private T value;
    private final EnumSet<PropertyFlags> flags;
    private final List<PropertyChangeListener<T>> subscribers;

    public Property(T value) {
        this(value, PropertyFlags.NONE);
    }

    public Property(T value, PropertyFlags... flags) {
        this.value = value;
        this.flags = flags.length == 0
                ? EnumSet.of(PropertyFlags.NONE)
                : EnumSet.noneOf(PropertyFlags.class);
        Collections.addAll(this.flags, flags);
        this.subscribers = new ArrayList<>();
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        if (this.flags.contains(PropertyFlags.READ_ONLY) && this.value != null)
            throw new IllegalArgumentException("Cannot set read-only property");

        if (this.flags.contains(PropertyFlags.DISTINCT_VALUE) && this.value == value) return;
        if (this.flags.contains(PropertyFlags.EQUALS_VALUE) && Objects.equals(this.value, value)) return;

        var old = this.value;
        this.value = value;

        if (!this.flags.contains(PropertyFlags.SILENT_UPDATES))
            this.subscribers.forEach(sub -> sub.onPropertyChange(old, value));
    }

    public void subscribe(PropertyChangeListener<T> subscriber) {
        this.subscribers.add(subscriber);
        if (flags.contains(PropertyFlags.REPLAY_LAST)) {
            subscriber.onPropertyChange(this.value, this.value);
        }
    }

    public void unsubscribe(PropertyChangeListener<T> subscriber) {
        this.subscribers.remove(subscriber);
    }
}
