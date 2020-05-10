package pharros.bossmetrics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
class BossMetricsSession
{
    @Getter(AccessLevel.PACKAGE)
    private int sessionKc = 0;

    @Getter(AccessLevel.PACKAGE)
    BossMetricsMonster currentMonster;

    @Getter(AccessLevel.PACKAGE)
    private int currentTime = 0;

    @Getter(AccessLevel.PACKAGE)
    private int timeSinceTimeout;

    @Getter(AccessLevel.PACKAGE)
    private ArrayList<Integer> previousKillTimes;

    private Client client;
    private Instant sessionTimeoutStart;
    private BossMetricsPlugin plugin;
    private BossMetricsTimer killTimer;
    private ConfigManager configManager;
    private int lastPb;

    BossMetricsSession(BossMetricsPlugin plugin, Client client, ConfigManager configManager, BossMetricsMonster currentMonster) {
        this.plugin = plugin;
        this.client = client;
        this.configManager = configManager;
        previousKillTimes = new ArrayList<>(Arrays.asList(0,0,0,0,0,0));
        this.killTimer = new BossMetricsTimer(plugin);
        this.sessionTimeoutStart = Instant.EPOCH;
        this.currentMonster = currentMonster;
        log.info("SESSION STARTED FOR " + currentMonster.getName());
    }

    void incrementKc()
    {
        sessionKc++;
    }

    void resetTimeout()
    {

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

    void expire()
    {

    }
}
