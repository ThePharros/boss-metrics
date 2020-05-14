package pharros.bossmetrics;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;

@Getter
enum BossMetricsMonster
{
	//vanilla PBs
	CORRUPTED_GAUNTLET("Corrupted Gauntlet",0,0,true,7768),
	GAUNTLET("Gauntlet",0,0,true,7512),
	GROTESQUE_GUARDIANS("Grotesque Guardians", 7851, 7803, true, 6727), //npc start: 7803 or 7809?
	HESPORI("Hespori", 8583, 8225, true, 5021),
	HYDRA("Alchemical Hydra", 0, 0, true, 5536),
	JAD("TzTok-Jad", 0, 0, true, 9551),
	NIGHTMARE("Nightmare of Ashihama", 0, 0, true, 15515),
	SKOTIZO("Skotizo", 0, 0, true, 6810),
	VORKATH("Vorkath", 8059, 0, true, 9023),
	ZUK("TzKal-Zuk", 0, 0, true, 9043),
	ZULRAH("Zulrah", 2042, 5804, true, 8751, 9007, 9008);

	//SEREN(),
	//GALVEK(),

	//no vanilla PBs
	/*
	ABYSSAL_SIRE("Abyssal Sire", 0, 0, false, 11851, 11850, 12363, 12362),
	CERBERUS("Cerberus", 0, 0, false, 4883, 5140, 5395),
	COMMANDER_ZILYANA("Commander Zilyana", 0, 0, false, 11602),
	DKS("Dagannoth Kings", 0, 0, false, 11588, 11589),
	GENERAL_GRAARDOR("General Graardor", 0, 0, false, 11347),
	GIANT_MOLE("Giant Mole", 0, 0, false, 6993, 6992),
	KQ("Kalphite Queen", 0, 0, false, 13972),
	KRAKEN("Kraken", 0, 0, false, 9116),
	KREEARRA("Kree'arra", 0, 0, false, 11346),
	KRIL_TSUTSAROTH("K'ril Tsutsaroth", 0, 0, false, 11603),
	SMOKE_DEVIL("Thermonuclear smoke devil", 0, 0, false, 9363, 9619),
	WINTERTODT("Wintertodt", 0, 0, false, 6462),
	ZALCANO("Zalcano", 0, 0, false, 12126); //region: 12126 or 13250?
	 */
	//and many more

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
	private final boolean monsterInstance;

	@Nullable
	private int[] regionIds;

	BossMetricsMonster(String name, int NpcStartID, int deathAnimID, boolean monsterInstance, int... regionIds)
	{
		this.name = name;
		this.NpcStartID = NpcStartID;
		this.deathAnimID = deathAnimID;
		this.monsterInstance = monsterInstance;
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
