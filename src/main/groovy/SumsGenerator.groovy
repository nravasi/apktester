import java.util.regex.Pattern

/**
 * Created by nmravasi on 5/23/17.
 */
class SumsGenerator {

    def static pattern = Pattern.compile("summaryMethods-(.*)_(\\d+).txt")

    def static intervals = [1, 5, 10, 15, 30, 60, 120]

    def static noMethodsIndicator = "No methods were found"

    public static void main(String[] args) {
        def sendDir = new File('./results/summaries/')

        def output = ["Tool,App,Index,Minutes,Permissions"]

        def sums = ["MONKEY"   : [],
                    "SAPIENZ"  : [],
                    "DROIDMATE": []]
        sums.each { k, v -> 10.times { v.add(new HashSet<String>()) } }

        sendDir.eachFile { file ->
            def matcher = pattern.matcher(file.getName());
            if (matcher.matches()) {


                def fname = matcher.group(1)
                def idx = Integer.parseInt(matcher.group(2))
                def tool = fname.split(' ')[0]
                def app = fname.split(' ')[1]

                def currentMethodSet = sums[tool][idx - 1]


                def methods = file.readLines()



                methods.each { method ->
                    if (!method.contains(noMethodsIndicator) && idx <= 10) {
                        currentMethodSet.add(method)
                    }
                }

            }
        }

        new File('./sums.csv').withWriter { out ->
            out.println "Tool,Index,Permissions"
            sums.each { k, v ->
                v.eachWithIndex { val, idx ->
                    out.println([k, idx + 1, val.size()].join(','))
                }
            }
        }

        sums.each { k, v ->
            new File("./permisos_${k}.csv").withWriter { out ->
                v.max { it.size() }.each {
                    out.println(it)
                }
            }
        }

    }
}
