import configuration.Config

import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.regex.Pattern

/**
 * Created by nmravasi on 5/23/17.
 */
class SummaryMerger {

    def static pattern = Pattern.compile("summary-.*_(\\d)+.txt")

    public static void main(String[] args) {
        def nameCount = [:]

        def sourceDir = new File('./send/')
        def targetDir =  new File('./sent/')

        sourceDir.eachFile {
            def matcher = pattern.matcher(it.getName());
            if(matcher.matches()){
                def idx = Integer.parseInt(matcher.group(1))

                def fileName = it.getName()

                while(new File(targetDir, fileName).exists()){
                    fileName = fileName.replace("_"+idx, "_"+(idx+1))
                    idx++
                }

                println "Moving ${it.getName()} to ${fileName}"

                Files.copy(it.toPath(), new File(targetDir, fileName).toPath())
                Files.copy(new File(it.getPath().replace("summary", "summaryMethods")).toPath(), new File(targetDir, fileName.replace("summary", "summaryMethods")).toPath(), StandardCopyOption.REPLACE_EXISTING)


            }
        }
    }
}
