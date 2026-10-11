package serap.sillyge;

import org.tinylog.Logger;

public class SillyGameEngine {
    public static final String ENGINE_NAME = "silly game engine";
    public static final String ENGINE_NAME_SHORT = "sillyge";

    public static boolean debugOutput;
    public static boolean infoOutput;
    public static boolean errorOutput;
    public static boolean warnOutput;

    public static void info(Object o) {
        if(infoOutput) {
            Logger.info(o.toString());
        }
    }

    public static void warn(Object o) {
        if(warnOutput) {
            Logger.warn(o);
        }
    }

    public static void error(Object o) {
        if(errorOutput) {
            Logger.error(o);
        }
    }

    public static void debug(Object o) {
        if(debugOutput) {
            Logger.debug(o);
        }
    }
}
