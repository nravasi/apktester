package statistics

class Run {
    String tool
    String app
    int permissions
    int idx
    int minutes

    Run(a){
        tool = a[0]
        app = a[1]
        idx = Integer.parseInt(a[2])
        minutes = Integer.parseInt(a[3])
        permissions = Integer.parseInt(a[4])
    }
}