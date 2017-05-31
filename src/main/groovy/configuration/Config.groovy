package configuration

import java.lang.reflect.Modifier

/**
 * Created by nmravasi on 10/8/16.
 */
public class Config {

    public static String DROIDMATE_DIR = '/Users/nmravasi/thesis/d2/droidmate/dev/droidmate/';
    public static String SAPIENZ_DIR = '/Users/nmravasi/thesis/sapienz/';
    public static String MONKEY_RUNNER_DIR = '';
    public static String MONITOR_APK_DIR_IN_DEVICE = '';
//    public static String SD_PATH =  '/storage/emulated/0/';
    public static String SD_PATH = '/storage/sdcard/';
    public static String SD_PATH_REL = SD_PATH;
    public static String APKS_PATH = 'apks';
    public static String SDK = "C:\\Users\\Ignacio\\AppData\\Local\\Android\\Sdk\\tools";
    public static String ADV_NAME = "Nexus_7_2012_Edited_API_19";
    public static String ADB_PATH = "/Users/nmravasi/Library/Android/sdk/platform-tools/adb";
    public static String AAPT_PATH = "/Users/nmravasi/Library/Android/sdk/build-tools/19.1.0/aapt";
    public static String ANDROID_TOOLS_PATH = "/Users/nmravasi/Library/Android/sdk/tools/";
    public static String EMULATOR_NAME = "thesis";
    public static String MONKEY_OPTIONS = "--ignore-crashes --ignore-timeouts";
    public static boolean MONKEY_INSTALL = true
    public static int monkeyTimes = 20000;

    public static int minutes = 1;

    public static int times = 2;

    public static boolean shouldInline = false;
    public static boolean initEmulator = true;
    public static Tool toolToUse = Tool.DROIDMATE;

    //Way until kill monkey 30 min would be 18000000 (in milliseconds )
    public static int TIMEOUT_BEFORE_KILL = 10000;

    static updateConfig(Properties props) {
        Config.class.getDeclaredFields().each { field ->
            if (Modifier.isStatic(field.getModifiers())) {
                def property = props.getProperty(field.getName())

                if (property) {
                    if (field.getType() == int.class) {
                        field.setInt(Config.class, Integer.parseInt(property))
                    } else if (field.getType() == boolean.class){
                        field.setBoolean(Config.class, Boolean.parseBoolean(property))
                    } else if (field.getType() == Tool.class){
                        field.set(Config.class, Tool.valueOf(property))
                    } else {
                        field.set(Config.class, property)
                    }
                }
            }
        }
    }

}
