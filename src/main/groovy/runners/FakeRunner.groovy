package runners;

import configuration.Command;
import configuration.Config;
import configuration.Tool;
import logger.LogAnalyzer;
import model.APK;
import model.Execution

import java.nio.file.Paths;

/**
 * Created by nmravasi on 5/20/17.
 */
public class FakeRunner extends AbstractRunner {
    protected FakeRunner(Object apk, Object loggerDaemon) {
        super(apk, loggerDaemon);
    }

    def static apps = ["eBay", "Job_Search", "PicsArt", "File_Manager"]

    @Override
    public void testApk(APK apk) {

    }

    FakeRunner() {}

    public static void main(String[] args) {
        println(args)

        if (args) {

            def tool = args[0]

            def name = args[1]

            def date = args[2]
            parseExecutionData(tool, name, date)
        } else {
            new File(Config.RES_PATH).eachDir { file ->
                if (apps.any { file.name.contains(it) }) {
                    new File(file, 'summary.txt').delete()
                    new File(file, 'summaryMethods.txt').delete()
                    def split = file.getName().split('-')

                    if (split && split.size() >= 3) {
                        parseExecutionData(split[0], split[1], split.drop(2).join('-'))
                    }
                }
            }
        }
    }

    private static void parseExecutionData(String tool, String name, String date) {
        println("${tool}  ${name}  ${date}")
        def runner = new FakeRunner()
        Config.toolToUse = Tool.valueOf(tool)
        runner.execution = new Execution(name, date)
        LogAnalyzer analyzer = new LogAnalyzer(runner.execution);
        runner.generateSummary(analyzer)
    }

    def generateSummary(LogAnalyzer analyzer) {
        def res = analyzer.processFiles();
        println(res)
        writeOutput(res, analyzer.methodList)

    }
}
