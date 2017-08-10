import java.util.regex.Pattern

/**
 * Created by nmravasi on 5/23/17.
 */
class CSVGenerator {

    def static pattern = Pattern.compile("summary-(.*)_(\\d+).txt")

    def static intervals = [1, 5, 10, 15, 30, 60, 120]

    public static void main(String[] args) {
        def sendDir = new File('./sent/')

        def output = ["Tool,App,Index,Minutes,Permissions"]


        def sums = ["MONKEY": [0] * 10, "SAPIENZ": [0] * 10, "DROIDMATE": [0] * 10]

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
                        value = "0"
                    }

                    def idxint = Integer.parseInt(idx)
                    if (idxint <= 10 && it == 120) {
                        sums[tool][idxint - 1] += Integer.parseInt(value)
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

        new File('./sums.csv').withWriter { out ->
            out.println "Tool,Index,Permissions"
            sums.each { k, v ->
                v.eachWithIndex { val, idx ->
                    out.println([k, idx + 1, val].join(','))
                }
            }
        }

    }
}
