package configuration
/**
 * Created by nmravasi on 10/8/16.
 */
public class Config {

    public static String DROIDMATE_DIR =  '/Users/nmravasi/dev/thesis/droidmate/dev/droidmate/';
    public static String THIS_PATH =  '/Users/nmravasi/dev/thesis/tesis-tools/APKTester/';
    public static String SAPIENZ_DIR =  '/Users/nmravasi/dev/thesis/sapienz/';
    public static String MONKEY_RUNNER_DIR =  '';
    public static String MONITOR_APK_DIR_IN_DEVICE =  '';
//    public static String SD_PATH =  '/storage/emulated/0/';
    public static String SD_PATH =  '/storage/sdcard/';
    public static String SD_PATH_REL =  SD_PATH;
    public static String APKS_PATH =  'apks';
    public static String SDK = "C:\\Users\\Ignacio\\AppData\\Local\\Android\\Sdk\\tools";
    public static String ADV_NAME = "Nexus_7_2012_Edited_API_19";
    public static String ADB_PATH = "/Users/nmravasi/Library/Android/sdk/platform-tools/adb";
    public static String AAPT_PATH = "/Users/nmravasi/Library/Android/sdk/build-tools/19.1.0/aapt";
    public static String ANDROID_TOOLS_PATH = "/Users/nmravasi/Library/Android/sdk/tools/";
    public static String EMULATOR_NAME = "thesis";

    public static int minutes =  2;
    public static int seconds =  120;

    public static int times = 1;

    public static boolean shouldInline = false;
    public static boolean initEmulator = true;
    public static Tool TOOL_TO_USE = Tool.MONKEY;

    //Way until kill monkey 30 min would be 18000000 (in milliseconds )
    public static Long TIMEOUT_BEFORE_KILL = 10000;

}
