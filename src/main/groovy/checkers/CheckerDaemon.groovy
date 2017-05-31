package checkers

import configuration.Config

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created by nmravasi on 5/17/17.
 */
class CheckerDaemon {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> future;

    def scheduleCheck(){
        def killer = new Killer();
        future = executor.schedule(killer, Config.minutes + 5, TimeUnit.MINUTES)
    }

    def suspendCheck(){
        future.cancel(true);
    }

    private class Killer implements Runnable{

        @Override
        void run() {
            throw new Exception("Suspend everything! Time was exceeded")
        }
    }
}
