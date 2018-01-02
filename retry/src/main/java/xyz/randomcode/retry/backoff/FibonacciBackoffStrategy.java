package xyz.randomcode.retry.backoff;

public class FibonacciBackoffStrategy extends AbstractBackoffStrategy {

    public FibonacciBackoffStrategy(long initialDelay) {
        super(initialDelay);
    }

    @Override
    public long getNextRetryDelayMillis(int currentAttempt) {
        return 0;
    }
}
