package core.dependencyInjection;

public interface ServiceProvider
{
    <T> T getService(Class<? extends T> type);
    <T> T getRequiredService(Class<? extends T> type);
}
