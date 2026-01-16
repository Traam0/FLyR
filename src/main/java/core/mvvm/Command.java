package core.mvvm;

public interface Command {
    void execute(Object param);
    boolean canExecute(Object prams);
}
