package io.github.racoondog.tokyo.utils.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public record ClientLookingPosArgument(double x, double y, double z) implements ClientPosArgument {
    public static final char CARET = '^';

    @Override
    public Vec3d toAbsolutePos(CommandSource source) {
        Vec2f rotation = mc.player.getRotationClient();
        Vec3d pos = EntityAnchorArgumentType.EntityAnchor.FEET.positionAt(mc.player);
        final float PiDividedBy180 = 0.017453292F;
        float f = MathHelper.cos((rotation.y + 90.0F) * PiDividedBy180);
        float g = MathHelper.sin((rotation.y + 90.0F) * PiDividedBy180);
        float h = MathHelper.cos(-rotation.x * PiDividedBy180);
        float i = MathHelper.sin(-rotation.x * PiDividedBy180);
        float j = MathHelper.cos((-rotation.x + 90.0F) * PiDividedBy180);
        float k = MathHelper.sin((-rotation.x + 90.0F) * PiDividedBy180);
        Vec3d vec3d2 = new Vec3d((f * h), i, (g * h));
        Vec3d vec3d3 = new Vec3d((f * j), k, (g * j));
        Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0D);
        double d = vec3d2.x * this.z + vec3d3.x * this.y + vec3d4.x * this.x;
        double e = vec3d2.y * this.z + vec3d3.y * this.y + vec3d4.y * this.x;
        double l = vec3d2.z * this.z + vec3d3.z * this.y + vec3d4.z * this.x;
        return new Vec3d(pos.x + d, pos.y + e, pos.z + l);
    }

    @Override
    public Vec2f toAbsoluteRotation(CommandSource source) {
        return Vec2f.ZERO;
    }

    @Override
    public boolean isXRelative() {
        return true;
    }

    @Override
    public boolean isYRelative() {
        return true;
    }

    @Override
    public boolean isZRelative() {
        return true;
    }

    public static ClientLookingPosArgument parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        double x = readCoordinate(reader, cursor);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            double y = readCoordinate(reader, cursor);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                double z = readCoordinate(reader, cursor);
                return new ClientLookingPosArgument(x, y, z);
            } else {
                reader.setCursor(cursor);
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
            }
        } else {
            reader.setCursor(cursor);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
        }
    }

    private static double readCoordinate(StringReader reader, int startingCursorPos) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw CoordinateArgument.MISSING_COORDINATE.createWithContext(reader);
        } else if (reader.peek() != CARET) {
            reader.setCursor(startingCursorPos);
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(reader);
        }
        reader.skip();
        return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ClientLookingPosArgument lookingPosArgument)) {
            return false;
        } else {
            return this.x == lookingPosArgument.x && this.y == lookingPosArgument.y && this.z == lookingPosArgument.z;
        }
    }
}
