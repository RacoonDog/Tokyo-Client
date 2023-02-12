package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import io.github.racoondog.tokyo.mixininterface.IChunkDeltaUpdateS2CPacket;
import io.github.racoondog.tokyo.mixininterface.IPackedIntegerArray;
import io.github.racoondog.tokyo.mixininterface.IPalettedContainer;
import io.github.racoondog.tokyo.utils.CSVWriter;
import io.github.racoondog.tokyo.utils.ExportUtils;
import io.github.racoondog.tokyo.utils.FileUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.EmptyPaletteStorage;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Math;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class OldChunks extends Module {
    public static final OldChunks INSTANCE = new OldChunks();
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgRender = this.settings.createGroup("render");

    // General

    private final Setting<Boolean> generateList = sgGeneral.add(new BoolSetting.Builder()
        .name("generate-list")
        .description("Enables list generation mode, which uses the blocks found in your world to refine the block check. Requires you to be in untouched chunks.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> resetList = sgGeneral.add(new BoolSetting.Builder()
        .name("reset-list")
        .description("Reset the block check list for the current dimension.")
        .defaultValue(false)
        .visible(generateList::get)
        .onChanged(o -> {
            if (o) regenerateList();
        })
        .build()
    );

    public final Setting<Boolean> blockCheck = sgGeneral.add(new BoolSetting.Builder()
        .name("block-check")
        .description("Block check.")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<Block>> overworldBlocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("overworld-blocks")
        .description("Blocks that cannot generate naturally in the Overworld.")
        .defaultValue(compileList("minecraft:polished_diorite", "minecraft:mangrove_planks", "minecraft:bamboo_planks", "minecraft:bamboo_mosaic", "minecraft:oak_sapling", "minecraft:spruce_sapling", "minecraft:birch_sapling", "minecraft:jungle_sapling", "minecraft:dark_oak_sapling", "minecraft:nether_gold_ore", "minecraft:bamboo_block", "minecraft:stripped_birch_log", "minecraft:stripped_jungle_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_mangrove_log", "minecraft:stripped_bamboo_block", "minecraft:oak_wood", "minecraft:spruce_wood", "minecraft:birch_wood", "minecraft:jungle_wood", "minecraft:dark_oak_wood", "minecraft:mangrove_wood", "minecraft:stripped_birch_wood", "minecraft:stripped_jungle_wood", "minecraft:stripped_acacia_wood", "minecraft:stripped_dark_oak_wood", "minecraft:stripped_mangrove_wood", "minecraft:sponge", "minecraft:lapis_block", "minecraft:dispenser", "minecraft:magenta_bed", "minecraft:light_blue_bed", "minecraft:lime_bed", "minecraft:pink_bed", "minecraft:gray_bed", "minecraft:light_gray_bed", "minecraft:cyan_bed", "minecraft:brown_bed", "minecraft:green_bed", "minecraft:black_bed", "minecraft:powered_rail", "minecraft:detector_rail", "minecraft:piston", "minecraft:orange_wool", "minecraft:magenta_wool", "minecraft:lime_wool", "minecraft:pink_wool", "minecraft:purple_wool", "minecraft:brown_wool", "minecraft:green_wool", "minecraft:red_wool", "minecraft:black_wool", "minecraft:moving_piston", "minecraft:wither_rose", "minecraft:iron_block", "minecraft:tnt", "minecraft:chiseled_bookshelf", "minecraft:fire", "minecraft:diamond_block", "minecraft:oak_sign", "minecraft:spruce_sign", "minecraft:birch_sign", "minecraft:acacia_sign", "minecraft:jungle_sign", "minecraft:dark_oak_sign", "minecraft:mangrove_sign", "minecraft:bamboo_sign", "minecraft:birch_wall_sign", "minecraft:acacia_wall_sign", "minecraft:jungle_wall_sign", "minecraft:dark_oak_wall_sign", "minecraft:mangrove_wall_sign", "minecraft:bamboo_wall_sign", "minecraft:oak_hanging_sign", "minecraft:spruce_hanging_sign", "minecraft:birch_hanging_sign", "minecraft:acacia_hanging_sign", "minecraft:jungle_hanging_sign", "minecraft:dark_oak_hanging_sign", "minecraft:crimson_hanging_sign", "minecraft:warped_hanging_sign", "minecraft:mangrove_hanging_sign", "minecraft:bamboo_hanging_sign", "minecraft:oak_wall_hanging_sign", "minecraft:spruce_wall_hanging_sign", "minecraft:birch_wall_hanging_sign", "minecraft:acacia_wall_hanging_sign", "minecraft:jungle_wall_hanging_sign", "minecraft:dark_oak_wall_hanging_sign", "minecraft:mangrove_wall_hanging_sign", "minecraft:crimson_wall_hanging_sign", "minecraft:warped_wall_hanging_sign", "minecraft:bamboo_wall_hanging_sign", "minecraft:spruce_pressure_plate", "minecraft:birch_pressure_plate", "minecraft:jungle_pressure_plate", "minecraft:dark_oak_pressure_plate", "minecraft:mangrove_pressure_plate", "minecraft:bamboo_pressure_plate", "minecraft:jukebox", "minecraft:soul_soil", "minecraft:basalt", "minecraft:soul_torch", "minecraft:soul_wall_torch", "minecraft:glowstone", "minecraft:nether_portal", "minecraft:cake", "minecraft:white_stained_glass", "minecraft:orange_stained_glass", "minecraft:magenta_stained_glass", "minecraft:light_blue_stained_glass", "minecraft:yellow_stained_glass", "minecraft:lime_stained_glass", "minecraft:pink_stained_glass", "minecraft:gray_stained_glass", "minecraft:light_gray_stained_glass", "minecraft:cyan_stained_glass", "minecraft:purple_stained_glass", "minecraft:blue_stained_glass", "minecraft:brown_stained_glass", "minecraft:green_stained_glass", "minecraft:red_stained_glass", "minecraft:black_stained_glass", "minecraft:birch_trapdoor", "minecraft:acacia_trapdoor", "minecraft:mangrove_trapdoor", "minecraft:bamboo_trapdoor", "minecraft:packed_mud", "minecraft:mud_bricks", "minecraft:infested_cobblestone", "minecraft:infested_cracked_stone_bricks", "minecraft:attached_pumpkin_stem", "minecraft:attached_melon_stem", "minecraft:brick_stairs", "minecraft:mud_brick_stairs", "minecraft:mycelium", "minecraft:nether_bricks", "minecraft:nether_brick_fence", "minecraft:nether_brick_stairs", "minecraft:nether_wart", "minecraft:enchanting_table", "minecraft:cauldron", "minecraft:lava_cauldron", "minecraft:powder_snow_cauldron", "minecraft:end_portal", "minecraft:end_portal_frame", "minecraft:end_stone", "minecraft:dragon_egg", "minecraft:ender_chest", "minecraft:tripwire", "minecraft:emerald_block", "minecraft:command_block", "minecraft:beacon", "minecraft:mossy_cobblestone_wall", "minecraft:flower_pot", "minecraft:potted_oak_sapling", "minecraft:potted_spruce_sapling", "minecraft:potted_birch_sapling", "minecraft:potted_jungle_sapling", "minecraft:potted_acacia_sapling", "minecraft:potted_dark_oak_sapling", "minecraft:potted_mangrove_propagule", "minecraft:potted_fern", "minecraft:potted_poppy", "minecraft:potted_blue_orchid", "minecraft:potted_allium", "minecraft:potted_azure_bluet", "minecraft:potted_red_tulip", "minecraft:potted_orange_tulip", "minecraft:potted_white_tulip", "minecraft:potted_pink_tulip", "minecraft:potted_oxeye_daisy", "minecraft:potted_cornflower", "minecraft:potted_lily_of_the_valley", "minecraft:potted_wither_rose", "minecraft:potted_red_mushroom", "minecraft:potted_brown_mushroom", "minecraft:potted_dead_bush", "minecraft:oak_button", "minecraft:spruce_button", "minecraft:birch_button", "minecraft:jungle_button", "minecraft:acacia_button", "minecraft:dark_oak_button", "minecraft:mangrove_button", "minecraft:bamboo_button", "minecraft:skeleton_wall_skull", "minecraft:wither_skeleton_skull", "minecraft:wither_skeleton_wall_skull", "minecraft:zombie_head", "minecraft:zombie_wall_head", "minecraft:player_head", "minecraft:player_wall_head", "minecraft:creeper_head", "minecraft:creeper_wall_head", "minecraft:dragon_head", "minecraft:dragon_wall_head", "minecraft:piglin_head", "minecraft:piglin_wall_head", "minecraft:anvil", "minecraft:chipped_anvil", "minecraft:damaged_anvil", "minecraft:trapped_chest", "minecraft:light_weighted_pressure_plate", "minecraft:heavy_weighted_pressure_plate", "minecraft:daylight_detector", "minecraft:nether_quartz_ore", "minecraft:hopper", "minecraft:quartz_block", "minecraft:chiseled_quartz_block", "minecraft:quartz_pillar", "minecraft:quartz_stairs", "minecraft:activator_rail", "minecraft:dropper", "minecraft:magenta_terracotta", "minecraft:lime_terracotta", "minecraft:pink_terracotta", "minecraft:gray_terracotta", "minecraft:cyan_terracotta", "minecraft:purple_terracotta", "minecraft:blue_terracotta", "minecraft:green_terracotta", "minecraft:black_terracotta", "minecraft:orange_stained_glass_pane", "minecraft:magenta_stained_glass_pane", "minecraft:light_blue_stained_glass_pane", "minecraft:lime_stained_glass_pane", "minecraft:pink_stained_glass_pane", "minecraft:gray_stained_glass_pane", "minecraft:light_gray_stained_glass_pane", "minecraft:cyan_stained_glass_pane", "minecraft:purple_stained_glass_pane", "minecraft:blue_stained_glass_pane", "minecraft:brown_stained_glass_pane", "minecraft:green_stained_glass_pane", "minecraft:red_stained_glass_pane", "minecraft:black_stained_glass_pane", "minecraft:mangrove_stairs", "minecraft:bamboo_stairs", "minecraft:bamboo_mosaic_stairs", "minecraft:slime_block", "minecraft:barrier", "minecraft:light", "minecraft:prismarine_stairs", "minecraft:prismarine_brick_stairs", "minecraft:dark_prismarine_stairs", "minecraft:prismarine_slab", "minecraft:prismarine_brick_slab", "minecraft:dark_prismarine_slab", "minecraft:orange_carpet", "minecraft:magenta_carpet", "minecraft:lime_carpet", "minecraft:pink_carpet", "minecraft:brown_carpet", "minecraft:black_carpet", "minecraft:coal_block", "minecraft:white_banner", "minecraft:orange_banner", "minecraft:magenta_banner", "minecraft:light_blue_banner", "minecraft:yellow_banner", "minecraft:lime_banner", "minecraft:pink_banner", "minecraft:gray_banner", "minecraft:light_gray_banner", "minecraft:cyan_banner", "minecraft:purple_banner", "minecraft:blue_banner", "minecraft:brown_banner", "minecraft:green_banner", "minecraft:red_banner", "minecraft:black_banner", "minecraft:orange_wall_banner", "minecraft:magenta_wall_banner", "minecraft:light_blue_wall_banner", "minecraft:yellow_wall_banner", "minecraft:lime_wall_banner", "minecraft:pink_wall_banner", "minecraft:gray_wall_banner", "minecraft:light_gray_wall_banner", "minecraft:cyan_wall_banner", "minecraft:purple_wall_banner", "minecraft:blue_wall_banner", "minecraft:green_wall_banner", "minecraft:red_wall_banner", "minecraft:black_wall_banner", "minecraft:chiseled_red_sandstone", "minecraft:cut_red_sandstone", "minecraft:red_sandstone_stairs", "minecraft:mangrove_slab", "minecraft:bamboo_slab", "minecraft:bamboo_mosaic_slab", "minecraft:sandstone_slab", "minecraft:cut_sandstone_slab", "minecraft:petrified_oak_slab", "minecraft:brick_slab", "minecraft:mud_brick_slab", "minecraft:nether_brick_slab", "minecraft:quartz_slab", "minecraft:red_sandstone_slab", "minecraft:cut_red_sandstone_slab", "minecraft:purpur_slab", "minecraft:smooth_quartz", "minecraft:smooth_red_sandstone", "minecraft:birch_fence_gate", "minecraft:jungle_fence_gate", "minecraft:dark_oak_fence_gate", "minecraft:mangrove_fence_gate", "minecraft:bamboo_fence_gate", "minecraft:mangrove_fence", "minecraft:bamboo_fence", "minecraft:birch_door", "minecraft:mangrove_door", "minecraft:bamboo_door", "minecraft:end_rod", "minecraft:chorus_plant", "minecraft:chorus_flower", "minecraft:purpur_block", "minecraft:purpur_pillar", "minecraft:purpur_stairs", "minecraft:end_stone_bricks", "minecraft:end_gateway", "minecraft:repeating_command_block", "minecraft:chain_command_block", "minecraft:frosted_ice", "minecraft:nether_wart_block", "minecraft:red_nether_bricks", "minecraft:structure_void", "minecraft:observer", "minecraft:shulker_box", "minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:light_gray_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box", "minecraft:white_glazed_terracotta", "minecraft:orange_glazed_terracotta", "minecraft:magenta_glazed_terracotta", "minecraft:light_blue_glazed_terracotta", "minecraft:lime_glazed_terracotta", "minecraft:pink_glazed_terracotta", "minecraft:gray_glazed_terracotta", "minecraft:light_gray_glazed_terracotta", "minecraft:cyan_glazed_terracotta", "minecraft:blue_glazed_terracotta", "minecraft:brown_glazed_terracotta", "minecraft:green_glazed_terracotta", "minecraft:red_glazed_terracotta", "minecraft:black_glazed_terracotta", "minecraft:white_concrete", "minecraft:orange_concrete", "minecraft:magenta_concrete", "minecraft:light_blue_concrete", "minecraft:yellow_concrete", "minecraft:lime_concrete", "minecraft:pink_concrete", "minecraft:gray_concrete", "minecraft:light_gray_concrete", "minecraft:cyan_concrete", "minecraft:purple_concrete", "minecraft:blue_concrete", "minecraft:brown_concrete", "minecraft:green_concrete", "minecraft:red_concrete", "minecraft:black_concrete", "minecraft:white_concrete_powder", "minecraft:orange_concrete_powder", "minecraft:magenta_concrete_powder", "minecraft:light_blue_concrete_powder", "minecraft:yellow_concrete_powder", "minecraft:lime_concrete_powder", "minecraft:pink_concrete_powder", "minecraft:gray_concrete_powder", "minecraft:light_gray_concrete_powder", "minecraft:cyan_concrete_powder", "minecraft:purple_concrete_powder", "minecraft:blue_concrete_powder", "minecraft:brown_concrete_powder", "minecraft:green_concrete_powder", "minecraft:red_concrete_powder", "minecraft:black_concrete_powder", "minecraft:dried_kelp_block", "minecraft:turtle_egg", "minecraft:dead_tube_coral_block", "minecraft:dead_brain_coral_block", "minecraft:dead_bubble_coral_block", "minecraft:dead_fire_coral_block", "minecraft:dead_horn_coral_block", "minecraft:dead_tube_coral", "minecraft:dead_brain_coral", "minecraft:dead_bubble_coral", "minecraft:dead_fire_coral", "minecraft:dead_horn_coral", "minecraft:dead_tube_coral_fan", "minecraft:dead_brain_coral_fan", "minecraft:dead_bubble_coral_fan", "minecraft:dead_fire_coral_fan", "minecraft:dead_horn_coral_fan", "minecraft:dead_tube_coral_wall_fan", "minecraft:dead_brain_coral_wall_fan", "minecraft:dead_bubble_coral_wall_fan", "minecraft:dead_fire_coral_wall_fan", "minecraft:dead_horn_coral_wall_fan", "minecraft:conduit", "minecraft:bamboo_sapling", "minecraft:potted_bamboo", "minecraft:void_air", "minecraft:polished_granite_stairs", "minecraft:smooth_red_sandstone_stairs", "minecraft:polished_diorite_stairs", "minecraft:end_stone_brick_stairs", "minecraft:stone_stairs", "minecraft:smooth_sandstone_stairs", "minecraft:smooth_quartz_stairs", "minecraft:granite_stairs", "minecraft:andesite_stairs", "minecraft:red_nether_brick_stairs", "minecraft:polished_andesite_stairs", "minecraft:polished_granite_slab", "minecraft:smooth_red_sandstone_slab", "minecraft:polished_diorite_slab", "minecraft:mossy_cobblestone_slab", "minecraft:end_stone_brick_slab", "minecraft:smooth_sandstone_slab", "minecraft:smooth_quartz_slab", "minecraft:granite_slab", "minecraft:andesite_slab", "minecraft:red_nether_brick_slab", "minecraft:polished_andesite_slab", "minecraft:diorite_slab", "minecraft:brick_wall", "minecraft:prismarine_wall", "minecraft:red_sandstone_wall", "minecraft:granite_wall", "minecraft:mud_brick_wall", "minecraft:nether_brick_wall", "minecraft:andesite_wall", "minecraft:red_nether_brick_wall", "minecraft:sandstone_wall", "minecraft:end_stone_brick_wall", "minecraft:scaffolding", "minecraft:soul_campfire", "minecraft:warped_stem", "minecraft:stripped_warped_stem", "minecraft:warped_hyphae", "minecraft:stripped_warped_hyphae", "minecraft:warped_nylium", "minecraft:warped_fungus", "minecraft:warped_wart_block", "minecraft:warped_roots", "minecraft:nether_sprouts", "minecraft:crimson_stem", "minecraft:stripped_crimson_stem", "minecraft:crimson_hyphae", "minecraft:stripped_crimson_hyphae", "minecraft:crimson_nylium", "minecraft:crimson_fungus", "minecraft:shroomlight", "minecraft:weeping_vines", "minecraft:weeping_vines_plant", "minecraft:twisting_vines", "minecraft:twisting_vines_plant", "minecraft:crimson_roots", "minecraft:crimson_planks", "minecraft:warped_planks", "minecraft:crimson_slab", "minecraft:warped_slab", "minecraft:crimson_pressure_plate", "minecraft:warped_pressure_plate", "minecraft:crimson_fence", "minecraft:warped_fence", "minecraft:crimson_trapdoor", "minecraft:warped_trapdoor", "minecraft:crimson_fence_gate", "minecraft:warped_fence_gate", "minecraft:crimson_stairs", "minecraft:warped_stairs", "minecraft:crimson_button", "minecraft:warped_button", "minecraft:crimson_door", "minecraft:warped_door", "minecraft:crimson_sign", "minecraft:warped_sign", "minecraft:crimson_wall_sign", "minecraft:warped_wall_sign", "minecraft:structure_block", "minecraft:jigsaw", "minecraft:beehive", "minecraft:honey_block", "minecraft:honeycomb_block", "minecraft:netherite_block", "minecraft:ancient_debris", "minecraft:respawn_anchor", "minecraft:potted_crimson_fungus", "minecraft:potted_warped_fungus", "minecraft:potted_crimson_roots", "minecraft:potted_warped_roots", "minecraft:lodestone", "minecraft:blackstone", "minecraft:blackstone_stairs", "minecraft:blackstone_wall", "minecraft:blackstone_slab", "minecraft:polished_blackstone", "minecraft:polished_blackstone_bricks", "minecraft:cracked_polished_blackstone_bricks", "minecraft:chiseled_polished_blackstone", "minecraft:polished_blackstone_brick_slab", "minecraft:polished_blackstone_brick_stairs", "minecraft:polished_blackstone_brick_wall", "minecraft:gilded_blackstone", "minecraft:polished_blackstone_stairs", "minecraft:polished_blackstone_slab", "minecraft:polished_blackstone_pressure_plate", "minecraft:polished_blackstone_button", "minecraft:polished_blackstone_wall", "minecraft:chiseled_nether_bricks", "minecraft:cracked_nether_bricks", "minecraft:quartz_bricks", "minecraft:orange_candle", "minecraft:magenta_candle", "minecraft:light_blue_candle", "minecraft:yellow_candle", "minecraft:lime_candle", "minecraft:pink_candle", "minecraft:gray_candle", "minecraft:light_gray_candle", "minecraft:cyan_candle", "minecraft:purple_candle", "minecraft:blue_candle", "minecraft:brown_candle", "minecraft:green_candle", "minecraft:red_candle", "minecraft:black_candle", "minecraft:candle_cake", "minecraft:white_candle_cake", "minecraft:orange_candle_cake", "minecraft:magenta_candle_cake", "minecraft:light_blue_candle_cake", "minecraft:yellow_candle_cake", "minecraft:lime_candle_cake", "minecraft:pink_candle_cake", "minecraft:gray_candle_cake", "minecraft:light_gray_candle_cake", "minecraft:cyan_candle_cake", "minecraft:purple_candle_cake", "minecraft:blue_candle_cake", "minecraft:brown_candle_cake", "minecraft:green_candle_cake", "minecraft:red_candle_cake", "minecraft:black_candle_cake", "minecraft:tinted_glass", "minecraft:oxidized_copper", "minecraft:weathered_copper", "minecraft:exposed_copper", "minecraft:copper_block", "minecraft:oxidized_cut_copper", "minecraft:weathered_cut_copper", "minecraft:exposed_cut_copper", "minecraft:cut_copper", "minecraft:oxidized_cut_copper_stairs", "minecraft:weathered_cut_copper_stairs", "minecraft:exposed_cut_copper_stairs", "minecraft:cut_copper_stairs", "minecraft:oxidized_cut_copper_slab", "minecraft:weathered_cut_copper_slab", "minecraft:exposed_cut_copper_slab", "minecraft:cut_copper_slab", "minecraft:waxed_copper_block", "minecraft:waxed_weathered_copper", "minecraft:waxed_exposed_copper", "minecraft:waxed_oxidized_copper", "minecraft:waxed_oxidized_cut_copper", "minecraft:waxed_weathered_cut_copper", "minecraft:waxed_exposed_cut_copper", "minecraft:waxed_cut_copper", "minecraft:waxed_oxidized_cut_copper_stairs", "minecraft:waxed_weathered_cut_copper_stairs", "minecraft:waxed_exposed_cut_copper_stairs", "minecraft:waxed_cut_copper_stairs", "minecraft:waxed_oxidized_cut_copper_slab", "minecraft:waxed_weathered_cut_copper_slab", "minecraft:waxed_exposed_cut_copper_slab", "minecraft:waxed_cut_copper_slab", "minecraft:lightning_rod", "minecraft:raw_gold_block", "minecraft:potted_azalea_bush", "minecraft:potted_flowering_azalea_bush", "minecraft:ochre_froglight", "minecraft:verdant_froglight", "minecraft:pearlescent_froglight", "minecraft:frogspawn"))
        .visible(blockCheck::get)
        .build()
    );

    private final Setting<List<Block>> netherBlocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("nether-blocks")
        .description("Blocks that cannot generate naturally in the Nether.")
        .defaultValue(compileList())
        .visible(blockCheck::get)
        .build()
    );

    private final Setting<List<Block>> endBlocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("end-blocks")
        .description("Blocks that cannot generate naturally in the End.")
        .defaultValue(compileList())
        .visible(blockCheck::get)
        .build()
    );

    private final Setting<Boolean> remove = sgGeneral.add(new BoolSetting.Builder()
        .name("remove")
        .description("Remove chunk rendering when out of range.")
        .defaultValue(true)
        .build()
    );

    // Render

    private final Setting<Integer> renderHeight = sgRender.add(new IntSetting.Builder()
        .name("render-height")
        .description("The height at which the chunks will be rendered")
        .defaultValue(0)
        .min(-64)
        .sliderRange(-64, 319)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("Color of the chunks that have been loaded before.")
        .defaultValue(new SettingColor(255, 0, 0, 255))
        .visible(() -> shapeMode.get() == ShapeMode.Lines || shapeMode.get() == ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("Color of the chunks that have been loaded before.")
        .defaultValue(new SettingColor(255, 0, 0, 75))
        .visible(() -> shapeMode.get() == ShapeMode.Lines || shapeMode.get() == ShapeMode.Both)
        .build()
    );

    private final List<ChunkPos> queue = Collections.synchronizedList(new ArrayList<>());
    private final List<ChunkPos> oldChunks = new ArrayList<>();

    private OldChunks() {
        super(Tokyo.CATEGORY, "old-chunks", "Assumes vanilla world generation.");

        generateList.set(false);
    }

    private void regenerateList() {
        resetList.set(false);
        if (resetList.get() && generateList.get() && Utils.canUpdate()) {
            List<Block> list = getDimensionSpecificList();
            list.clear();
            for (var block : Registries.BLOCK) list.add(block);
        }
    }

    @Override
    public void onActivate() {
        regenerateList();
    }

    @Override
    public void onDeactivate() {
        if (generateList.get()) {
            List<Block> list = getDimensionSpecificList();
            if (!list.isEmpty()) Tokyo.LOG.info(list.stream().map(block -> "\"" + Registries.BLOCK.getId(block) + "\"").collect(Collectors.joining(", ")));
        }
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();
        WButton reset = list.add(theme.button("Reset Chunks")).expandX().widget();
        reset.action = oldChunks::clear;
        WButton export = list.add(theme.button("Export as .CSV")).expandX().widget();
        export.action = () -> {
            if (oldChunks.isEmpty()) return;

            Path exportLocation = ExportUtils.computePath(ExportUtils.WORLDNAME.get() + "/" + "old_chunks_" + ExportUtils.DATETIME.get(), ".csv", ExportUtils.Mode.KEEP);
            FileUtils.ensureDirectoryExists(exportLocation);
            try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(exportLocation))) {
                writer.writeNext("X", "Z");
                for (var chunk : oldChunks) {
                    writer.writeNext(chunk.x, chunk.z);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        return table;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (remove.get()) oldChunks.removeIf(chunk -> !isChunkInRange(chunk));

        synchronized (queue) {
            for (var chunkPos : queue) {
                if (!oldChunks.contains(chunkPos) && (!remove.get() || isChunkInRange(chunkPos))) {
                    oldChunks.add(chunkPos);
                }
            }
            queue.clear();
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (lineColor.get().a <= 5 && sideColor.get().a <= 5) return;

        for (var chunk : oldChunks) {
            event.renderer.box(
                chunk.getStartX(), renderHeight.get(), chunk.getStartZ(),
                chunk.getStartX() + 16, renderHeight.get(), chunk.getStartZ() + 16,
                sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof ChunkDeltaUpdateS2CPacket packet) {
            short[] positions = IChunkDeltaUpdateS2CPacket.getPosition(packet);
            BlockState[] blockStates = IChunkDeltaUpdateS2CPacket.getBlockStates(packet);
            for (int i = 0; i < positions.length; i++) {
                short s = positions[i];
                if (check(blockStates[i])) {
                    ChunkSectionPos sectionPos = IChunkDeltaUpdateS2CPacket.getSectionPos(packet);
                    int x = ChunkSectionPos.getSectionCoord(sectionPos.unpackBlockX(s));
                    int z = ChunkSectionPos.getSectionCoord(sectionPos.unpackBlockZ(s));
                    queue.add(new ChunkPos(x, z));
                    return;
                }
            }
        } else if (event.packet instanceof BlockUpdateS2CPacket packet) {
            check(packet.getPos(), packet.getState());
        }
    }

    @ApiStatus.Internal
    public static void onChunkDataPacketReceive(WorldChunk chunk) {
        if (MinecraftClient.getInstance().world == null) return;

        MeteorExecutor.execute(() -> {
            if (!INSTANCE.check(chunk)) return;

            INSTANCE.queue.add(chunk.getPos());
        });
    }

    private boolean check(WorldChunk chunk) {
        for (ChunkSection section : chunk.getSectionArray()) {
            PaletteStorage storage = IPalettedContainer.getStorage(section.getBlockStateContainer());
            if (storage instanceof EmptyPaletteStorage) continue;
            Palette<BlockState> palette = IPalettedContainer.getPalette(section.getBlockStateContainer());
            int i = 0;
            sectionLabel:
            for (long data : storage.getData()) {
                for (int j = 0; j < IPackedIntegerArray.getElementsPerLong(storage); j++) {
                    BlockState state = palette.get((int)(data & IPackedIntegerArray.getMaxValue(storage)));
                    if (check(state)) {
                        Tokyo.LOG.info("Found block: " + Registries.BLOCK.getId(state.getBlock()));
                        if (generateList.get()) getDimensionSpecificList().remove(state.getBlock());
                        return true;
                    }
                    data >>= storage.getElementBits();
                    if (++i < storage.getSize()) continue;
                    break sectionLabel;
                }
            }
        }

        return false;
    }

    private void check(BlockPos pos, BlockState state) {
        if (check(state)) {
            queue.add(new ChunkPos(pos));
        }
    }

    private boolean check(BlockState state) {
        if (blockCheck.get()) {
            if (getDimensionSpecificList().contains(state.getBlock())) return true;

            if (state.isOf(Blocks.SNOW) && state.get(SnowBlock.LAYERS) != 1) return true;
            if (state.isOf(Blocks.SWEET_BERRY_BUSH) && state.get(SweetBerryBushBlock.AGE) != 3) return true;
        }

        return false;
    }

    @Override
    public String getInfoString() {
        return String.valueOf(generateList.get() ? overworldBlocks.get().size() : oldChunks.size());
    }

    private List<Block> getDimensionSpecificList() {
        if (mc.world == null) return Collections.emptyList();
        return switch (PlayerUtils.getDimension()) {
            case Overworld -> overworldBlocks.get();
            case Nether -> netherBlocks.get();
            case End -> endBlocks.get();
        };
    }

    private static List<Block> compileList(String... identifiers) {
        List<Block> list = new ArrayList<>();
        for (var str : identifiers) {
            Identifier id = Identifier.tryParse(str);
            list.add(Registries.BLOCK.get(id));
        }
        return list;
    }

    private boolean isChunkInRange(ChunkPos chunkPos) {
        double x = mc.cameraEntity.getX();
        double z = mc.cameraEntity.getZ();
        double dx = x - chunkPos.getStartX();
        double dz = z - chunkPos.getStartZ();

        return Math.fma(dx, dx, dz * dz) <= 1024 * 1024;
    }
}
