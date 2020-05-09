package pharros.bossmetrics;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;

@Getter
enum BossMetricsMonster
{
    ABYSSAL_SIRE("Abyssal Sire", 11851, 11850, 12363, 12362),
    CERBERUS("Cerberus", 4883, 5140, 5395),
    COMMANDER_ZILYANA("Commander Zilyana", 11602),
    DKS("Dagannoth Kings", 11588, 11589),
    GENERAL_GRAARDOR("General Graardor", 11347),
    GIANT_MOLE("Giant Mole", 6993, 6992),
    GROTESQUE_GUARDIANS("Grotesque Guardians", 6727),
    HYDRA("Alchemical Hydra", 5536),
    KQ("Kalphite Queen", 13972),
    KRAKEN("Kraken", 9116),
    KREEARRA("Kree'arra", 11346),
    KRIL_TSUTSAROTH("K'ril Tsutsaroth", 11603),
    SKOTIZO("Skotizo", 6810),
    SMOKE_DEVIL("Thermonuclear smoke devil", 9363, 9619),
    VORKATH("Vorkath", 9023),
    WINTERTODT("Wintertodt", 6462),
    ZALCANO("Zalcano", 13250),
    ZULRAH("Zulrah", 9007),
    NIGHTMARE("Nightmare of Ashihama", 15515);

    private static final Map<Integer, BossMetricsMonster> FROM_REGION;

    static
    {
        ImmutableMap.Builder<Integer, BossMetricsMonster> regionMapBuilder = new ImmutableMap.Builder<>();
        for (BossMetricsMonster monster : BossMetricsMonster.values())
        {
            if (monster.getRegionIds() == null)
            {
                continue;
            }

            for (int region : monster.getRegionIds())
            {
                regionMapBuilder.put(region, monster);
            }
        }
        FROM_REGION = regionMapBuilder.build();
    }

    private final String name;

    @Nullable
    private int[] regionIds;

    BossMetricsMonster(String name, int... regionIds)
    {
        this.name = name;
        this.regionIds = regionIds;
    }

    public static BossMetricsMonster fromRegion(final int regionId)
    {
        return FROM_REGION.get(regionId);
    }
}
