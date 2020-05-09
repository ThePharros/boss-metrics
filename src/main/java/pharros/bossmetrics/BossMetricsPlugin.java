package pharros.bossmetrics;

import com.google.inject.Provides;
import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Constants;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ChatMessage;
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

	@Getter
	private BossMetricsMonster currentMonster = null;

	@Getter
	private String currentMonsterName = "";

	@Getter
	private int personalBest = -1;

	@Getter
	private int currentTime = 0;

	@Getter
	private int sessionKills = 0;

	@Getter
	private Color colCurrentTime = Color.GREEN;

	@Getter
	private int[] previousKillTimes;

	@Getter
	private boolean isTimerActive = false;

	@Getter
	private Duration timeSince = Duration.ofSeconds(0);

	@Getter
	private long lastTickMillis;

	private Instant lastMonsterChange = Instant.EPOCH;

	private static final Pattern KILL_DURATION_PATTERN = Pattern.compile("(?i)^(?:Fight |Lap |Challenge |Corrupted challenge )?duration: <col=ff0000>[0-9:]+</col>\\. Personal best: ([0-9:]+)");
	private static final Pattern NEW_PB_PATTERN = Pattern.compile("(?i)^(?:Fight |Lap |Challenge |Corrupted challenge )?duration: <col=ff0000>([0-9:]+)</col> \\(new personal best\\)");

	@Override
	protected void startUp()
	{
		log.info("Boss Metrics plugin started!");
		previousKillTimes = new int[]{0, 0, 0, 0, 0};
		setCurrentMonster();
	}

	@Override
	protected void shutDown()
	{
		currentMonster = null;
		overlayManager.remove(overlay);
		overlayManager.remove(previousKillsOverlay);
		log.info("Boss Metrics plugin stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN && currentMonster != null)
		{
			personalBest = getPb(currentMonster.getName());
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			String message = chatMessage.getMessage();
			Matcher matcher = KILL_DURATION_PATTERN.matcher(message);

			if (matcher.find())
			{

			}

			matcher = NEW_PB_PATTERN.matcher(message);
			if (matcher.find())
			{

			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		lastTickMillis = System.currentTimeMillis();
		setCurrentMonster();

		if (isTimerActive) {
			Duration timerOffset = Duration.ofSeconds(config.getTimerOffset());
			timeSince = Duration.between(lastMonsterChange, Instant.now());
			log.info("TIME SINCE LAST CHECK: " + timeSince.getSeconds());
			if (timeSince.compareTo(timerOffset) >= 0 || currentMonster != null) {
				onTimerExpired();
			}
		}

	}

	private void onTimerExpired()
	{
		log.info("TIMER EXPIRED");
		isTimerActive = false;
		overlayManager.remove(previousKillsOverlay);
		overlayManager.remove(overlay);
		sessionKills = 0;
		previousKillTimes = new int[]{0,0,0,0,0};
		currentMonster = null;
		currentMonsterName = "";
	}

	private void onMonsterChanged(BossMetricsMonster newMonster)
	{
		if (newMonster == null)
		{
			//overlayManager.remove(overlay);
			//overlayManager.remove(previousKillsOverlay);
			lastMonsterChange = Instant.now();
			isTimerActive = true;
		}
		else
		{
			currentMonsterName = newMonster.getName();
			personalBest = getPb(newMonster.getName());
			overlayManager.add(overlay);
			if (config.getPreviousKillAmount() > 0)
			{
				overlayManager.add(previousKillsOverlay);
			}
		}
	}

	private void setCurrentMonster()
	{
		if (client.getLocalPlayer() != null)
		{

			final int playerRegionID = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();

			if (playerRegionID == 0)
			{
				return;
			}

			BossMetricsMonster newMonster = BossMetricsMonster.fromRegion(playerRegionID);

			if (currentMonster != newMonster)
			{
				currentMonster = newMonster;
				onMonsterChanged(currentMonster);
			}
		}
	}

	private int getPb(String boss)
	{
		Integer personalBest = configManager.getConfiguration("personalbest." + client.getUsername().toLowerCase(),
			boss.toLowerCase(), int.class);
		return personalBest == null ? 0 : personalBest;
	}

	String getDisplayTime(int secs)
	{
		if (secs <= 0)
		{
			return "-:--";
		}

		int seconds = secs % 60;
		int minutes = secs % 3600 / 60;
		int hours = secs / 3600;
		if (hours > 0)
		{
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		}
		return String.format("%d:%02d", minutes, seconds);
	}

	int getChangeTime(final int time)
	{
		final long diff = System.currentTimeMillis() - lastTickMillis;
		return time != -1 ? (int)((time * Constants.GAME_TICK_LENGTH - diff) / 1000d) : time;
	}

	@Provides
	BossMetricsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossMetricsConfig.class);
	}
}
