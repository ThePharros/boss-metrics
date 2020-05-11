package pharros.bossmetrics;

import java.time.Duration;
import java.time.Instant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BossMetricsTimer
{
    @Getter
    private long currSeconds;

    private boolean isActive;
    private Instant startTime;
    private int delay;

    BossMetricsTimer() {
        currSeconds = 0;
        delay = 0;
        isActive = false;
    }

    void start(int delay) {
        log.info("Timer started!");
        this.delay = delay;
        startTime = Instant.now();
        isActive = true;
    }

    void update()
    {
        if (isActive)
        {
            Duration timeSince = Duration.between(startTime, Instant.now());
            currSeconds = timeSince.getSeconds() - delay;
            log.info("Timer's current seconds: " + currSeconds);
        }
    }

    void stop()
    {
        if (isActive)
        {
            log.info("STOPPING TIMER WITH CURRENT SECONDS OF: " + currSeconds);
            isActive = false;
        }
    }
}
