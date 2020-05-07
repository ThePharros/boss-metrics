package pharros.bossmetrics;

import com.google.inject.Provides;
import java.awt.Color;
import java.time.Duration;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.GameState;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Boss Metrics"
)
public class BossMetricsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BossMetricsConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BossMetricsOverlay overlay;

	@Inject
	private BossMetricsPreviousKillsOverlay previousKillsOverlay;

	@Inject
	private BossMetricsPreviousKillsAverageOverlay previousKillsAverageOverlay;

	@Getter
	private BossMetricsMonster currentMonster;

	@Getter
	private int personalBest = -1;

	@Getter
	private int currentTime = 0;

	@Getter
	private Color colCurrentTime = Color.GREEN;

	@Getter
	int[] previousKillTimes;

	@Getter
	private boolean isInBossRegion;

	@Override
	protected void startUp()
	{
		log.info("Boss Metrics plugin started!");
		previousKillTimes = new int[]{181, 188, 199, 185, 211};
		currentMonster = BossMetricsMonster.GROTESQUE_GUARDIANS;
		overlayManager.add(overlay);
		if (config.getPreviousKillAmount() > 0)
		{
			overlayManager.add(previousKillsOverlay);
		}
		if (config.showPreviousKillAverage())
		{
			overlayManager.add(previousKillsAverageOverlay);
		}
	}

	@Override
	protected void shutDown()
	{
		log.info("Boss Metrics plugin stopped!");
	}

	private int getPb(String boss)
	{
		Integer personalBest = configManager.getConfiguration("personalbest." + client.getUsername().toLowerCase(),
			boss.toLowerCase(), int.class);
		return personalBest == null ? 0 : personalBest;
	}

	 String getDisplayTime(int secs)
	{
		int seconds = secs % 60;
		int minutes = secs % 3600 / 60;
		int hours = secs % 3600;
		if (hours == 0) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		}
		return String.format("%d:%02d", minutes, seconds);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			personalBest = getPb("grotesque guardians");
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{

	}


	@Provides
	BossMetricsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossMetricsConfig.class);
	}
}
