package runners

import configuration.Command
import configuration.Config
import configuration.Command
import configuration.Config
import model.APK
import model.StreamGobbler
import org.apache.commons.io.FileUtils

import java.nio.file.Paths
import java.util.regex.Pattern

/**
 * Created by nmravasi on 10/8/16.
 */
class DroidmaterRunner extends AbstractRunner {

    def timeLimitPattern = Pattern.compile(".*timeLimit (\\d+).*")
    def timeToRun = Config.minutes * 60 * 1000
    def bootUpTime = 60000
    private File apksDir

    protected DroidmaterRunner(apks, daemon) {
        super(apks, daemon)
    }

    @Override
    void testApk(APK apk) {
        def droidmateCmd = Config.DROIDMATE_DIR + 'gradlew -p ' + Config.DROIDMATE_DIR + ' :p:com:run'

        while (timeToRun > 0) {
            def process = Command.run(droidmateCmd);

            StreamGobbler errorGobbler = new
                    StreamGobbler(process.getErrorStream(), "ERROR");

            // any output?
            StreamGobbler outputGobbler = new
                    StreamGobbler(process.getInputStream(), "OUTPUT");

            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            println("Executing droidmate for ${apk.appName}")
            def startTime = System.currentTimeMillis()
            def res = process.waitFor()
            def duration = System.currentTimeMillis() - startTime
            timeToRun -= duration
        }

        println("Finished testing apk ${apk.appName}")

    }

    @Override
    void beforeStart() {
        super.beforeStart()

        println("!!!!±±±!±Modifying args file to specify running minutes")

        def argsFile = Paths.get(Config.DROIDMATE_DIR, 'args.txt').toFile()
        def contents = argsFile.text;

        println contents

        def matcher = timeLimitPattern.matcher(contents)
        if (matcher.matches()) {
            println 'matches'
            contents = contents.replace(matcher.group(1), Integer.toString(Config.minutes * 60));
        } else {
            println 'not matches'
            contents += " -timeLimit ${Integer.toString(Config.minutes * 60)}"
        }

        println contents

        argsFile.write(contents);

        println("Finished modifying droidmate args file")

        println("Cleaning all files in apk/inlined directory")


        apksDir = Paths.get(Config.DROIDMATE_DIR, 'apks', 'inlined').toFile()

    }

    @Override
    void beforeApk(APK apk) {
        super.beforeApk(apk)

        println("Cleaning all files in apk/inlined directory")

        apksDir.listFiles()
                .findAll { it.name.endsWith('apk') }.each { it.delete() }

        println("Copying ${apk.file.name} to droidmate apks directory")
        FileUtils.copyFileToDirectory(apk.file, apksDir);
    }
}
