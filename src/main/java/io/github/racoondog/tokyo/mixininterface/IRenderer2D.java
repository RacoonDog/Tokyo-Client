package io.github.racoondog.tokyo.mixininterface;

import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IRenderer2D {
    IRenderer2D COLOR = (IRenderer2D) Renderer2D.COLOR;
    IRenderer2D TEXTURE = (IRenderer2D) Renderer2D.TEXTURE;

    void texQuadHFlip(double x, double y, double width, double height, Color color);
    void texQuadVFlip(double x, double y, double width, double height, Color color);
    void texQuadHVFlip(double x, double y, double width, double height, Color color);
}
