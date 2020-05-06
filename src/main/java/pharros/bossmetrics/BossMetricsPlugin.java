package pharros.bossmetrics;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
	private OverlayManager overlayManager;

	@Inject
	private BossMetricsOverlay overlay;

	@Getter
	private BossMetricsMonster currentMonster;

	@Getter
	private int personalBest;

	@Getter
	private boolean isInBossRegion;

	@Override
	protected void startUp()
	{
		log.info("Example started!");
		currentMonster = BossMetricsMonster.GROTESQUE_GUARDIANS;
		overlayManager.add(overlay);

		try {
			personalBest = getPb(client.getUsername(), currentMonster.getName());
		} catch (IOException e) {
			log.info("IOException caught!");
			personalBest = -1;
		}

	}

	@Override
	protected void shutDown()
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		//Player player = client.getLocalPlayer();
	}

	public int getPb(String username, String boss) throws IOException
	{
		HttpUrl url = RuneLiteAPI.getApiBase().newBuilder()
			.addQueryParameter("name", username)
			.addQueryParameter("boss", boss)
			.build();

		Request request = new Request.Builder()
			.url(url)
			.build();

		try (Response response = RuneLiteAPI.CLIENT.newCall(request).execute())
		{
			if (!response.isSuccessful())
			{
				throw new IOException("Unable to look up personal best!");
			}
			return Integer.parseInt(response.body().string());
		}
	}

	@Provides
	BossMetricsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossMetricsConfig.class);
	}
}
