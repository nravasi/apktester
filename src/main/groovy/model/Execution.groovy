package model

import configuration.Config

import java.text.SimpleDateFormat

/**
 * Created by nmravasi on 4/7/17.
 */
class Execution {

    static formatter = new SimpleDateFormat("dd-MM-yyyy--hh.mm.ss")

    APK apk;
    String executionDate

    Execution(apk){
        this.apk = apk;
        executionDate = formatter.format(new Date())
    }

    String folderName(){
        return "${Config.toolToUse}-${apk.appName}-${executionDate}"
    }

    Execution(String name, String exDate){
        this.apk = new APK(name);
        this.executionDate =exDate;
    }

}
