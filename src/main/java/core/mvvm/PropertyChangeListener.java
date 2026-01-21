package core.mvvm;

@FunctionalInterface
public interface PropertyChangeListener<T> {
    void onPropertyChange(T oldValue, T newValue);
}
