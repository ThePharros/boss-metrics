package pharros.bossmetrics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum BossMetricsState
{
    NO_SESSION,
    IN_SESSION,
    IN_SESSION_TIMEOUT,
}
