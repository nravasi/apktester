package model

import configuration.Command
import configuration.Config

import java.util.regex.Pattern

/**
 * Created by nmravasi on 10/9/16.
 */
class APK {

    static Pattern packageNamePattern = Pattern.compile('^package: name=\'(.*)\' versionCode.*$', Pattern.MULTILINE)
    static Pattern appnamePattern = Pattern.compile('^application-label:\'(.*)\'$', Pattern.MULTILINE)

    File file;
    String appName;
    String packageName;

    APK(File file) {
        this.file = file;
        setNames(file)
    }

    APK(String name){
        appName = name
    }


    private String setNames(File file) {

        def proc = Command.run("${Config.AAPT_PATH} d badging " + file.toString().replace(" ", "\\ "))

        def text = proc.text
        def matcher = appnamePattern.matcher(text)

        if (matcher.find()) {
            appName = matcher.group(1).replace(" ", "_");
        }

        matcher = packageNamePattern.matcher(text)

        if (matcher.find()) {
            packageName = matcher.group(1);
        }
    }
}
