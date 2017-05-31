import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Created by nmravasi on 5/23/17.
 */
class SummaryExtractor {

    public static void main(String[] args) {
        def nameCount = [:]

        new File('./send/').mkdirs()

        new File('./res').eachDir {
            def fname = it.getName().split('-').take(2).join(' ');

            if (!nameCount.containsKey(fname)) {
                nameCount[fname] = 0
            }

            nameCount[fname]++
            def val = nameCount[fname]

            def summaryFile = new File(it, 'summary.txt')

            if (summaryFile.exists())
                Files.copy(summaryFile.toPath(), new File("./send/summary-${fname}_${val}.txt").toPath(), StandardCopyOption.REPLACE_EXISTING)

            def methodsFile = new File(it, 'summaryMethods.txt')
            if (methodsFile.exists()) {
                Files.copy(methodsFile.toPath(), new File("./send/summaryMethods ${fname}_${val}.txt").toPath(), StandardCopyOption.REPLACE_EXISTING)
            } else {
                Files.copy(new File("./utils/dummyMethods.txt").toPath(), new File("./send/summaryMethods ${fname}_${val}.txt").toPath(), StandardCopyOption.REPLACE_EXISTING)

            }

        }
    }
}
