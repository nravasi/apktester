package statistics

import org.rosuda.REngine.Rserve.RConnection

class Vargha {
    /**
     * Rserve (R 3.1 or higher) must be running, with command:
     * R CMD Rserve
     */
    static def createConnection(Closure cback) {
        def c = new RConnection();
        try {
            cback.call(c)
        } finally {
            c.close()
        }
    }

    public static void main(String[] args) {
        def runs = new File("./results.csv").readLines().tail().collect { new Run(it.split(",")) }
        def minutes = 120

        runs*.app.unique().each { app ->
            def monkeyRuns = runs.findAll { it.tool == "MONKEY" && it.minutes == minutes && it.app == app }
            def droidmateRuns = runs.findAll { it.tool == "DROIDMATE" && it.minutes == minutes && it.app == app }
            def sapienzRuns = runs.findAll { it.tool == "SAPIENZ" && it.minutes == minutes && it.app == app }

            createConnection { c ->
                println "${app} Monkey vs Droidmate: " + calculateVarghaDelaney(c, monkeyRuns*.permissions, droidmateRuns*.permissions)
                println "${app} Sapienz vs Droidmate: " + calculateVarghaDelaney(c, sapienzRuns*.permissions, droidmateRuns*.permissions)
                println "${app} Sapienz vs Monkey: " + calculateVarghaDelaney(c, sapienzRuns*.permissions, monkeyRuns*.permissions)
            }
        }
    }


    static calculateVarghaDelaney(RConnection c, List<Integer> a1, List<Integer> a2) {
        c.assign("a", a1 as int[]);
        c.assign("b", a2 as int[]);

        String script = """
          AMeasure <- function(a,b){
        
            # Compute the rank sum (Eqn 13)
            r = rank(c(a,b))
            r1 = sum(r[seq_along(a)])
        
            # Compute the measure (Eqn 14) 
            m = length(a)
            n = length(b)
            A = (r1/m - (m+1)/2)/n
        
            A
          }
        
          AMeasure(a,b)
        
          """.split('\n')*.trim().join('\n').trim()

        def result = c.eval(script).asDouble()
        return result
    }

}
