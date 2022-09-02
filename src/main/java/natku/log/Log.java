package natku.log;

public class Log {
    private static boolean debug = false;
    public static void println(int message){
        if (debug) {
            System.out.println(message);
        }
    }
    public static void println(Object message){
        if (debug) {
            System.out.println(message);
        }
    }
}
