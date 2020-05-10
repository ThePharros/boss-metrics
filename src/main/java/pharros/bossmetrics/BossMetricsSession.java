package pharros.bossmetrics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
class BossMetricsSession
{
    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private int timeoutTimeRemaining;

    @Getter(AccessLevel.PACKAGE)
    private int sessionKc = 0;

    @Getter(AccessLevel.PACKAGE)
    BossMetricsMonster currentMonster;

    @Getter(AccessLevel.PACKAGE)
    private ArrayList<Integer> previousKillTimes;

    @Getter
    private int personalBest;

    @Getter
    private Instant sessionTimeoutStart;

    @Getter
    private BossMetricsTimer killTimer;

    BossMetricsSession(BossMetricsPlugin plugin, BossMetricsMonster currentMonster) {
        previousKillTimes = new ArrayList<>(Arrays.asList(0,0,0,0,0,0));
        killTimer = new BossMetricsTimer();
        this.sessionTimeoutStart = Instant.EPOCH;
        this.currentMonster = currentMonster;
        this.personalBest = plugin.getPb(currentMonster.getName());
        log.info("SESSION STARTED FOR " + currentMonster.getName());
    }

    void incrementKc()
    {
        sessionKc++;
    }

    void updateSessionTimeoutStart()
    {
        sessionTimeoutStart = Instant.now();
    }

    void recordPreviousTime(long seconds)
    {
        previousKillTimes.add(0, (int)seconds);
        previousKillTimes.remove(5);
    }
}
