package core.mvvm;

@FunctionalInterface
public interface PropertyChangeListener {
    void onPropertyChange(Object oldValue, Object newValue);
}
