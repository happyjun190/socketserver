package com.socketserver.thrack.server;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.stereotype.Component;

/**
 * Spring's bean is a singleton
 * Created by wushenjun on 2017/3/30.
 */
@Component
public class ExecutorGroupFactory {

    private static ExecutorGroupFactory executorGroupFactory;

    //异步发送请求group
    private EventExecutorGroup ayncReqInvtTaskGroup;
    private Object ayncReqInvtTaskGroupLockObject = new Object();

    //异步插入数据group
    private EventExecutorGroup writingDBTaskGroup;
    private Object writingDBTaskGroupLockObject = new Object();

    private ExecutorGroupFactory() {
    }

    //初始化ExecutorGroupFactory
    public static ExecutorGroupFactory getInstance() {
        if (executorGroupFactory == null) {
            synchronized (ExecutorGroupFactory.class) {
                executorGroupFactory = new ExecutorGroupFactory();
            }
        }
        return executorGroupFactory;
    }

    public EventExecutorGroup getAyncReqInvtTaskGroup() {
        if (ayncReqInvtTaskGroup == null) {
            synchronized(ayncReqInvtTaskGroupLockObject) {
                if (ayncReqInvtTaskGroup == null) {
                    ayncReqInvtTaskGroup = new DefaultEventExecutorGroup(4);
                }
            }
        }
        return ayncReqInvtTaskGroup;
    }


    public EventExecutorGroup getWritingDBTaskGroup() {
        if (writingDBTaskGroup == null) {
            synchronized(writingDBTaskGroupLockObject) {
                if (writingDBTaskGroup == null) {
                    writingDBTaskGroup = new DefaultEventExecutorGroup(4);
                }
            }
        }
        return writingDBTaskGroup;
    }


}
