package xyz.randomcode.retry;

import android.os.Handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import xyz.randomcode.retry.actions.CompleteAction;
import xyz.randomcode.retry.actions.FailAction;
import xyz.randomcode.retry.actions.SuccessAction;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RetryTest {

    @Mock
    Handler handler;

    @Mock
    SuccessAction successAction;

    @Mock
    FailAction failAction;

    @Mock
    CompleteAction completeAction;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        reset(handler, successAction, failAction);
    }

    @Test
    public void callSuccess() throws Exception {
        RetryOptions options = RetryOptions.builder()
                .setInitialDelayMillis(0)
                .setMaxRetries(1)
                .build();

        Retry retry = new Retry(options, handler).withSuccessAction(successAction);

        retry.recordSuccess();

        verify(successAction).onSuccess();
    }

    @Test
    public void callFail() throws Exception {
        RetryOptions options = RetryOptions.builder()
                .setInitialDelayMillis(0)
                .setMaxRetries(1)
                .build();
        Retry retry = new Retry(options, handler).withFailAction(failAction);

        when(handler.postDelayed(any(Retry.RetryRunnable.class), anyLong())).thenAnswer(
                invocation -> {
                    Retry.RetryRunnable retryRunnable = invocation.getArgument(0);
                    retryRunnable.run();
                    return true;
                }
        );

        retry.recordFailure();

        assertThat(retry).hasFieldOrPropertyWithValue("currentRetry", 1);
        verify(handler).removeCallbacks(any(Retry.RetryRunnable.class));
        verify(handler).postDelayed(any(Retry.RetryRunnable.class), eq(options.initialDelayMillis()));
        verify(failAction).onFail();
    }

    @Test
    public void callComplete() throws Exception {
        RetryOptions options = RetryOptions.builder()
                .setInitialDelayMillis(0)
                .setMaxRetries(0)
                .build();
        Retry retry = new Retry(options, handler).withCompleteAction(completeAction);

        retry.recordFailure();

        verify(handler, never()).removeCallbacks(any(Runnable.class));
        verify(handler, never()).postDelayed(any(Retry.RetryRunnable.class), anyLong());
        verify(completeAction).onComplete();
    }
}
