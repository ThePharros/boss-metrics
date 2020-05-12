package pharros.bossmetrics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter(AccessLevel.PACKAGE)
enum BossMetricsState
{
	NO_SESSION,
	IN_SESSION,
	IN_SESSION_TIMEOUT,
}
