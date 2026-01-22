package core.mvvm;

import java.util.*;
import java.util.function.Consumer;

public class IterableProperty<T> {
    private List<T> value;
    private final EnumSet<PropertyFlags> flags;
    private final List<PropertyChangeListener<List<T>>> subscribers;

    public IterableProperty() {
        this(new ArrayList<>(), PropertyFlags.NONE);
    }

    public IterableProperty(PropertyFlags... flags) {
        this(new ArrayList<>(), flags);
    }

    public IterableProperty(List<T> value) {
        this(value, PropertyFlags.NONE);
    }

    public IterableProperty(List<T> value, PropertyFlags... flags) {
        this.value = value != null ? value : new ArrayList<>();
        this.flags = flags.length == 0
                ? EnumSet.of(PropertyFlags.NONE)
                : EnumSet.noneOf(PropertyFlags.class);
        Collections.addAll(this.flags, flags);
        this.subscribers = new ArrayList<>();
    }

    public List<T> get() {
        return this.value;
    }

    public void set(List<T> value) {
        if (this.flags.contains(PropertyFlags.READ_ONLY))
            throw new IllegalArgumentException("Cannot set read-only property");

        if (this.flags.contains(PropertyFlags.DISTINCT_VALUE) && this.value.equals(value)) return;
        if (this.flags.contains(PropertyFlags.EQUALS_VALUE) && Objects.equals(this.value, value)) return;

        var old = this.value;
        this.value = value != null ? value : new ArrayList<>();

        if (!this.flags.contains(PropertyFlags.SILENT_UPDATES))
            this.subscribers.forEach(sub -> sub.onPropertyChange(old, this.value));
    }

    public void add(T item) {
        if (this.flags.contains(PropertyFlags.READ_ONLY))
            throw new IllegalArgumentException("Cannot modify read-only property");

        var old = new ArrayList<>(this.value);
        this.value.add(item);

        if (!this.flags.contains(PropertyFlags.SILENT_UPDATES))
            this.subscribers.forEach(sub -> sub.onPropertyChange(old, this.value));
    }

    public void remove(T item) {
        if (this.flags.contains(PropertyFlags.READ_ONLY))
            throw new IllegalArgumentException("Cannot modify read-only property");

        var old = new ArrayList<>(this.value);
        this.value.remove(item);

        if (!this.flags.contains(PropertyFlags.SILENT_UPDATES))
            this.subscribers.forEach(sub -> sub.onPropertyChange(old, this.value));
    }

    public void subscribe(PropertyChangeListener<List<T>> subscriber) {
        this.subscribers.add(subscriber);
        if (flags.contains(PropertyFlags.REPLAY_LAST)) {
            subscriber.onPropertyChange(this.value, this.value);
        }
    }

    public void unsubscribe(PropertyChangeListener<List<T>> subscriber) {
        this.subscribers.remove(subscriber);
    }

    // Optionally, you can add methods to modify specific indices
    public void updateAt(int index, T newValue) {
        if (this.flags.contains(PropertyFlags.READ_ONLY))
            throw new IllegalArgumentException("Cannot modify read-only property");

        var old = new ArrayList<>(this.value);
        this.value.set(index, newValue);

        if (!this.flags.contains(PropertyFlags.SILENT_UPDATES))
            this.subscribers.forEach(sub -> sub.onPropertyChange(old, this.value));
    }

    public void forEachItem(Consumer<T> consumer) {
        for (T item : value) {
            consumer.accept(item);
        }
    }
}
