package logger

import configuration.Command
import configuration.Config
import model.APK

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


    def notifyStart(APK apk) {
        println("Instantiating daemon")
        def retriever = new LogRetriever(apk)
        future = executor.scheduleAtFixedRate(retriever, 1, 1, TimeUnit.MINUTES)
    }

    def notifyFinish() {
        future.cancel(false);
    }

    private class LogRetriever implements Runnable {

        APK apk;

        LogRetriever(APK apk) {
            this.apk = apk;
        }

        int timestamp = 0; //This represents the nth minute where it's run

        @Override
        void run() {
            timestamp++;
            println("Executing retriever for iteration no.$timestamp")
            def cmd = "${Config.ADB_PATH} pull ${Config.SD_PATH_REL}logs/${apk.appName}.txt tmp/${apk.appName}_${timestamp}.txt"
            def run = Command.run(cmd);
            println("Retrieved ${apk.appName}_${timestamp}.txt file")
        }
    }


    private class LogGetter implements Runnable {

        APK apk;

        LogGetter(APK apk) {
            this.apk = apk;
        }

        int timestamp = 0; //This represents the nth minute where it's run

        @Override
        void run() {
            timestamp++;
            println("Executing retriever for iteration no.$timestamp")
            def cmd = "${Config.ADB_PATH} pull ${Config.SD_PATH}logs/${apk.appName}.txt tmp\\${apk.appName}_${timestamp}.txt"
            def run = Command.run(cmd);
            println("Retrieved ${apk.appName}.txt file for the ${timestamp} iteration")

        }
    }

}
