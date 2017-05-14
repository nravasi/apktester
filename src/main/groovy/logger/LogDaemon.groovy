package logger

import configuration.Command
import configuration.Config
import model.APK
import model.Execution

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created by nmravasi on 10/16/16.
 */
class LogDaemon {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> future;


    def notifyStart(Execution execution) {
        println("Instantiating daemon")
        def retriever = new LogRetriever(execution)
        future = executor.scheduleAtFixedRate(retriever, 10, 10, TimeUnit.SECONDS)
    }

    def notifyFinish() {
        println 'killing daemon'
        future.cancel(false);
    }

    private class LogRetriever implements Runnable {

        Execution execution;

        LogRetriever(Execution execution) {
            this.execution = execution;
        }

        int timestamp = 0; //This represents the nth minute where it's run

        @Override
        void run() {
            timestamp++;
            println("Executing retriever for iteration no.$timestamp")
            def cmd = "${Config.ADB_PATH} pull ${Config.SD_PATH_REL}logs/${execution.apk.appName}.txt ./res/${execution.folderName()}/log_${String.format("%05d", timestamp)}.txt"
            println cmd
            def run = Command.run(cmd);
            println("Retrieved log_${timestamp}.txt file")
        }
    }
}