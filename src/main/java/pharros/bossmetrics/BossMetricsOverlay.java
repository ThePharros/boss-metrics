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

class BossMetricsOverlay extends Overlay
{
    private final BossMetricsConfig config;
    private final Client client;
    private final BossMetricsPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private BossMetricsOverlay (Client client, BossMetricsConfig config, BossMetricsPlugin plugin)
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
            .text(plugin.getCurrentMonster().getName())
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
                .left("Killstreak:")
                .leftColor(Color.WHITE)
                .right(Integer.toString(plugin.getCurrKillCount()))
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