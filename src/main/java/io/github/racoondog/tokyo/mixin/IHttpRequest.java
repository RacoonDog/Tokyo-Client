package io.github.racoondog.tokyo.mixin;

import meteordevelopment.meteorclient.utils.network.Http;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.http.HttpRequest;

@Environment(EnvType.CLIENT)
@Mixin(Http.Request.class)
public interface IHttpRequest {
    @Accessor("builder")
    HttpRequest.Builder tokyo$getBuilder();
}
