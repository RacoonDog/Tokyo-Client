package io.github.racoondog.tokyo.systems.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.racoondog.tokyo.utils.commands.Vec3ArgumentType;
import meteordevelopment.meteorclient.commands.Command;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.Vec3d;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class LookAtCommand extends Command {
    public static final LookAtCommand INSTANCE = new LookAtCommand();

    private LookAtCommand() {
        super("lookAt", "Looks at the specified location");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(argument("target", Vec3ArgumentType.vec3(true)).executes(ctx -> lookAt(Vec3ArgumentType.getVec3(ctx, "target"))));
    }

    private int lookAt(Vec3d target) {
        mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target);

        return SINGLE_SUCCESS;
    }
}
