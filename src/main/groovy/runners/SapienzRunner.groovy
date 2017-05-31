package runners

import configuration.ADB
import configuration.Command
import configuration.Config
import model.APK
import model.StreamGobbler

/**
 * Created by nmravasi on 10/8/16.
 */
class SapienzRunner extends AbstractRunner {
    protected SapienzRunner(apks, daemon) {
        super(apks, daemon)
    }


    @Override
    void beforeStart() {
        super.beforeStart()
    }

    @Override
    void testApk(APK apk) {
        println("Starting sapienz for apk ${apk.appName}")

        def process = Command.run("python ${Config.SAPIENZ_DIR}main.py ${apk.file.absolutePath}")
        StreamGobbler errorGobbler = new
                StreamGobbler(process.getErrorStream(), "ERROR");

        // any output?
        StreamGobbler outputGobbler = new
                StreamGobbler(process.getInputStream(), "OUTPUT");

        // kick them off
        errorGobbler.start();
        outputGobbler.start();
        process.waitForOrKill(Config.minutes * 60000)
        println("Terminating sapienz for apk ${apk.appName}")

    }

    @Override
    def done(APK apk) {
        super.done(apk)
        ADB.RemoveAPK(apk.packageName);
        Thread.sleep(15000);
        Command.run("${Config.ADB_PATH} shell reboot");
        Thread.sleep(30000);

    }

    @Override
    void beforeApk(APK apk) {
        super.beforeApk()

        def rmSd = "${Config.ADB_PATH} shell rm -f " + Config.SD_PATH

        Command.run(rmSd + 'motifcore*', rmSd + '*strings.xml', rmSd + 'activity.coverage', rmSd + 'skin.coverage')
    }
}
