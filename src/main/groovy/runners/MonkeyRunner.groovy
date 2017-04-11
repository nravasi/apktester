package runners

import configuration.Command
import configuration.Config
import configuration.ADB
import configuration.Command
import configuration.Config
import model.APK

/**
 * Created by nmravasi on 10/8/16.
 */
class MonkeyRunner extends runners.AbstractRunner {

    protected MonkeyRunner(apks, daemon) {
        super(apks, daemon)


    }

    boolean doRun = true;

    @Override
    void beforeApk(APK apk) {
        super.beforeApk();

        //remove the apk before install it, if it exists
        if (ADB.IsAPKInstalled(apk.packageName)) {

            ADB.RemoveAPK(apk.packageName);
        }

        ADB.InstallAPK(apk.packageName, apk.file.toString());
    }

    @Override
    void beforeStart() {
        super.beforeStart()
    }

    @Override
    void afterApk(APK apk) {
        super.afterApk(apk)
        ADB.RemoveAPK(apk.packageName);
    }

    @Override
    void testApk(APK apk) {
        println "Running MONKEY";

        def monkeyCmd = "${Config.ADB_PATH} shell monkey -p ${apk.packageName} -v ${2000000} --ignore-crashes"

        def process = Command.run(monkeyCmd)

        process.waitForOrKill(Config.minutes * 60000)
        //This kills monkey running on the device
        Command.run("${Config.ADB_PATH} shell ps | awk '/com\\.android\\.commands\\.monkey/ { system(\"adb shell kill \" \$2) }'")

        println "Monkey finished OK";
        /* Command.run(monkeyCmd);*/
    }
}
