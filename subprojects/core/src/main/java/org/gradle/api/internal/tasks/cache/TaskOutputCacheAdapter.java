/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.cache;

import org.gradle.api.Task;
import org.gradle.api.execution.TaskOutputCacheListener;
import org.gradle.api.internal.tasks.cache.diagnostics.TaskCached;
import org.gradle.api.internal.tasks.cache.diagnostics.TaskNotCached;
import org.gradle.api.internal.tasks.cache.diagnostics.TaskOutputCacheDiagnostics;
import org.gradle.api.internal.tasks.cache.diagnostics.TaskOutputCacheDiagnosticsListener;
import org.gradle.initialization.BuildCompletionListener;

public class TaskOutputCacheAdapter implements TaskOutputCacheListener, BuildCompletionListener {
    private TaskOutputCacheDiagnostics diagnostics;
    private TaskOutputCacheDiagnosticsListener listener;

    public TaskOutputCacheAdapter(TaskOutputCacheDiagnosticsListener listener) {
        this.listener = listener;
        diagnostics = new TaskOutputCacheDiagnostics();
    }

    @Override
    public void fromCache(Task task) {
        diagnostics.event(new TaskCached(task));
    }

    @Override
    public void notCached(Task task, NotCachedReason reason) {
        diagnostics.event(new TaskNotCached(task, reason));
    }

    @Override
    public void completed() {
        if (diagnostics != null) {
            try {
                listener.buildFinished(diagnostics);
            } finally {
                diagnostics = new TaskOutputCacheDiagnostics();
            }
        }
    }
}
