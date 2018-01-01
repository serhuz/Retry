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

import android.support.annotation.IntRange;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RetryOptions {

    public static RetryOptions.Builder builder() {
        return new AutoValue_RetryOptions.Builder();
    }

    public abstract int maxRetries();

    public abstract long initialDelayMillis();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder setMaxRetries(@IntRange(from = 0) int maxRetries);

        public abstract Builder setInitialDelayMillis(@IntRange(from = 0) long initialDelayMillis);

        public abstract RetryOptions build();
    }
}
