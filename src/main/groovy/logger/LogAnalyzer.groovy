package logger

import model.Execution

import java.util.regex.Pattern

/**
 * Created by nmravasi on 10/19/16.
 */
class LogAnalyzer {

    def pattern = Pattern.compile(".*objCls: ([^ ]*) mthd: ([^ ]*) .*")
    def res = new HashMap();
    def methodList = new HashSet();
    Execution execution;

    LogAnalyzer(execution){
        this.execution = execution;
    }

    def getMethodName(String s) {
        def match = pattern.matcher(s);
        if (match.matches()) {
            return "${match.group(1)}.${match.group(2)}"
        }
    }

    def processFile(File file) {
        def methods = new HashSet()

        file.eachLine {
            def name = getMethodName(it)
            name && methods << name;
        }
        /*output = new File("../output/" + fname.split(Pattern.quote('.'))[0] + '-uniq.txt')

        methods.each {
            output << it + '\n'
        }*/

        res.put(file.name.split(Pattern.quote('.'))[0].split(Pattern.quote("_")).last().toInteger() * 10, methods.size());

//        FileUtils.copyFile(file, new File("./tmpbk/" + file.name));
//        file.delete()
        return methods
    }


    def processFiles() {
        def temp = new HashSet()
        def list = new File("res/${execution.folderName()}").listFiles()
        list.each {
            if (!it.name.startsWith('.'))
                temp = processFile(it)
            if (methodList.size() < temp.size()) {
                methodList = temp
            }
        }

        return res;
    }
}
