package io.github.racoondog.tokyo.mixin;

import io.github.racoondog.tokyo.mixininterface.IRenderer2D;
import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(value = Renderer2D.class, remap = false)
public abstract class Renderer2DMixin implements IRenderer2D {
    @Shadow @Final public Mesh triangles;

    @Override
    public void tokyo$texQuadHFlip(double x, double y, double width, double height, Color color) {
        triangles.quad(
            triangles.vec2(x + width, y).vec2(0, 0).color(color).next(),
            triangles.vec2(x + width, y + height).vec2(0, 1).color(color).next(),
            triangles.vec2(x, y + height).vec2(1, 1).color(color).next(),
            triangles.vec2(x, y).vec2(1, 0).color(color).next()
        );
    }

    @Override
    public void tokyo$texQuadVFlip(double x, double y, double width, double height, Color color) {
        triangles.quad(
            triangles.vec2(x, y + height).vec2(0, 0).color(color).next(),
            triangles.vec2(x, y).vec2(0, 1).color(color).next(),
            triangles.vec2(x + width, y).vec2(1, 1).color(color).next(),
            triangles.vec2(x + width, y + height).vec2(1, 0).color(color).next()
        );
    }

    @Override
    public void tokyo$texQuadHVFlip(double x, double y, double width, double height, Color color) {
        triangles.quad(
            triangles.vec2(x + width, y + height).vec2(0, 0).color(color).next(),
            triangles.vec2(x + width, y).vec2(0, 1).color(color).next(),
            triangles.vec2(x, y).vec2(1, 1).color(color).next(),
            triangles.vec2(x, y + height).vec2(1, 0).color(color).next()
        );
    }
}
