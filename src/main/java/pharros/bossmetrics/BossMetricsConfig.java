package pharros.bossmetrics;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("bossmetrics")
public interface BossMetricsConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "showsessionkillcount",
		name = "Show Session Kill Count",
		description = "Shows the current session's kill count"
	)
	default boolean showSessionKillCount()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "showpreviouskillaverage",
		name = "Show Previous Kill Average",
		description = "Shows the average time of the last N kills"
	)
	default boolean showPreviousKillAverage()
	{
		return true;
	}

	@Range(max=5)
	@ConfigItem(
		position = 3,
		keyName = "getpreviouskillamount",
		name = "Previous Kill Amount",
		description = "The number of previous kills to track (0 to disable)"
	)
	default int getPreviousKillAmount()
	{
		return 5;
	}

	@Units(Units.SECONDS)
	@Range(max=300)
	@ConfigItem(
		position = 4,
		keyName = "gettimeroffset",
		name = "Timer Offset",
		description = "Set how long to wait before timers and the overlay expire once the player leaves the boss region"
	)
	default int getTimerOffset()
	{
		return 30;
	}


}
