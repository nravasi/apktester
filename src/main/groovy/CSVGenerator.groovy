import configuration.Config

import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.regex.Pattern

/**
 * Created by nmravasi on 5/23/17.
 */
class CSVGenerator {

    def static pattern = Pattern.compile("summary-(.*)_(\\d+).txt")

    def static intervals = [1, 5, 10, 15, 30, 60, 120]

    public static void main(String[] args) {
        def sendDir = new File('./send/')

        def output = []

        sendDir.eachFile { file ->
            def matcher = pattern.matcher(file.getName());
            if (matcher.matches()) {
                def summary = file.readLines().tail()
                def fname = matcher.group(1)
                def idx = matcher.group(2)
                def tool = fname.split(' ')[0]
                def app = fname.split(' ')[1]
                intervals.each {
                    def seconds = it * 60
                    def value
                    if (!summary.empty) {
                        def line = summary.find { it.startsWith(seconds.toString()) } ?: summary.last()
                        value = line.split('\t')[1]
                    } else {
                        value = 0
                    }

                    output << [tool, app, idx, it, value].join(',')
                }
            }
        }

        new File('./results.csv').withWriter { out ->
            output.each {
                out.println it
            }
        }


    }
}
