package pharros.bossmetrics;

import com.google.inject.Provides;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.NPC;
import static net.runelite.api.NpcID.VORKATH_8058;
import static net.runelite.api.NpcID.VORKATH_8059;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.NpcSpawned;
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

	@Getter(AccessLevel.PACKAGE)
	private BossMetricsSession session;

	@Getter(AccessLevel.PACKAGE)
	private BossMetricsState state = BossMetricsState.NO_SESSION;

	private BossMetricsMonster newMonster;

	private static final Pattern KILL_DURATION_PATTERN = Pattern.compile("(?i)^(?:Fight |Lap |Challenge |Corrupted challenge )?duration: <col=ff0000>([0-9:]+)</col>\\. Personal best: [0-9:]+");
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
		stopSession();
		log.info("Boss Metrics plugin stopped!");
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
				log.info("KILL COUNT PATTERN FOUND, MATCHER GROUP 1 IS " + matcher.group(1));
				String boss = matcher.group(1);
				if (boss.equals(session.getCurrentMonster().getName()))
				{
					session.incrementKc();
				}
			}

			matcher = KILL_DURATION_PATTERN.matcher(message);
			if (matcher.find())
			{
				log.info("KILL DUR PATTERN FOUND. MATCHER GROUP 1 = " + matcher.group(1));
				int seconds = timeStringToSeconds(matcher.group(1));
				session.recordPreviousTime(seconds);
			}

			matcher = NEW_PB_PATTERN.matcher(message);
			if (matcher.find())
			{
				log.info("NEW PB PATTERN FOUND. MATCHER GROUP 1 = " + matcher.group(1));
				int seconds = timeStringToSeconds(matcher.group(1));
				session.recordPreviousTime(seconds);
			}
		}
	}

	/*
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		//log.info("ANIMCHANGED > ACTOR: 	" + event.getActor().getName() + ", ID: " + event.getActor().getAnimation());

		//Grotesque Guardians
		if (session != null && event.getActor().getAnimation() == 390)
		{
			session.startKillTimer();
		}
	}
	*/

	/*
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof NPC))
		{
			return;
		}

		//log.info("MONSTER ANIM DETECTED: " + event.getActor().getName() + ", ANIM ID: " + event.getActor().getAnimation());

		final BossMetricsMonster dyingMonster = BossMetricsMonster.monsterDeathAnimIdMap.get(event.getActor().getAnimation());

		log.info("DYING MONSTER VAR: " + dyingMonster.getName());

		if (dyingMonster != null)
		{
			log.info("DYING MONSTER DETECTED: " + dyingMonster.getName());
		}
	}
	 */
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() instanceof Player)
		{
			log.info("PLAYER: " + event.getActor().getName() + ", ANIM ID: " + event.getActor().getAnimation());
		}

		//Animation based kill timer events
		if (event.getActor() instanceof Player && session != null)
		{
			switch (event.getActor().getAnimation())
			{
				case 827: //Vorkath start
					session.startKillTimer();
					break;
				case 839: //Vorkath end
					if (session.getKillTimer() != null)
					{
						session.stopKillTimer();
					}
					break;
			}
		}

		//default
		if (!(event.getActor() instanceof NPC))
		{
			return;
		}
		log.info("MONSTER: " + event.getActor().getName() + ", ANIM ID: " + event.getActor().getAnimation());
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		log.info("NPC SPAWNED! NAME: " + event.getNpc().getName() + ", ID: " + event.getNpc().getId());

		//if Vorkath spawned, ignore the onspawn event and use animation event instead
		if (event.getNpc().getId() == VORKATH_8058 || event.getNpc().getId() == VORKATH_8059)
		{
			return;
		}

		if (session != null &&
			session.getKillTimer() == null &&
			event.getNpc().getId() == session.getCurrentMonster().getNpcStartID())
		{
			session.startKillTimer();
		}
	}

	/*
	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (session != null &&
			session.getKillTimer() != null &&
			event.getNpc().getId() == session.getCurrentMonster().getNpcStartID())
		{
			//log.info("DESPAWNED MONSTER: " + event.getNpc().getName() + ", ID: " + event.getNpc().getId());
			session.recordPreviousTime(session.getKillTimer().getTimeDuration().getSeconds());
		}
	}
	*/

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		updateBossMetricsState();
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

			//is player in a plugin-valid boss region?
			newMonster = BossMetricsMonster.fromRegion(playerRegionID);

			//leaving boss area, start timeout
			if (newMonster == null && state == BossMetricsState.IN_SESSION)
			{
				log.info("START SESSION TIMEOUT");
				setState(BossMetricsState.IN_SESSION_TIMEOUT);
			}

			//not in boss area and in timeout
			if (newMonster == null && state == BossMetricsState.IN_SESSION_TIMEOUT)
			{
				final long timerSeconds = session.getTimeoutTimer().getTimeDuration().getSeconds();
				log.info("TIME REMAINING: " + timerSeconds);
				if (timerSeconds < 0)
				{
					stopSession();
				}
			}

			//entered boss area for first time
			if (newMonster != null && state == BossMetricsState.NO_SESSION)
			{
				log.info("NEW MONSTER IS: " + newMonster);
				setState(BossMetricsState.IN_SESSION);
			}

			//entered boss area during session timeout
			if (newMonster != null && state == BossMetricsState.IN_SESSION_TIMEOUT)
			{
				log.info("EXPIRE SESSION TIMEOUT");
				setState(BossMetricsState.IN_SESSION);
			}
		}
	}

	private void onBossMetricsStateChanged(BossMetricsState state)
	{
		switch (state)
		{
			case NO_SESSION:
				break;
			case IN_SESSION:
				if (session == null)
				{
					startSession();
				}
				else if (session.getCurrentMonster() != newMonster)
				{
					stopSession();
					startSession();
				}
				else if (session.getTimeoutTimer() != null)
				{
					session.stopTimeoutTimer();
				}
				break;
			case IN_SESSION_TIMEOUT:
				session.startTimeoutTimer(config.sessionTimeout());
				break;
		}
	}

	private void setState(BossMetricsState newState)
	{
		state = newState;
		onBossMetricsStateChanged(state);
	}

	int getPb(String boss)
	{
		Integer personalBest = configManager.getConfiguration("personalbest." + client.getUsername().toLowerCase(),
			boss.toLowerCase(), int.class);
		return personalBest == null ? 0 : personalBest;
	}

	private void startSession()
	{
		session = new BossMetricsSession(this, newMonster);
		overlayManager.add(overlay);
		if (config.previousKillAmount() > 0)
		{
			overlayManager.add(previousKillsOverlay);
		}
		if (state != BossMetricsState.IN_SESSION)
		{
			setState(BossMetricsState.IN_SESSION);
		}
	}

	private void stopSession()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(previousKillsOverlay);
		session = null;
		if (state != BossMetricsState.NO_SESSION)
		{
			setState(BossMetricsState.NO_SESSION);
		}
		log.info("SESSION EXPIRED");
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

	@Provides
	BossMetricsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossMetricsConfig.class);
	}
}
