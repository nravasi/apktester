package logger

import configuration.Config
import model.Execution

import java.util.regex.Pattern

/**
 * Created by nmravasi on 10/19/16.
 */
class LogAnalyzer {

    public static final String uriClazz = 'android.net.Uri'
    def pattern = Pattern.compile(".*objCls: ([^ ]*) mthd: ([^ ]*) .* params: (.*) stacktrace.*")
    def res = new HashMap();
    def methodList = new HashSet();
    int lastLine = 0
    def mediaContentStr = "content://media/external/images/media/"
    int accum = 0
    Execution execution;

    LogAnalyzer(execution) {
        this.execution = execution;
    }

    def getMethodName(String s) {
        def match = pattern.matcher(s);
        if (match.matches()) {
            def method = match.group(2)
            if (blacklisted(method)) return null

            def params = match.group(3);
            def clazz = match.group(1)
            def uri = '', parsedParams = ''
            if (params) {
                def res = parseParams(params.trim());
                parsedParams = res.params
                uri = res.uri
            }

            def formatted = "${clazz}.${method}(${parsedParams})".toString()

            if (!ApiFilters.isValidApi(formatted)) return null

            return uri ? formatted + ' ' + uri : formatted
        }
    }

    def parseParams(str) {
        def splt = str.split(' ')

        def res = []

        String uri = ''

        boolean isUri

        for (int i = 0; i < splt.size(); i += 1) {
            if (i % 2 == 0) {
                isUri = (splt[i] == uriClazz)
                res.add(splt[i]);
            } else {
                if (isUri) {
                    uri = splt[i]
                    if(uri?.startsWith(mediaContentStr)) uri = mediaContentStr
                }
            }
        }

        return [params: res.join(', '), uri: uri]
    }

    boolean blacklisted(String methodName) {
        return [
                "startActiv",
                "startIntentSender",
                "startService",
                "stopService",
                "bindService",
                "unbindService",
                "sendBroadcast",
                "sendOrderedBroadcast"]
                .any { methodName.startsWith(it) }
    }

    def processFile(File file) {
        def methods = new HashSet(methodList)

        file.eachLine { it, idx ->
            if (idx < lastLine) return
            lastLine++
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

        def file = new File("${Config.RES_PATH}${execution.folderName()}")
        println file.name
        def list = file.listFiles().sort()
        list.each {
            if (it.name.startsWith('log_')) {
                temp = processFile(it)
                methodList.addAll(temp)
            }
        }

        return res;
    }

}