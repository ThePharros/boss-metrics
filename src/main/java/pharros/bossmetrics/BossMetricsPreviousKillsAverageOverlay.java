package pharros.bossmetrics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class BossMetricsPreviousKillsAverageOverlay extends Overlay
{
    private final BossMetricsConfig config;
    private final Client client;
    private final BossMetricsPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private BossMetricsPreviousKillsAverageOverlay (Client client, BossMetricsConfig config, BossMetricsPlugin plugin)
    {
        setPosition(OverlayPosition.TOP_RIGHT);
        this.config = config;
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
            .text(config.getPreviousKillAmount() + "-Kill Average")
            .color(Color.WHITE)
            .build());

        int sum = 0;
        for (int i = 0; i < config.getPreviousKillAmount(); i++)
        {
            sum += plugin.getPreviousKillTimes()[i];
        }
        sum /= config.getPreviousKillAmount();
        panelComponent.getChildren().add(TitleComponent.builder()
            .text(plugin.getDisplayTime(sum))
            .color(Color.YELLOW)
            .build());

        return panelComponent.render(graphics);
    }
}