/*
 * Copyright 2017 Sergei Munovarov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.randomcode.retry;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.lang.ref.WeakReference;

import xyz.randomcode.retry.actions.CompleteAction;
import xyz.randomcode.retry.actions.FailAction;
import xyz.randomcode.retry.actions.SuccessAction;

public class Retry {

    private final Handler handler;
    private final RetryRunnable retryRunnable;
    private final RetryOptions retryOptions;

    private WeakReference<SuccessAction> successActionRef;
    private WeakReference<FailAction> failActionRef;
    private WeakReference<CompleteAction> completeActionRef;

    private int currentRetry = 0;

    @VisibleForTesting
    Retry(@NonNull RetryOptions retryOptions,
                  @NonNull Handler handler) {
        this.handler = handler;
        this.retryOptions = retryOptions;
        this.retryRunnable = new RetryRunnable();
    }

    public static Retry create(@NonNull RetryOptions retryOptions) {
        return create(retryOptions, Looper.getMainLooper());
    }

    public static Retry create(@NonNull RetryOptions retryOptions,
                               @NonNull Looper looper) {
        return new Retry(retryOptions, new Handler(looper));
    }

    public Retry withSuccessAction(@NonNull SuccessAction successAction) {
        successActionRef = new WeakReference<>(successAction);
        return this;
    }

    public Retry withFailAction(@NonNull FailAction failAction) {
        failActionRef = new WeakReference<>(failAction);
        return this;
    }

    public Retry withCompleteAction(@Nullable CompleteAction completeAction) {
        completeActionRef = new WeakReference<>(completeAction);
        return this;
    }

    public void recordFailure() {
        if (currentRetry < retryOptions.maxRetries()) {
            handler.removeCallbacks(retryRunnable);
            handler.postDelayed(retryRunnable, retryOptions.initialDelayMillis() * currentRetry++);
        } else {
            CompleteAction action = completeActionRef.get();
            if (action != null) action.onComplete();
        }
    }

    public void recordSuccess() {
        currentRetry = 0;
        SuccessAction action = successActionRef.get();
        if (action != null) action.onSuccess();
    }

    @VisibleForTesting
    class RetryRunnable implements Runnable {
        @Override
        public void run() {
            FailAction action = failActionRef.get();
            action.onFail();
        }
    }
}
