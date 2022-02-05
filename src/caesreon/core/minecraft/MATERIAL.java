package caesreon.core.minecraft;

import java.util.HashMap;
import java.util.Map;

public enum MATERIAL {
    air("0"),
    stone("1"),
    granite("1:1"),
    polished_granite("1:2"),
    diorite("1:3"),
    polished_diorite("1:4"),
    andesite("1:5"),
    polished_andesite("1:6"),
    grass_block("2"),
    dirt("3"),
    coarse_dirt("3:1"),
    podzol("3:2"),
    cobblestone("4"),

    oak_planks("5"),
    spruce_planks("5:1"),
    birch_planks("5:2"),
    jungle_planks("5:3"),
    acacia_planks("5:4"),
    dark_oak_planks("5:5"),

    oak_sapling("6"),
    spruce_sapling("6:1"),
    birch_sapling("6:2"),
    jungle_sapling("6:3"),
    acacia_sapling("6:4"),
    dark_oak_sapling("6:5"),

    bedrock("7"),
    sand("12"),
    red_sand("12:1"),
    gravel("13"),

    gold_ore("14"),
    iron_ore("15"),
    coal_ore("16"),

    oak_log("17"),
    spruce_log("17:1"),
    birch_log("17:2"),
    jungle_log("17:3"),
    acacia_log("162"),
    dark_oak_log("162:1"),
    /*
    stripped_oak_log
    stripped_spruce_log
    stripped_birch_log
    stripped_jungle_log
    stripped_acacia_log
    stripped_dark_oak_log
    stripped_oak_wood
    stripped_spruce_wood
    stripped_birch_wood
    stripped_jungle_wood
    stripped_acacia_wood
    stripped_dark_oak_wood

    oak_wood
    spruce_wood
    birch_wood
    jungle_wood
    acacia_wood
    dark_oak_wood
    */
    oak_leaves("18"),
    spruce_leaves("18:1"),
    birch_leaves("18:2"),
    jungle_leaves("18:3"),
    acacia_leaves("161"),
    dark_oak_leaves("161:1"),


    sponge("19"),
    wet_sponge("19:1"),
    glass("20"),
    lapis_ore("21"),
    lapis_block("22"),
    dispenser("23"),
    sandstone("24"),
    chiseled_sandstone("24:1"),
    cut_sandstone("24:2"),
    note_block("25"),
    powered_rail("27"),
    detector_rail("28"),
    sticky_piston("29"),
    cobweb("30"),
    //grass
    fern("31:2"),
    dead_bush("32"),
    //seagrass
    //sea_pickle
    piston("33"),

    white_wool("35"),
    orange_wool("35:1"),
    magenta_wool("35:2"),
    light_blue_wool("35:3"),
    yellow_wool("35:4"),
    lime_wool("35:5"),
    pink_wool("35:6"),
    gray_wool("35:7"),
    light_gray_wool("35:8"),
    cyan_wool("35:9"),
    purple_wool("35:10"),
    blue_wool("35:11"),
    brown_wool("35_12"),
    green_wool("35:13"),
    red_wool("35:14"),
    black_wool("35:15"),

    dandelion("37"),
    poppy("38"),
    blue_orchid("38:1"),
    allium("38:2"),
    azure_bluet("38:3"),
    red_tulip("38:4"),
    orange_tulip("38:5"),
    white_tulip("38:6"),
    pink_tulip("38:7"),
    oxeye_daisy("38:8"),

    brown_mushroom("39"),
    red_mushroom("40"),

    gold_block("41"),
    iron_block("42"),

    oak_slab("126"),
    spruce_slab("126:1"),
    birch_slab("126:2"),
    jungle_slab("126:3"),
    acacia_slab("126:4"),
    dark_oak_slab("126:5"),
    stone_slab("44:2"),
    sandstone_slab("44:1"),
    //petrified_oak_slab
    cobblestone_slab("44:3"),
    brick_slab("44:4"),
    stone_brick_slab("44:5"),
    nether_brick_slab("44:6"),
    quartz_slab("44:7"),
    red_sandstone_slab("182"),
    purpur_slab("205"),
    //prismarine_slab

    //prismarine_brick_slab
    //dark_prismarine_slab

    smooth_quartz("43:7"),
    //smooth_red_sandstone
    //smooth_sandstone
    smooth_stone("43:8"),

    bricks("45"),
    tnt("46"),
    bookshelf("47"),
    mossy_cobblestone("48"),
    obsidian("49"),

    torch("50"),
    end_rod("198"),
    chorus_plant("199"),
    chorus_flower("200"),

    purpur_block("201"),
    purpur_pillar("202"),
    purpur_stairs("203"),

    spawner("52"),
    oak_stairs("53"),
    //---------------------------------------------------------------------------------------
    chest("54"),
    diamond_ore("56"),
    diamond_block("57"),
    crafting_table("58"),
    farmland("60"),
    furnace("61"),
    ladder("65"),
    rail("66"),
    cobblestone_stairs("67"),
    lever("69"),
    stone_pressure_plate("70"),
    oak_pressure_plate("72"),
    /*
    spruce_pressure_plate
    birch_pressure_plate
    jungle_pressure_plate
    acacia_pressure_plate
    dark_oak_pressure_plate
    */
    redstone_ore("73"),
    redstone_torch("76"),
    stone_button("77"),
    snow("78"),
    ice("79"),
    snow_block("80"),
    cactus("81"),
    clay("337"),
    jukebox("84"),
    oak_fence("85"),
    spruce_fence("188"),
    birch_fence("189"),
    jungle_fence("190"),
    acacia_fence("192"),
    dark_oak_fence("191"),
    pumpkin("86"),
    //carved_pumpkin
    netherrack("87"),
    soul_sand("88"),
    glowstone("89"),
    jack_o_lantern("91"),
    oak_trapdoor("96"),
    /*
    spruce_trapdoor
    birch_trapdoor
    jungle_trapdoor
    acacia_trapdoor
    dark_oak_trapdoor
    */
    infested_stone("97"),
    infested_cobblestone("97:1"),
    infested_stone_bricks("97:2"),
    infested_mossy_stone_bricks("97:3"),
    infested_cracked_stone_bricks("97:4"),
    infested_chiseled_stone_bricks("97:5"),
    stone_bricks("98"),
    mossy_stone_bricks("98:1"),
    cracked_stone_bricks("98:2"),
    chiseled_stone_bricks("98:3"),
    brown_mushroom_block("99"),
    red_mushroom_block("100"),
    //mushroom_stem
    iron_bars("101"),
    glass_pane("102"),
    melon("103"),
    vine("106"),
    oak_fence_gate("107"),
    spruce_fence_gate("183"),
    birch_fence_gate("184"),
    jungle_fence_gate("185"),
    acacia_fence_gate("187"),
    dark_oak_fence_gate("186"),
    brick_stairs("108"),
    stone_brick_stairs("109"),
    mycelium("110"),
    lily_pad("111"),
    nether_bricks("112"),
    nether_brick_fence("113"),
    nether_brick_stairs("114"),
    enchanting_table("116"),
    end_portal_frame("120"),
    end_stone("121"),
    end_stone_bricks("206"),
    dragon_egg("122"),
    redstone_lamp("123"),
    sandstone_stairs("128"),
    emerald_ore("129"),
    ender_chest("130"),
    tripwire_hook("131"),
    emerald_block("133"),
    spruce_stairs("134"),
    birch_stairs("135"),
    jungle_stairs("136"),
    command_block("137"),
    beacon("138"),
    cobblestone_wall("139"),
    mossy_cobblestone_wall("139:1"),
    oak_button("143"),
    /*
    spruce_button
    birch_button
    jungle_button
    acacia_button
    dark_oak_button
    */
    anvil("145"),
    chipped_anvil("145:1"),
    damaged_anvil("145:2"),
    trapped_chest("146"),
    light_weighted_pressure_plate("147"),
    heavy_weighted_pressure_plate("148"),
    daylight_detector("151"),
    redstone_block("152"),
    nether_quartz_ore("153"),
    hopper("154"),
    chiseled_quartz_block("155:1"),
    quartz_block("155"),
    quartz_pillar("155:2"),
    quartz_stairs("156"),
    activator_rail("157"),
    dropper("158"),
    white_terracotta("159"),
    orange_terracotta("159:1"),
    magenta_terracotta("159:2"),
    light_blue_terracotta("159:3"),
    yellow_terracotta("159:4"),
    lime_terracotta("159:5"),
    pink_terracotta("159:6"),
    gray_terracotta("159:7"),
    light_gray_terracotta("159:8"),
    cyan_terracotta("159:9"),
    purple_terracotta("159:10"),
    blue_terracotta("159:11"),
    brown_terracotta("159:12"),
    green_terracotta("159:13"),
    red_terracotta("159:14"),
    black_terracotta("159:15"),
    barrier("166"),
    iron_trapdoor("167"),
    hay_block("170"),
    white_carpet("171"),
    orange_carpet("171:1"),
    magenta_carpet("171:2"),
    light_blue_carpet("171:3"),
    yellow_carpet("171:4"),
    lime_carpet("171:5"),
    pink_carpet("171:6"),
    gray_carpet("171:7"),
    light_gray_carpet("171:8"),
    cyan_carpet("171:9"),
    purple_carpet("171:10"),
    blue_carpet("171:11"),
    brown_carpet("171:12"),
    green_carpet("171:13"),
    red_carpet("171:14"),
    black_carpet("171:15"),
    terracotta("172"),
    //------------------------------------------------------------------------------------------------------------------
    //TODO: https://minecraftitemids.com/3-9
    ende("");

    private static Map<String, MATERIAL> IdZuEnumMap = new HashMap<>();

    static {
        for (MATERIAL id : MATERIAL.values()) {
            IdZuEnumMap.put(id.getMinecraftItemID(), id);
        }
    }

    private String McID = "";

    MATERIAL(String McID) {
        this.McID = McID;
    }

    public static MATERIAL getMaterialfromString(String NumerischeID) {
        return IdZuEnumMap.get(NumerischeID);
    }

    public String getMinecraftItemID() {
        return McID;
    }
}
