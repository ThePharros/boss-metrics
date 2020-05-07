package pharros.bossmetrics;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("bossmetrics")
public interface BossMetricsConfig extends Config
{
	@Range(max=5)
	@ConfigItem(
		keyName = "previouskillamount",
		name = "Previous Kill Amount",
		description = "The number of previous kills to track (0 to disable)."
	)
	default int getPreviousKillAmount()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "showpreviouskillaverage",
		name = "Show Previous Kill Average",
		description = "Shows the average time of the last N kills."
	)
	default boolean showPreviousKillAverage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showsessionkillcount",
		name = "Show Session Kill Count",
		description = "Shows the current session's kill count."
	)
	default boolean showSessionKillCount()
	{
		return true;
	}
}
