package configuration

/**
 * Created by nmravasi on 10/8/16.
 */
class Command {

    public static Process run(String command){
        return command.execute();
    }

    public static ArrayList runAndRead(String command) {
        def process = command.execute();

        def waiting = process.waitFor();

        if(waiting != 0){
            throw new RuntimeException("There was an error running this command: " + command);
        }

        return process.text.readLines();
    }

    public static ArrayList runAndKillAfterTimeout(String command) {
        def process = command.execute();

        process.waitForOrKill(Config.TIMEOUT_BEFORE_KILL);

        return process.text.readLines();
    }

    public static void run(String... commands){
        commands.each {it.execute()};
    }
}
