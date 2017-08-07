import java.util.regex.Pattern

/**
 * Created by nmravasi on 8/6/17.
 */
class MaxGenerator {
    def static pattern = Pattern.compile("summary-(.*)_(\\d+).txt")

    def static intervals = [1, 5, 10, 15, 30, 60, 120]

    public static void main(String[] args) {
        def sendDir = new File('./sent/')

        def output = []

        def organizedFiles = [:]

        sendDir.eachFile { file ->
            def matcher = pattern.matcher(file.getName());
            if (matcher.matches()/* && file.getName().contains("summary-DROIDMATE AntiVirus")*/) {
                def summary = file.readLines().tail()
                def fname = matcher.group(1)
                def idx = matcher.group(2)
                def tool = fname.split(' ')[0]
                def app = fname.split(' ')[1]

                if (!organizedFiles[fname]) {
                    organizedFiles[fname] = [:]
                }

                organizedFiles[fname][idx] = summary
            }
        }

        organizedFiles.each { name, summaries ->
            def permByMinute = [0] * 121
            def tool = name.split(' ')[0]
            def app = name.split(' ')[1]

            summaries.each { idx, lines ->

                def currentMinute = 1
                def size = lines.size()
                if (size > 0) {
                    def first = Integer.parseInt(lines[0].split('\t')[1])
//                    println("first is ${first}")
                    permByMinute[0] = Math.max(permByMinute[0], first)

                    while (currentMinute <= 120) {
                        def idxLine = currentMinute * 6 - 1;
                        if(idxLine < size){
                            def val =  Integer.parseInt(lines[idxLine].split('\t')[1])
//                            println "for ${currentMinute} val is ${val}"

                            permByMinute[currentMinute] = Math.max(permByMinute[currentMinute], val)
                        }
                        currentMinute++
                    }
                }
            }

            int last = 0

            permByMinute.eachWithIndex {perm, min ->
                if(perm < last) perm = last

                output << [tool, app, min, perm].join(',')

                last = perm
            }
        }

        new File('./maxes.csv').withWriter { out ->
            output.each {
                out.println it
            }
        }


    }
}
