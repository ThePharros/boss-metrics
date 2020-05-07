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

class BossMetricsPreviousKillsOverlay extends Overlay
{
    private final BossMetricsConfig config;
    private final Client client;
    private final BossMetricsPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private int[] previousKillTimes;

    @Inject
    private BossMetricsPreviousKillsOverlay (Client client, BossMetricsConfig config, BossMetricsPlugin plugin)
    {
        setPosition(OverlayPosition.TOP_RIGHT);
        this.config = config;
        this.plugin = plugin;
        this.client = client;
        //previousKillTimes = new int[]{123, 188, 154, 185};
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
            .text("Previous Kills")
            .color(Color.WHITE)
            .build());

        for (int i = 0; i < config.getPreviousKillAmount(); i++)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Kill " + i)
                .leftColor(Color.WHITE)
                .right(plugin.getDisplayTime(plugin.getPreviousKillTimes()[i]))
                .rightColor(Color.WHITE)
                .build());
            i++;
        }

        return panelComponent.render(graphics);
    }
}