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
    private BossMetricsSession session;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    BossMetricsOverlay(Client client, BossMetricsConfig config, BossMetricsPlugin plugin)
    {
        setPosition(OverlayPosition.TOP_RIGHT);
        this.config = config;
        this.plugin = plugin;
        this.client = client;
        this.session = plugin.getSession();
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        if (plugin.getState() == BossMetricsState.IN_SESSION_TIMEOUT)
        {
            long diff = System.currentTimeMillis() - plugin.getLastTickMillis();
            int time = 1 + (int) (((long) config.getTimerOffset() * 1000 - plugin.getTimeSince().toMillis() - diff) / 1000d);
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Timeout in:")
                .leftColor(Color.WHITE)
                .right(plugin.getDisplayTime(time))
                .rightColor(Color.RED)
                .build());
        }

        panelComponent.getChildren().add(TitleComponent.builder()
            .text(session.getCurrentMonster().getName())
            .color(Color.WHITE)
            .build());

        panelComponent.getChildren().add(LineComponent.builder()
            .left("")
            .leftColor(Color.WHITE)
            .right("")
            .rightColor(Color.WHITE)
            .build());

        if (config.showSessionKillCount())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Session kills:")
                .leftColor(Color.WHITE)
                .right(Integer.toString(plugin.getSessionKills()))
                .rightColor(Color.WHITE)
                .build());
        }

        String strPersonalBest = plugin.getDisplayTime(plugin.getPersonalBest());
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Personal best:")
            .leftColor(Color.WHITE)
            .right(strPersonalBest)
            .rightColor(Color.YELLOW)
            .build());

        String strCurrentTime = plugin.getDisplayTime(plugin.getCurrentTime());
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Current time:")
            .leftColor(Color.WHITE)
            .right(strCurrentTime)
            .rightColor(plugin.getColCurrentTime())
            .build());
        return panelComponent.render(graphics);
    }
}