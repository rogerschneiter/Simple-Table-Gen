package globals;

public class Logger {
    private static boolean logStatus = true;

    public static void log(String tag, String msg) {
        if (logStatus) {
            System.out.println(tag + ": " + msg);
        }
    }
}
