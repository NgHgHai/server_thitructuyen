package edu.vn.hcmuaf.layer2;

import org.apache.log4j.Logger;

public class LogUtils {
    /**
     * @param logger
     * @param begin     time
     * @param threshold the value to know when to output the log
     * @param msg
     */
    public static void warnIfSlow(Logger logger, long begin, long threshold, String msg) {
        long cost = System.currentTimeMillis() - begin;
        if (cost > threshold)
            logger.warn(msg + " t=" + cost);
    }
}
