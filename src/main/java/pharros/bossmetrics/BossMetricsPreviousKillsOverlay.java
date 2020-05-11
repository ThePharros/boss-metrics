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

    @Inject
    BossMetricsPreviousKillsOverlay(Client client, BossMetricsConfig config, BossMetricsPlugin plugin)
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
        if (config.previousKillAmount() > 0)
        {
            final BossMetricsSession session = plugin.getSession();
            panelComponent.getChildren().clear();

            panelComponent.getChildren().add(TitleComponent.builder()
                .text("Previous Kills")
                .color(Color.WHITE)
                .build());

            double sum = 0.0;
            int totalRecordedTimes = 0;
            for (int i = 0; i < config.previousKillAmount(); i++)
            {
                if (i == 0)
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                        .left("Last kill")
                        .leftColor(Color.WHITE)
                        .right(plugin.getDisplayTime(session.getPreviousKillTimes().get(i)))
                        .rightColor(Color.WHITE)
                        .build());
                }
                else
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                        .left(i + 1 + " kills ago")
                        .leftColor(Color.WHITE)
                        .right(plugin.getDisplayTime(session.getPreviousKillTimes().get(i)))
                        .rightColor(Color.WHITE)
                        .build());
                }

                if (config.showPreviousKillAverage() && session.getPreviousKillTimes().get(i) > 0)
                {
                    sum += session.getPreviousKillTimes().get(i);
                    totalRecordedTimes++;
                }
            }

            if (config.showPreviousKillAverage() && config.previousKillAmount() > 1)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("")
                    .leftColor(Color.WHITE)
                    .right("")
                    .rightColor(Color.WHITE)
                    .build());

                if (totalRecordedTimes > 0)
                {
                    sum /= 1.0 * totalRecordedTimes;
                }

                panelComponent.getChildren().add(LineComponent.builder()
                    .left(config.previousKillAmount() + "-kill average")
                    .leftColor(Color.WHITE)
                    .right(plugin.getDisplayTime((int)Math.round(sum)))
                    .rightColor(Color.YELLOW)
                    .build());
            }
            return panelComponent.render(graphics);
        }
        else
        {
            panelComponent.getChildren().clear();
            return panelComponent.render(graphics);
        }
    }
}