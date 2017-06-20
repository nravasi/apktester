package logger

/**
 * Created by nmravasi on 6/8/17.
 */
class ApiFilters {

    static boolean initialized = false;

    static List<String> apis

    static boolean isValidApi(String method){
        if(!initialized) initialize()

        return apis.contains(method)
    }

    static initialize() {
        apis = new File("./utils/AppGuard_apis_list.txt").readLines()
        initialized = true
    }
}
