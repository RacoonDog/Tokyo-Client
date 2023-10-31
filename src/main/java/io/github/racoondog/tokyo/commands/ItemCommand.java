package io.github.racoondog.tokyo.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class ItemCommand extends Command {
    /*
    private static final Dynamic3CommandExceptionType NOT_A_CONTAINER_TARGET_EXCEPTION = new Dynamic3CommandExceptionType(
        (x, y, z) -> Text.translatable("commands.item.target.not_a_container", x, y, z)
    );
    private static final SimpleCommandExceptionType NOT_IN_RANGE_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Target container is not in range."));
    private static final Dynamic3CommandExceptionType NOT_A_CONTAINER_SOURCE_EXCEPTION = new Dynamic3CommandExceptionType(
        (x, y, z) -> Text.translatable("commands.item.source.not_a_container", x, y, z)
    );
     */
    private static final DynamicCommandExceptionType NO_SUCH_SLOT_SOURCE_EXCEPTION = new DynamicCommandExceptionType(
        slot -> Text.translatable("commands.item.source.no_such_slot", slot)
    );
    private static final SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this."));

    public ItemCommand() {
        super("item", "Bypass /item op requirement with creative mode.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
            literal("replace").then(
                argument("slot", ItemSlotArgumentType.itemSlot()).then(
                    literal("with").then(
                        argument("item", ItemStackArgumentType.itemStack(REGISTRY_ACCESS))
                            .executes(ctx -> replace(ItemStackArgumentType.getItemStackArgument(ctx, "item").createStack(1, false), ctx.getArgument("slot", Integer.class)))
                            .then(
                                argument("count", IntegerArgumentType.integer(1, 64))
                                    .executes(ctx -> replace(ItemStackArgumentType.getItemStackArgument(ctx, "item").createStack(IntegerArgumentType.getInteger(ctx, "count"), false), ctx.getArgument("slot", Integer.class)))
                            )
                    )
                ).then(
                    literal("from")/*.then(
                        literal("block").then(
                            argument("source", BlockPosArgumentType.blockPos()).then(
                                argument("sourceSlot", ItemSlotArgumentType.itemSlot())
                                    .executes(ctx -> replaceFromBlock(ctx.getArgument("source", PosArgument.class).toAbsoluteBlockPos(mc.player.getCommandSource()), ctx.getArgument("sourceSlot", Integer.class), ctx.getArgument("slot", Integer.class)))
                            )
                        )
                    )*/.then(
                        literal("slot").then(
                            argument("sourceSlot", ItemSlotArgumentType.itemSlot())
                                .executes(ctx -> replaceFromSlot(ctx.getArgument("sourceSlot", Integer.class), ctx.getArgument("slot", Integer.class)))
                        )
                    )
                )
            )
        );
    }

    private int replace(ItemStack stack, int slot) throws CommandSyntaxException {
        give(stack, slot);
        return SINGLE_SUCCESS;
    }

    /*
    private int replaceFromBlock(BlockPos blockPos, int sourceSlot, int slot) throws CommandSyntaxException {
        if (!PlayerUtils.isWithinReach(blockPos.toCenterPos())) throw NOT_IN_RANGE_EXCEPTION.create();
        if (!(mc.world.getBlockEntity(blockPos) instanceof Inventory)) throw NOT_A_CONTAINER_TARGET_EXCEPTION.create(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return SINGLE_SUCCESS;
    }

     */
    private int replaceFromSlot(int sourceSlot, int slot) throws CommandSyntaxException {
        ItemStack stack;
        if (sourceSlot >= 200 && sourceSlot < 227) {
            stack = EChestMemory.ITEMS.get(sourceSlot - 200);
        } else {
            StackReference stackReference = mc.player.getStackReference(sourceSlot);
            if (stackReference == StackReference.EMPTY) throw NO_SUCH_SLOT_SOURCE_EXCEPTION.create(sourceSlot);
            stack = stackReference.get();
        }

        give(stack, slot);

        return SINGLE_SUCCESS;
    }

    private void give(ItemStack stack, int slot) throws CommandSyntaxException {
        if (!mc.player.getAbilities().creativeMode) throw NOT_IN_CREATIVE.create();
        mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(slot, stack));
    }
}
