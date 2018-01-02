package xyz.randomcode.retry.backoff;

public class ExponentialBackoffStrategy extends AbstractBackoffStrategy {

    public ExponentialBackoffStrategy(long initialDelay) {
        super(initialDelay);
    }

    @Override
    public long getNextRetryDelayMillis(int currentAttempt) {
        return 0;
    }
}
