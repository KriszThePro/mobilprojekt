package hu.szollosikrisztian.mobilprojekt.utils;

import android.util.Log;

public class LogUtil {
    public static void d(Object caller, String message) {
        Log.d(caller.getClass().getSimpleName(), message);
    }

    public static void e(Object caller, String message, Throwable t) {
        Log.e(caller.getClass().getSimpleName(), message, t);
    }
}
