package pharros.bossmetrics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
	private ArrayList<Integer> previousKillTimes;

	@Getter
	private int personalBest;

	@Getter
	private BossMetricsTimer killTimer;

	@Getter
	private BossMetricsTimer timeoutTimer;

	private BossMetricsPlugin plugin;

	BossMetricsSession(BossMetricsPlugin plugin, BossMetricsMonster currentMonster)
	{
		previousKillTimes = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0));
		this.plugin = plugin;
		this.currentMonster = currentMonster;
		this.personalBest = plugin.getPb(currentMonster.getName());
		log.info("SESSION STARTED FOR " + currentMonster.getName());
	}

	void incrementKc()
	{
		sessionKc++;
	}

	void recordPreviousTime(long seconds)
	{
		if ((int)seconds < personalBest)
		{
			personalBest = plugin.getPb(currentMonster.getName());
		}
		previousKillTimes.add(0, (int)seconds);
		previousKillTimes.remove(5);
		stopKillTimer();
	}

	void startTimeoutTimer(int period)
	{
		timeoutTimer = new BossMetricsTimer(period, 0);
		if (killTimer != null && currentMonster.isMonsterInstance())
		{
			stopKillTimer();
		}
	}

	void startKillTimer()
	{
		killTimer = new BossMetricsTimer(-1, 1);
	}

	void stopTimeoutTimer()
	{
		timeoutTimer = null;
	}

	void stopKillTimer()
	{
		log.info("KILL TIMER STOPPED WITH TIME OF: " + killTimer.getText());
		killTimer = null;
	}
}
