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

class BossMetricsKillCountOverlay extends Overlay
{
    private final BossMetricsConfig config;
    private final Client client;
    private final BossMetricsPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private BossMetricsKillCountOverlay (Client client, BossMetricsConfig config, BossMetricsPlugin plugin)
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
            .text("Session Kill Count")
            .color(Color.WHITE)
            .build());

        String txt;
        if (plugin.getCurrKillCount() == 1)
        {
            txt = " kill";
        } else
            {
            txt = " kills";
        }

        panelComponent.getChildren().add(TitleComponent.builder()
            .text(plugin.getCurrKillCount() + txt)
            .color(Color.GREEN)
            .build());

        return panelComponent.render(graphics);
    }
}