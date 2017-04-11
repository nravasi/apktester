package configuration

/**
 * Created by Ignacio on 20-Oct-16.
 */
class ADB {

    public static void InstallAPK(String packageName, String file){
        println "Installing " + packageName + " ....";

        def installApk = Command.runAndRead("${Config.ADB_PATH} install -r " + file);

        if (!installApk.contains("Success")) {
            throw new RuntimeException("Something goes wrong installing ${packageName}");
        }

        println "Installed OK";
    }

    public static Boolean IsAPKInstalled(String packageName){

        def data = Command.runAndRead("${Config.ADB_PATH} shell pm list packages | grep " + packageName);

        return data.contains("package:" + packageName);
    }

    public static void RemoveAPK(String packageName){

        println "Removing package " + packageName;

        def removeApk = Command.run("${Config.ADB_PATH} shell pm uninstall -k " + packageName);

        println "Remove OK"
    }

    public static void KillAPK(String nameApk){
        def pid = GetPid(nameApk)

        println "Kill ${nameApk} - ${pid}"
        Command.run "${Config.ADB_PATH} shell kill ${pid}"
    }

    public static String GetPid(String nameApk){
       def processInfo = Command.run("${Config.ADB_PATH} shell ps | grep ${nameApk}").text.readLines();

        return processInfo[0].split(" ")[6]
    }

    public static Boolean IsDeviceUp(){

        def emulatorRunning = Command.run("${Config.ADB_PATH} shell getprop sys.boot_completed").text.readLines();

        return emulatorRunning.contains("1");
    }

   /* public static void RunEmulator(){

        println "Running Emulator - ${ADV_NAME}"

        Command.run("${SDK}/emulator -avd ${ADV_NAME}");

        def isRunning = false;

        while(!isRunning){
            isRunning = IsDeviceUp()

            if(isRunning){
                //wait 10 seconds just in case..
                Thread.sleep(10000);
                println "Emulator ${ADV_NAME} is UP"
            }

            //wait 2 second to avoid followed  commands
            Thread.sleep(2000);
        }
    }*/
}
