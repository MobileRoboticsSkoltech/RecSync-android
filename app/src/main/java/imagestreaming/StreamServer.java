package imagestreaming;

public abstract class StreamServer extends Thread {
    public abstract boolean isExecuting();

    public abstract void stopExecuting();
}
