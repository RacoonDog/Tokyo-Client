package io.github.racoondog.tokyo.systems.modules;

import io.github.racoondog.tokyo.Tokyo;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

//todo find some way to fix the node height not updating
//todo probably via threading and iterating through blocks
@Environment(EnvType.CLIENT)
public class DragonNodeESP extends Module {
    public static final DragonNodeESP INSTANCE = new DragonNodeESP();
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Boolean> highlightNearest = sgGeneral.add(new BoolSetting.Builder()
        .name("highlight-nearest")
        .description("Highlight the nearest node to the dragon's current location.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> nodeShape = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("node-shape")
        .description("The shape of the nodes.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> nodeSideColor = sgGeneral.add(new ColorSetting.Builder()
        .name("node-side-color")
        .description("The side color of the nodes.")
        .defaultValue(new SettingColor(64, 64, 64, 55))
        .visible(() -> nodeShape.get().sides())
        .build()
    );

    private final Setting<SettingColor> nodeLineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("node-line-color")
        .description("The line color of the nodes.")
        .defaultValue(new SettingColor(64, 64, 64))
        .visible(() -> nodeShape.get().lines())
        .build()
    );

    private final Setting<SettingColor> nearestNodeSideColor = sgGeneral.add(new ColorSetting.Builder()
        .name("node-side-color")
        .description("The side color of the nodes.")
        .defaultValue(new SettingColor(255, 255, 0, 55))
        .visible(() -> highlightNearest.get() && nodeShape.get().sides())
        .build()
    );

    private final Setting<SettingColor> nearestNodeLineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("node-line-color")
        .description("The line color of the nodes.")
        .defaultValue(new SettingColor(255, 255, 0))
        .visible(() -> highlightNearest.get() && nodeShape.get().lines())
        .build()
    );

    private EnderDragonEntity entity;

    private DragonNodeESP() {
        super(Tokyo.CATEGORY, "dragon-node-esp", "View info about ender dragon pathing behaviour.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!Utils.canUpdate() || PlayerUtils.getDimension() != Dimension.End) return;

        if (entity == null) {
            for (var e : mc.world.getEntities()) {
                if (e instanceof EnderDragonEntity enderDragonEntity) {
                    entity = enderDragonEntity;
                    generateNodes();
                    break;
                }
            }
            if (entity == null) {
                reset();
                return; //No dragons found
            }
        }

        if (entity.isDead() || entity.getHealth() <= 0) {
            reset();
            return;
        }

        for (var node : PathNode.NODES) {
            node.lineColor = nodeLineColor.get();
            node.sideColor = nodeSideColor.get();
        }

        if (highlightNearest.get()) {
            PathNode nearestNode = PathNode.getNearest(entity.getX(), entity.getY(), entity.getZ());
            nearestNode.lineColor = nearestNodeLineColor.get();
            nearestNode.sideColor = nearestNodeSideColor.get();
        }
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        if (!Utils.canUpdate() || PlayerUtils.getDimension() != Dimension.End) return;

        PathNode.NODES.forEach(n -> event.renderer.box(n.x + 0.25, n.y + 0.25, n.z + 0.25, n.x + 0.75, n.y + 0.75, n.z + 0.75, n.sideColor, n.lineColor, nodeShape.get(), 0));
    }

    //todo these can be static
    private void generateNodes() {
        for (int i = 0; i < 24; ++i) {
            int m;
            int l;
            int k = i;
            if (i < 12) {
                l = MathHelper.floor(60.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)k)));
                m = MathHelper.floor(60.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)k)));
            } else if (i < 20) {
                l = MathHelper.floor(40.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)(k -= 12))));
                m = MathHelper.floor(40.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)k)));
            } else {
                l = MathHelper.floor(20.0f * MathHelper.cos(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)(k -= 20))));
                m = MathHelper.floor(20.0f * MathHelper.sin(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)k)));
            }

            PathNode.create(l, m);
        }
        PathNode.create(0, 68, 0);
    }

    private void reset() {
        entity = null;
        PathNode.clear();
    }

    private static class PathNode {
        private final int x, z;
        private int y = 73;

        private SettingColor sideColor = INSTANCE.nodeSideColor.get();
        private SettingColor lineColor = INSTANCE.nodeLineColor.get();

        private PathNode(int x, int z) {
            this.x = x;
            this.z = z;
        }

        private static void create(int x, int z) {
            PathNode node = new PathNode(x, z);
            LOCATION_NODE_MAP.put(pack(x, z), node);
            NODES.add(node);
        }

        private static void create(int x, int y, int z) {
            PathNode node = new PathNode(x, z);
            node.y = y;
            LOCATION_NODE_MAP.put(pack(x, z), node);
            NODES.add(node);
        }

        private static final Short2ObjectMap<PathNode> LOCATION_NODE_MAP = new Short2ObjectOpenHashMap<>(25);
        private static final List<PathNode> NODES = new ArrayList<>(25);

        private static short pack(int x, int z) {
            return (short) (((x + 60) << 7) | (z + 60));
        }

        private static PathNode fromPos(int x, int z) {
            return LOCATION_NODE_MAP.get(pack(x, z));
        }

        private static void clear() {
            LOCATION_NODE_MAP.clear();
            NODES.clear();
        }

        private double squaredDistance(double x, double y, double z) {
            return PlayerUtils.squaredDistance(this.x, this.y, this.z, x, y, z);
        }

        private static PathNode getNearest(double x, double y, double z) {
            double distance = 10000.0f;
            PathNode nearest = null;
            for (var node : NODES) {
                if (nearest == null) { nearest = node; continue; }
                double currentDistance;
                if ((currentDistance = node.squaredDistance(x, y, z)) >= distance) continue;
                nearest = node;
                distance = currentDistance;
            }
            return nearest;
        }
    }
}
