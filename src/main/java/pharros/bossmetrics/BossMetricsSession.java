package pharros.bossmetrics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
	@Setter
	private int sessionTimeRemaining = -1;

	@Getter
	private BossMetricsTimer killTimer;

	@Getter
	private BossMetricsTimer timeoutTimer;

	BossMetricsSession(BossMetricsPlugin plugin, BossMetricsMonster currentMonster)
	{
		previousKillTimes = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0));
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
		previousKillTimes.add(0, (int)seconds);
		previousKillTimes.remove(5);
	}

	void setPb(int newPb)
	{
		personalBest = newPb;
	}

	void startTimeoutTimer(int period)
	{
		timeoutTimer = new BossMetricsTimer(period);
		if (killTimer != null && currentMonster.isMonsterInstance())
		{
			killTimer = null;
		}
	}

	void startKillTimer()
	{
		killTimer = new BossMetricsTimer(-1);
	}
}
