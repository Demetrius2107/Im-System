package com.lip.im.imservice.utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Elon
 * @title: ShareThreadPool
 * @projectName: IM-System
 * @description: 共享线程池
 * @date: 2025/3/6 19:22
 */
@Service
public class ShareThreadPool {

    private Logger logger = LoggerFactory.getLogger(ShareThreadPool.class);

    private final ThreadPoolExecutor threadPoolExecutor;

    {
        final AtomicInteger tNum = new AtomicInteger(0);

        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2 << 20), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {

                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("SHARE-Processor-" + tNum.getAndIncrement());
                return t;
            }
        });
    }

    private AtomicLong ind = new AtomicLong(0);

    public void submit(Runnable r) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        ind.incrementAndGet();

        threadPoolExecutor.submit(() -> {
            long start = System.currentTimeMillis();
            try {
                r.run();
            } catch (Exception e) {
                logger.error("ShareThreadPool_ERROR", e);
            } finally {
                long end = System.currentTimeMillis();
                long dur = end - start;
                long andDecrement = ind.decrementAndGet();
                if (dur > 1000) {
                    logger.warn("ShareThreadPool executed taskDone,remanent num = {},slow task fatal warning,costs time = {},stack: {}", andDecrement, dur, stackTrace);
                } else if (dur > 300) {
                    logger.warn("ShareThreadPool executed taskDone,remanent num = {},slow task warning: {},costs time = {},", andDecrement, r, dur);
                } else {
                    logger.debug("ShareThreadPool executed taskDone,remanent num = {}", andDecrement);
                }
            }
        });
    }
}
