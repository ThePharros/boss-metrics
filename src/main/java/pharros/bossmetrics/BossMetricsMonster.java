package pharros.bossmetrics;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;

@Getter
enum BossMetricsMonster
{
    ABYSSAL_SIRE("Abyssal Sire", 0,0,11851, 11850, 12363, 12362),
    CERBERUS("Cerberus", 0,0,4883, 5140, 5395),
    COMMANDER_ZILYANA("Commander Zilyana", 0,0,11602),
    DKS("Dagannoth Kings", 0,0,11588, 11589),
    GENERAL_GRAARDOR("General Graardor", 0,0,11347),
    GIANT_MOLE("Giant Mole", 0,0,6993, 6992),
    GROTESQUE_GUARDIANS("Grotesque Guardians", 7851,7803,6727), //7803 or 7809?
    HYDRA("Alchemical Hydra", 0,0,5536),
    KQ("Kalphite Queen", 0,0,13972),
    KRAKEN("Kraken", 0,0,9116),
    KREEARRA("Kree'arra", 0,0,11346),
    KRIL_TSUTSAROTH("K'ril Tsutsaroth", 0,0,11603),
    SKOTIZO("Skotizo", 0,0,6810),
    SMOKE_DEVIL("Thermonuclear smoke devil", 0,0,9363, 9619),
    VORKATH("Vorkath", 0,0,9023),
    WINTERTODT("Wintertodt", 0,0,6462),
    ZALCANO("Zalcano", 0,0,13250),
    ZULRAH("Zulrah", 0,0,9007),
    NIGHTMARE("Nightmare of Ashihama", 0,0,15515);

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

    private static final Map<Integer, BossMetricsMonster> monsterStartIdMap = new HashMap<>();

    static
    {
        for (BossMetricsMonster monster : BossMetricsMonster.values())
        {
            monsterStartIdMap.put(monster.getNpcStartID(), monster);
        }
    }

    static final Map<Integer, BossMetricsMonster> monsterDeathAnimIdMap = new HashMap<>();

    static
    {
        for (BossMetricsMonster monster : BossMetricsMonster.values())
        {
            monsterStartIdMap.put(monster.getDeathAnimID(), monster);
        }
    }

    private final String name;
    private final int NpcStartID;
    private final int deathAnimID;

    @Nullable
    private int[] regionIds;

    BossMetricsMonster(String name, int NpcStartID, int deathAnimID, int... regionIds)
    {
        this.name = name;
        this.NpcStartID = NpcStartID;
        this.deathAnimID = deathAnimID;
        this.regionIds = regionIds;
    }

    public static BossMetricsMonster fromRegion(final int regionId)
    {
        return FROM_REGION.get(regionId);
    }

    public static BossMetricsMonster fromStartID(final int ID)
    {
        return monsterStartIdMap.get(ID);
    }

    public static BossMetricsMonster fromDeathAnimID(final int ID)
    {
        return monsterDeathAnimIdMap.get(ID);
    }
}
