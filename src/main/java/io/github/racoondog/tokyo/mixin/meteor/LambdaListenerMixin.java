package io.github.racoondog.tokyo.mixin.meteor;

import io.github.racoondog.tokyo.systems.config.TokyoConfig;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.listeners.LambdaListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Set;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
@Mixin(value = LambdaListener.class, remap = false)
public abstract class LambdaListenerMixin {
    @Shadow @Final private Class<?> target;

    @Unique private static final Set<Class<?>> PROFILED_EVENTS = new ReferenceOpenHashSet<>(Set.of(TickEvent.Pre.class, TickEvent.Post.class, Render2DEvent.class, Render3DEvent.class));
    @Unique @Nullable private String identifier;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void generateInfo(LambdaListener.Factory factory, Class<?> klass, Object object, Method method, CallbackInfo ci) {
        if (PROFILED_EVENTS.contains(target)) identifier = MeteorClient.MOD_ID + "_" + method.getDeclaringClass().getSimpleName() + "_" + method.getName();
    }

    @Inject(method = "call", at = @At("HEAD"))
    private void onHead(Object event, CallbackInfo ci) {
        if (TokyoConfig.INSTANCE.eventDebug.get() && identifier != null) {
            mc.getProfiler().push(identifier);
        }
    }

    @Inject(method = "call", at = @At("TAIL"))
    private void onTail(Object event, CallbackInfo ci) {
        if (TokyoConfig.INSTANCE.eventDebug.get() && identifier != null) {
            mc.getProfiler().pop();
        }
    }
}
