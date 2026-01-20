package core.mvvm;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class RelayCommand implements Command {
    private final Consumer<Object> execute;
    private final Predicate<Object> canExecute;
    private final List<CanExecuteChangedListener> listeners;

    public RelayCommand(Consumer<Object> execute, Predicate<Object> canExecute) {
        this.execute = execute;
        this.canExecute = canExecute;
        this.listeners = new ArrayList<>();
    }

    @Override
    public void execute(Object param) {
        this.execute.accept(param);
    }

    @Override
    public boolean canExecute(Object param) {
        return this.canExecute.test(param);
    }

    public void canExecuteChanged() {
        this.listeners.forEach(CanExecuteChangedListener::canExecuteChanged);
    }

    public void subscribeChangeListener(CanExecuteChangedListener listener) {
        if (this.listeners.contains(listener)) return;
        this.listeners.add(listener);
    }

    public void unsubscribeChangeListener(CanExecuteChangedListener listener) {
        this.listeners.remove(listener);
    }

}
