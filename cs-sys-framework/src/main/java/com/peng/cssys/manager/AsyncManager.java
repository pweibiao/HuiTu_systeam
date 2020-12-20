package com.peng.cssys.manager;

import com.peng.cssys.utils.SpringUtils;
import com.peng.cssys.utils.Threads;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 */
public class AsyncManager {
    /**
     * 延迟10ms
     */
    private final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作任务调度线程池
     */
    private ScheduledExecutorService executor = SpringUtils.getBean("scheduledExecutorService");

    //单例模式
    private AsyncManager(){}

    private static AsyncManager me = new AsyncManager();

    public static AsyncManager me(){
        return me;
    }

    /**
     * 执行任务
     * @param task
     */
    public void execute(TimerTask task){
        executor.schedule(task,OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止线程池
     */
    public void shutdown(){
        Threads.shutdownAndAwaitTermination(executor);
    }


}
