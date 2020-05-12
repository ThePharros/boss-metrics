package pharros.bossmetrics;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class BossMetricsOverlay extends Overlay
{
	private final BossMetricsConfig config;
	private final Client client;
	private final BossMetricsPlugin plugin;
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	BossMetricsOverlay(Client client, BossMetricsConfig config, BossMetricsPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setPosition(OverlayPosition.TOP_RIGHT);
		this.config = config;
		this.plugin = plugin;
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final BossMetricsSession session = plugin.getSession();

		panelComponent.getChildren().clear();

		if (plugin.getState() == BossMetricsState.IN_SESSION_TIMEOUT)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Timeout in:")
				.leftColor(Color.WHITE)
				.right(plugin.getDisplayTimeRemaining(plugin.getSession().getSessionTimeRemaining()))
				.rightColor(Color.RED)
				.build());
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text(session.getCurrentMonster().getName())
			.color(Color.WHITE)
			.build());

		if (config.showSessionKillCount() || config.showPersonalBest() || config.showApproximateTime())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("")
				.leftColor(Color.WHITE)
				.right("")
				.rightColor(Color.WHITE)
				.build());
		}

		if (config.showSessionKillCount())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Session kills:")
				.leftColor(Color.WHITE)
				.right(Integer.toString(session.getSessionKc()))
				.rightColor(Color.WHITE)
				.build());
		}

		if (config.showPersonalBest())
		{
			String strPersonalBest = plugin.getDisplayTime(session.getPersonalBest());
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Personal best:")
				.leftColor(Color.WHITE)
				.right(strPersonalBest)
				.rightColor(Color.YELLOW)
				.build());
		}

		if (config.showApproximateTime())
		{
			Color currTimeColor = Color.GREEN;
			if (session.getKillTimer().getCurrSeconds() >= session.getPersonalBest())
			{
				currTimeColor = Color.WHITE;
			}
			String strCurrentTime = plugin.getDisplayTime((int)session.getKillTimer().getCurrSeconds());
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Approx. time:")
				.leftColor(Color.WHITE)
				.right(strCurrentTime)
				.rightColor(currTimeColor)
				.build());
		}

		return panelComponent.render(graphics);
	}
}