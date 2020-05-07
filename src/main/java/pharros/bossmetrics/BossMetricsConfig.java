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
		name = "Previous kill amount",
		description = "The number of previous kills to track (0 to disable)"
	)
	default int getPreviousKillAmount()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "showpreviouskillaverage",
		name = "Show previous kill average",
		description = "Will show the average time of the last N kills."
	)
	default boolean showPreviousKillAverage()
	{
		return true;
	}
}
