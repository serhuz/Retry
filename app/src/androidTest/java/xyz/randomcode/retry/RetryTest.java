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

import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;

import org.junit.Test;

import xyz.randomcode.retry.actions.CompleteAction;
import xyz.randomcode.retry.actions.FailAction;
import xyz.randomcode.retry.actions.SuccessAction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RetryTest {

    @Test
    public void invokeSuccessAction() throws Exception {
        RetryOptions options = RetryOptions.builder()
                .setInitialDelay(0)
                .setMaxRetries(1)
                .build();
        SuccessAction mockAction = mock(SuccessAction.class);

        Retry retry = Retry.create(options).withSuccessAction(mockAction);
        retry.recordSuccess();

        verify(mockAction, times(1)).onSuccess();
    }

    @Test
    public void invokeFailAction() throws Exception {
        CountingIdlingResource idlingResource = new CountingIdlingResource("Fail action");
        Espresso.registerIdlingResources(idlingResource);

        RetryOptions options = RetryOptions.builder()
                .setInitialDelay(0)
                .setMaxRetries(1)
                .build();
        FailAction action = new TestFailAction(idlingResource);
        FailAction spy = spy(action);

        Retry retry = Retry.create(options).withFailAction(spy);
        idlingResource.increment();
        retry.recordFailure();

        verify(spy, times(1)).onFail();
    }

    @Test
    public void invokeCompleteAction() throws Exception {
        RetryOptions options = RetryOptions.builder()
                .setInitialDelay(0)
                .setMaxRetries(1)
                .build();
        FailAction mockFailAction = mock(FailAction.class);
        CompleteAction mockCompleteAction = mock(CompleteAction.class);

        Retry retry = Retry.create(options)
                .withFailAction(mockFailAction)
                .withCompleteAction(mockCompleteAction);
        retry.recordFailure();
        verify(mockFailAction, times(1)).onFail();

        retry.recordFailure();
        verify(mockCompleteAction, times(1)).onComplete();
    }

    public static class TestFailAction implements FailAction {
        CountingIdlingResource idlingResource;

        public TestFailAction(CountingIdlingResource idlingResource) {
            this.idlingResource = idlingResource;
        }

        @Override
        public void onFail() {
            idlingResource.decrement();
        }
    }

}