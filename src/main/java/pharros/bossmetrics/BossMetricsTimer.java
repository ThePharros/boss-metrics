package pharros.bossmetrics;

import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BossMetricsTimer
{
	private final Instant startTime;
	private final Instant endTime;
	private final Duration duration;
	private final long period;

	BossMetricsTimer(long period)
	{
		this.period = period;
		startTime = Instant.now();
		if (period == -1)
		{
			duration = Duration.ofSeconds(0);
			endTime = Instant.now();
		}
		else
		{
			duration = Duration.ofSeconds(period);
			endTime = startTime.plus(duration);
		}
	}

	protected Duration getTimeDuration()
	{
		final Duration timeLeft;
		if (period == -1)
		{
			timeLeft = Duration.between(startTime, Instant.now());
		}
		else
		{
			timeLeft = Duration.between(Instant.now(), endTime);
		}
		return timeLeft;
	}

	protected String getText()
	{
		int secs = (int)(getTimeDuration().toMillis() / 1000L);

		int seconds = secs % 60;
		int minutes = (secs % 3600) / 60;
		int hours = secs / 3600;

		if (hours > 0)
		{
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		}
		return String.format("%d:%02d", minutes, seconds);
	}
}
