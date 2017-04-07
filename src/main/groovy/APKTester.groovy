
import configuration.Command
import configuration.Config
import logger.LogDaemon
import model.APK
import runners.AbstractRunner
import org.apache.commons.io.FileUtils

import java.nio.file.Paths

/**
 * Created by nmravasi on 10/8/16.
 */
class APKTester {

    public static void main(String[] args) {

        /*def lines = Command.run('adb devices').text.readLines()
        if (lines.size() != 3) {
        }*/

        //Mi idea es levantar la adv si no se encuentra, tambien hay q matarla y levantarla siempre, por que a veces se cuelgan las aplicaciones.
        //No me esta funcionando .. Si ne podes ayudar genial . el emulador esta en la carpeta sdk . hay que correrlo desde ahi , diciendo emulator -avd <nombre-emulador>
        // Command.run("cd C:/Users/Ignacio/AppData/Local/Android/Sdk" );
        // Command.run("emulator -avd " + Config.ADV_NAME);

        //deberiamos remover esto una vez q funcione lo otro
        //  throw new RuntimeException("Ensure there's exactly one device running")

//        if (!ADB.IsDeviceUp()) {
//            ADB.RunEmulator();
//        }
        Command.run("${Config.ADB_PATH} shell rm -rf ${Config.SD_PATH} logs")
        File apksPath = new File(Config.APKS_PATH);

        if (Config.shouldInline) {
            File inlinePath = Paths.get(Config.DROIDMATE_DIR, 'apks', 'inlined').toFile()
            inlinePath.deleteDir();
            FileUtils.copyDirectory(apksPath, inlinePath)
            println("Copied to " + inlinePath.getAbsolutePath())

            def argsFile = Paths.get(Config.DROIDMATE_DIR, 'args.txt').toFile()
            def originalArgs = argsFile.text

            if (!originalArgs.contains('-inline')) {
                println('Modifying args.txt to add inline command')
                def newArgs = originalArgs + ' -inline';
                argsFile.write(newArgs);
            } else {
                originalArgs = originalArgs.replace('-inline', '')
            }

            def droidmateCmd = Config.DROIDMATE_DIR + 'gradlew -p ' + Config.DROIDMATE_DIR + ' :p:com:run'
            println("Inlining with command " + droidmateCmd)
            def inliner = Command.run(droidmateCmd);

            def res = inliner.waitFor()

            if (res != 0) {
                println("Inlining failed")
                println(inliner.text)
                throw new RuntimeException()
            }


            println('Inlined successfuly')
            argsFile.write(originalArgs);

            println('Copying apk back to path')
            apksPath.deleteDir()
            FileUtils.copyDirectory(inlinePath, apksPath)
            FileUtils.deleteDirectory(new File(apksPath, 'originals'))
        }

        def apks = apksPath.listFiles().findAll {
            it.name.endsWith('inlined.apk')
        }.collect { new APK(it) }

        def loggerDaemon = new LogDaemon();
        def runner = AbstractRunner.getRunner(apks, loggerDaemon);

        runner.start();

        System.exit(0)
    }
}
