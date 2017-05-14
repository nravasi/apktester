package runners

import configuration.Command
import configuration.Config
import configuration.Tool
import logger.LogAnalyzer
import logger.LogDaemon
import model.APK
import model.Execution

/**
 * Created by nmravasi on 10/8/16.
 */
public abstract class AbstractRunner {

    protected Execution execution;
    protected apk;
    public boolean finished = false;
    LogDaemon loggerDaemon;

    protected AbstractRunner(apk, loggerDaemon) {
        this.execution = new Execution(apk)
        this.apk = apk
        this.loggerDaemon = loggerDaemon;
    }

    public static AbstractRunner getRunner(apks, loggerDaemon) {
        switch (Config.toolToUse) {
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

        beforeApk(apk)
        loggerDaemon.notifyStart(execution)
        testApk(apk)
        loggerDaemon.notifyFinish()
        afterApk(apk)
        done(apk)

        finished = true;
    }

    def done(APK apk) {
        def analyzer = new LogAnalyzer(execution);
        def res = analyzer.processFiles();
        println(res)
        writeOutput(res, analyzer.methodList)

        if(Config.initEmulator){
            Command.runAndRead("${Config.ADB_PATH} -s emulator-5554 emu kill")
        }
    }

    def writeOutput(HashMap hashMap, HashSet methods) {
        def outputFile = new File("./res/${execution.folderName()}/summary.txt")
        outputFile << "Seconds\tMethods\n"
        hashMap.keySet().sort().each {
            outputFile << "${it}\t${hashMap[it]}\n"
        }

        def methodsFile = new File("./res/${execution.folderName()}/summaryMethods.txt")

        methods.each {
            methodsFile << "${it}\n"
        }
    }

    public void beforeStart() {

        if (Config.initEmulator) {
            println 'starting emulator'
            Command.run("nohup ${Config.ANDROID_TOOLS_PATH}emulator -avd ${Config.EMULATOR_NAME} -wipe-data")
            println 'waiting for device'
            Command.runAndRead("${Config.ADB_PATH} wait-for-device")
            Thread.sleep(15000);
        }

        println 'pushing apk'
        Command.runAndRead("${Config.ADB_PATH} push utils/monitor_api19.apk /data/local/tmp/monitor.apk");


        println 'deleting logs'
        Command.run("${Config.ADB_PATH} shell rm -rf ${Config.SD_PATH}logs")

        new File("res/${execution.folderName()}").mkdirs()
    };

    public abstract void testApk(APK apk);

    public void beforeApk(APK apk) {}

    public void afterApk(APK apk) {}
}
