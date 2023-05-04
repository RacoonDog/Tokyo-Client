package io.github.racoondog.tokyo.systems.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.racoondog.tokyo.systems.hud.ImageHud;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.hud.Hud;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;

import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Cummand extends Command {
    public Cummand() {
        super("cummand", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("num", IntegerArgumentType.integer(0)).executes(ctx -> {
            int count = IntegerArgumentType.getInteger(ctx, "num");
            Random r = new Random();
            for (int i = 0; i < count; i++) {
                Hud.get().add(ImageHud.INFO.addPreset(
                    ":)))",
                    element -> {
                        ((Setting<ImageHud.Preset>) element.settings.get("preset")).set(ImageHud.Preset.LiveLeak);
                        ((Setting<Double>) element.settings.get("scale")).set(r.nextDouble(0.5, 2.5));
                    }),
                    r.nextInt(0, 1920),
                    r.nextInt(0, 1017)
                );
            }
            return 1;
        }));

        builder.executes(ctx -> {
            BakedModel model = mc.getBakedModelManager().getBlockModels().getModel(Blocks.OBSIDIAN.getDefaultState());
            System.out.println(model.getClass().getSimpleName());
            System.out.println(model.toString());
            for (var quad : model.getQuads(Blocks.OBSIDIAN.getDefaultState(), null, new Xoroshiro128PlusPlusRandom(2))) {
                System.out.println(quad.toString());
                System.out.println(quad.getSprite().toString());
            }
            return 1;
        });
    }
}
