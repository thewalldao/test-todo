package Utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class StopWatch {
    private static Logger log = LogManager.getLogger(StopWatch.class);

    private long startTime = -1;

    public void startClock() {
        try {
            if (this.startTime == -1) {
                this.startTime = new Date().getTime();
            } else {
                throw new Error("Clock has already started");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Error(e.getMessage());
        }
    }

    public long getElapsedTime() {
        try {
            long endTime = new Date().getTime();
            return endTime - this.startTime;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Error(e.getMessage());
        }
    }

    public long getElapsedTimeInSecond() {
        try {
            long endTime = new Date().getTime();
            return (endTime - this.startTime) / 1000;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Error(e.getMessage());
        }
    }

    public long getTimeLeftInSecond(long maxTimeoutInSecond) {
        try {
            return maxTimeoutInSecond - this.getElapsedTimeInSecond();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Error(e.getMessage());
        }
    }
}
