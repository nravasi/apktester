package runners

import configuration.Command
import configuration.Config
import configuration.Tool
import logger.LogAnalyzer
import logger.LogDaemon
import model.APK

/**
 * Created by nmravasi on 10/8/16.
 */
public abstract class AbstractRunner {

    protected apks;
    public boolean finished = false;
    LogDaemon loggerDaemon;

    protected AbstractRunner(apks, loggerDaemon) {
        this.apks = apks;
        this.loggerDaemon = loggerDaemon;
    }

    public static AbstractRunner getRunner(apks, loggerDaemon) {
        switch (Config.TOOL_TO_USE) {
            case Tool.MONKEY:
                return new MonkeyRunner(apks, loggerDaemon);
                break
            case Tool.SAPIENZ:
                return new SapienzRunner(apks, loggerDaemon);
                break
            case Tool.DROIDMATE:
                return new DroidmaterRunner(apks, loggerDaemon);
                break
        }
    }

    public void start() {

        beforeStart();

        apks.each { apk ->
            Config.times.times {
                beforeApk(apk)
                loggerDaemon.notifyStart(apk)
                testApk(apk)
                loggerDaemon.notifyFinish()
                afterApk(apk)
                done(apk)
            }
        }

        println "All APks runned OK"

        finished = true;
    }

    def done(APK apk) {
        def analyzer = new LogAnalyzer();
        def res = analyzer.processFiles();
        println(res)
        writeOutput(res, apk)
    }

    def writeOutput(HashMap hashMap, apk) {
        def outputFile = new File("./res/${apk.appName}_${Config.TOOL_TO_USE}.txt")
        outputFile << "Minutes\tMethods\n"
        hashMap.keySet().sort().each {
            outputFile << "${it}\t${hashMap[it]}\n"
        }
    }

    public void beforeStart() {
        Command.runAndRead("${Config.ADB_PATH} push utils/monitor_api19.apk /data/local/tmp/monitor.apk");
    };

    public abstract void testApk(APK apk);

    public void beforeApk(APK apk) {}

    public void afterApk(APK apk) {}
}
