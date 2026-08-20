package org.privates.client.api.interfaces;

import org.privates.common.impl.thread.ThreadPool;

public interface IExecutor {
    ThreadPool THREAD_POOL = new ThreadPool();
}
