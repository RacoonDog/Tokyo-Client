package io.github.racoondog.tokyo.utils.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public record ClientDefaultPosArgument(CoordinateArgument x, CoordinateArgument y, CoordinateArgument z) implements ClientPosArgument {
    @Override
    public Vec3d toAbsolutePos(CommandSource source) {
        Vec3d vec3d = mc.player.getPos();
        return new Vec3d(this.x.toAbsoluteCoordinate(vec3d.x), this.y.toAbsoluteCoordinate(vec3d.y), this.z.toAbsoluteCoordinate(vec3d.z));
    }

    @Override
    public Vec2f toAbsoluteRotation(CommandSource source) {
        Vec2f vec2f = mc.player.getRotationClient();
        return new Vec2f((float) this.x.toAbsoluteCoordinate(vec2f.x), (float) this.y.toAbsoluteCoordinate(vec2f.y));
    }

    @Override
    public boolean isXRelative() {
        return this.x.isRelative();
    }

    @Override
    public boolean isYRelative() {
        return this.y.isRelative();
    }

    @Override
    public boolean isZRelative() {
        return this.z.isRelative();
    }

    public static ClientDefaultPosArgument parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        CoordinateArgument coordinateArgument = CoordinateArgument.parse(reader);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(reader);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(reader);
                return new ClientDefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
            } else {
                reader.setCursor(cursor);
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
            }
        } else {
            reader.setCursor(cursor);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
        }
    }

    public static ClientDefaultPosArgument parse(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        CoordinateArgument coordinateArgument = CoordinateArgument.parse(reader, centerIntegers);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(reader, false);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(reader, centerIntegers);
                return new ClientDefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
            } else {
                reader.setCursor(cursor);
                throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
            }
        } else {
            reader.setCursor(cursor);
            throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
        }
    }

    public static ClientDefaultPosArgument absolute(double x, double y, double z) {
        return new ClientDefaultPosArgument(new CoordinateArgument(false, x), new CoordinateArgument(false, y), new CoordinateArgument(false, z));
    }

    public static ClientDefaultPosArgument absolute(Vec2f vec) {
        return new ClientDefaultPosArgument(new CoordinateArgument(false, vec.x), new CoordinateArgument(false, vec.y), new CoordinateArgument(true, 0.0D));
    }

    public static ClientDefaultPosArgument zero() {
        return new ClientDefaultPosArgument(new CoordinateArgument(true, 0.0D), new CoordinateArgument(true, 0.0D), new CoordinateArgument(true, 0.0D));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ClientDefaultPosArgument defaultPosArgument)) {
            return false;
        } else {
            if (this.x.equals(defaultPosArgument.x)) {
                return this.y.equals(defaultPosArgument.y) && this.z.equals(defaultPosArgument.z);
            }
            return false;
        }
    }
}
