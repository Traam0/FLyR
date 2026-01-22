package core.mvvm;

public interface Command {
    void execute(Object param);

    boolean canExecute(Object prams);

    public void canExecuteChanged();

    void subscribeChangeListener(CanExecuteChangedListener listener);

    void unsubscribeChangeListener(CanExecuteChangedListener listener);
}
