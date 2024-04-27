package edu.vn.hcmuaf.layer2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ThreadManage {
    private static final ThreadManage install = new ThreadManage();
        private final ExecutorService executorService = Executors.newWorkStealingPool(80);
//    private final ExecutorService executorService = Executors.newFixedThreadPool(30);

    private ThreadManage() {
    }

    public static ThreadManage me() {
        return install;
    }

    public void execute(Runnable r) {
        executorService.execute(r);
    }

    public Future submit(Runnable r) {
        return executorService.submit(r);
    }

    //    @SneakyThrows
    public void waitThreadDone() {
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}