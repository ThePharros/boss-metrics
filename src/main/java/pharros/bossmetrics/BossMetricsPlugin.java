package pharros.bossmetrics;

import com.google.inject.Provides;
import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.NpcSpawned;
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
	private int personalBest = -1;

	@Getter
	private int currentTime = 0;

	@Getter
	private int currentKc = 0;

	@Getter
	private int sessionKills = 0;

	@Getter
	private Color colCurrentTime = Color.GREEN;

	@Getter
	private boolean isTimerActive = false;

	@Getter(AccessLevel.PACKAGE)
	private BossMetricsSession session = null;

	@Getter
	private Duration timeSince = Duration.ofSeconds(0);

	@Getter
	private long lastTickMillis;

	@Getter
	private Instant lastMonsterChange = Instant.EPOCH;

	@Getter
	private BossMetricsState state = BossMetricsState.NO_SESSION;

	private BossMetricsTimer timer;

	private static final Pattern KILL_DURATION_PATTERN = Pattern.compile("(?i)^(?:Fight |Lap |Challenge |Corrupted challenge )?duration: <col=ff0000>[0-9:]+</col>\\. Personal best: ([0-9:]+)");
	private static final Pattern NEW_PB_PATTERN = Pattern.compile("(?i)^(?:Fight |Lap |Challenge |Corrupted challenge )?duration: <col=ff0000>([0-9:]+)</col> \\(new personal best\\)");
	private static final Pattern KILLCOUNT_PATTERN = Pattern.compile("Your (.+) (?:kill|harvest|lap|completion) count is: <col=ff0000>(\\d+)</col>");

	@Override
	protected void startUp()
	{
		log.info("Boss Metrics plugin started!");
		updateBossMetricsState();
	}

	@Override
	protected void shutDown()
	{
		session.expire();
		session = null;
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
		if (session != null && chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			String message = chatMessage.getMessage();

			Matcher matcher = KILLCOUNT_PATTERN.matcher(message);
			if (matcher.find())
			{
				String boss = matcher.group(1);
				if (boss.equals(session.getCurrentMonster().getName())) {
					session.incrementKc();
				}
			}

			matcher = KILL_DURATION_PATTERN.matcher(message);
			if (matcher.find())
			{
				matchKillTime(matcher);
			}

			matcher = NEW_PB_PATTERN.matcher(message);
			if (matcher.find())
			{
				matchKillTime(matcher);
			}
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		log.info("NPC SPAWNED! ID: " + event.getNpc().getId() + ", NAME: " + event.getNpc().getName());

		if (currentMonster != null)
		{
			if (currentMonster.getNpcStartID() == event.getNpc().getId())
			{
				log.info("CURRENT MONSTER: " + currentMonster.getName() + ", CURRENT START ID: " + currentMonster.getNpcStartID());
				timer.start();
			}
		}
	}

	/*
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof NPC))
		{
			return;
		}

		log.info("MONSTER ANIM DETECTED: " + event.getActor().getName() + ", ANIM ID: " + event.getActor().getAnimation());

		final BossMetricsMonster dyingMonster = BossMetricsMonster.monsterDeathAnimIdMap.get(event.getActor().getAnimation());

		log.info("DYING MONSTER VAR: " + dyingMonster.getName());

		if (dyingMonster != null)
		{
			log.info("DYING MONSTER DETECTED: " + dyingMonster.getName());
		}
	}
	 */

	/*
	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (currentMonster != null)
		{
			log.info("DESPAWNED MONSTER: " + event.getNpc().getName() + ", ID: " + event.getNpc().getId());
			if (currentMonster.getNpcEndID() == event.getNpc().getId())
			{
				log.info("LOGGED CURRENT MONSTER: " + currentMonster.getName() + ", CURRENT END ID: " + currentMonster.getNpcEndID());
				timer.end(true);
			}
		}
	}
	 */

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		lastTickMillis = System.currentTimeMillis();
		updateBossMetricsState();
		log.info(state.toString());
		log.info("CURRENT SESSION: " + session);




		if (isTimerActive) {
			Duration timerOffset = Duration.ofSeconds(config.getTimerOffset());
			timeSince = Duration.between(lastMonsterChange, Instant.now());
			log.info("TIME SINCE LAST CHECK: " + timeSince.getSeconds());
			if (timeSince.compareTo(timerOffset) >= 0 || currentMonster != null) {
				onTimerExpired();
			}
		}

		if (timer != null)
		{
			timer.update();
			currentTime = (int)timer.getCurrSeconds();
		}
	}

	private void setPb(String boss, int seconds)
	{
		configManager.setConfiguration("personalbest." + client.getUsername().toLowerCase(),
			boss.toLowerCase(), seconds);
	}

	private static int timeStringToSeconds(String timeString)
	{
		String[] s = timeString.split(":");
		if (s.length == 2) // mm:ss
		{
			return Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
		}
		else if (s.length == 3) // h:mm:ss
		{
			return Integer.parseInt(s[0]) * 60 * 60 + Integer.parseInt(s[1]) * 60 + Integer.parseInt(s[2]);
		}
		return Integer.parseInt(timeString);
	}

	private void matchKillTime(Matcher matcher)
	{
		int seconds = timeStringToSeconds(matcher.group(0));
		session.recordPreviousTime(seconds);
	}

	private void onTimerExpired()
	{
		log.info("TIMER EXPIRED");
		isTimerActive = false;
		overlayManager.remove(previousKillsOverlay);
		overlayManager.remove(overlay);
		session = null;
		currentMonster = null;
	}

	private void updateBossMetricsState()
	{
		if (client.getLocalPlayer() != null)
		{
			//get player's current region ID
			final int playerRegionID = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();

			if (playerRegionID == 0)
			{
				return;
			}

			//is player in boss region?
			BossMetricsMonster newMonster = BossMetricsMonster.fromRegion(playerRegionID);

			//leaving boss area, start timeout
			if (newMonster == null && state == BossMetricsState.IN_SESSION)
			{
				setState(BossMetricsState.IN_SESSION_TIMEOUT);
				session.updateSessionTimeoutStart();
			}

			//not in boss area and timeout expired
			if (newMonster == null && state == BossMetricsState.IN_SESSION_TIMEOUT)
			{
				if (session.getTimeSinceTimeout() > config.getTimerOffset())
				{
					overlayManager.remove(overlay);
					overlayManager.remove(previousKillsOverlay);
					session.expire();
					session = null;
					setState(BossMetricsState.NO_SESSION);
				}
			}

			//entered boss area for first time
			if (newMonster != null && state == BossMetricsState.NO_SESSION)
			{
				log.info("NEW MONSTER IS: " + newMonster);
				setState(BossMetricsState.IN_SESSION);
				session = new BossMetricsSession(this, client, configManager, newMonster);
				//overlay = new BossMetricsOverlay();
				overlayManager.add(overlay);
				if (config.getPreviousKillAmount() > 0)
				{
					overlayManager.add(previousKillsOverlay);
				}
			}

			//entered boss area during session timeout
			if (state == BossMetricsState.IN_SESSION_TIMEOUT)
			{
				session.resetTimeout();
			}
		}
	}

	private void setState(BossMetricsState newState)
	{
		state = newState;
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

	void startPbTimer() {
		timer.start();
	}

	@Provides
	BossMetricsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossMetricsConfig.class);
	}
}
