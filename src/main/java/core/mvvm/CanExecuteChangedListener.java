package core.mvvm;

import java.util.EventListener;

@FunctionalInterface
public interface CanExecuteChangedListener extends EventListener {
    void canExecuteChanged();
}
