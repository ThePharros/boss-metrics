package pharros.bossmetrics;

import java.time.Duration;
import java.time.Instant;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BossMetricsTimer
{
    @Getter
    private long currSeconds = 0;

    private boolean isActive = false;
    private long lastTickMillis;
    private BossMetricsPlugin plugin;
    private BossMetricsSession session;
    private Instant startTime;

    BossMetricsTimer(BossMetricsPlugin plugin) {
        this.plugin = plugin;
        this.session = plugin.getSession();
    }

    void update()
    {
        lastTickMillis = System.currentTimeMillis();

        if (isActive)
        {
            Duration timeSince = Duration.between(startTime, Instant.now());
            currSeconds = timeSince.getSeconds();
            //log.info("THIS TIMER'S SECONDS: " + currSeconds);
        }
    }


    void start() {
        log.info("Timer started!");
        isActive = true;
        currSeconds = 0;
        startTime = Instant.now();
    }

    void end(boolean recordTime) {
        isActive = false;
        if (recordTime && session != null)
        {
            session.recordPreviousTime(currSeconds);
        }
        currSeconds = 0;
        onTimerEnd();
        log.info("Timer ended!");
    }

    private void onTimerEnd() {

    }
}
