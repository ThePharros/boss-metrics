package pharros.bossmetrics;

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

    private final String name;

    @Nullable
    private final int[] regionIDs;

    BossMetricsMonster(String name, int... regionIDs)
    {
        this.name = name;
        this.regionIDs = regionIDs;
    }

}
