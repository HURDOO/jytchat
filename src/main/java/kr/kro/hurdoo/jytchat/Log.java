package kr.kro.hurdoo.jytchat;


import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    public static void log(Level level, String msg)
    {
        Logger.getGlobal().log(level,msg);
    }
}
