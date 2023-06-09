package io.github.racoondog.tokyo.utils.c2c.packets;

import io.github.racoondog.tokyo.mixin.IArrayPalette;
import io.github.racoondog.tokyo.mixin.IBiMapPalette;
import io.github.racoondog.tokyo.mixin.IPalettedContainer;
import io.github.racoondog.tokyo.utils.c2c.C2CPacket;
import io.github.racoondog.tokyo.utils.c2c.C2CPacketListener;
import io.github.racoondog.tokyo.utils.misc.MathUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.*;

@Environment(EnvType.CLIENT)
public class ChunkC2CPacket implements C2CPacket {
    public final int chunkX;
    public final int chunkZ;
    public final SerializableChunkData chunkData;

    public ChunkC2CPacket(PacketByteBuf buf) {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.chunkData = new SerializableChunkData(buf);
    }

    public ChunkC2CPacket(WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.chunkData = new SerializableChunkData(chunk);
    }

    public static ChunkC2CPacket of(ChunkPos chunkPos) {
        return new ChunkC2CPacket(MinecraftClient.getInstance().world.getChunk(chunkPos.x, chunkPos.z));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        chunkData.write(buf);
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onChunkC2CPacket(this);
    }

    public void debugOutput() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        write(buf);
        int totalSize = buf.getWrittenBytes().length;
        System.out.printf("Debugging ChunkC2CPacket %s, %s (%s bytes)%n", chunkX, chunkZ, totalSize);

        PacketByteBuf chunkDataBuf = new PacketByteBuf(Unpooled.buffer());
        chunkData.write(chunkDataBuf);
        int chunkDataSize = chunkDataBuf.getWrittenBytes().length;
        System.out.printf("Chunk data (%s bytes): {%n", chunkDataSize);

        System.out.printf("- Section data (%s bytes)%n", chunkData.sectionsData.length);
        System.out.printf("- Heightmap (%s bytes)%n", chunkDataSize - chunkData.sectionsData.length);;
        System.out.printf("}%n");
    }

    public static class SerializableChunkData {
        public final Byte2ObjectMap<long[]> heightmap = new Byte2ObjectOpenHashMap<>();
        public final byte[] sectionsData;

        public SerializableChunkData(WorldChunk chunk) {
            for (var heightmapEntry : chunk.getHeightmaps()) {
                if (!heightmapEntry.getKey().shouldSendToClient()) continue;
                this.heightmap.put((byte) heightmapEntry.getKey().ordinal(), heightmapEntry.getValue().asLongArray());
            }

            this.sectionsData = new byte[getSectionsPacketSize(chunk)];
            writeSectionsData(new PacketByteBuf(this.getWritableSectionsDataBuf()), chunk);
        }

        public SerializableChunkData(PacketByteBuf buf) {
            int heightmapCount = buf.readByte();
            for (int i = 0; i < heightmapCount; i++) {
                byte enumOrdinal = buf.readByte();
                long[] data = buf.readLongArray();
                this.heightmap.put(enumOrdinal, data);
            }

            int sectionsDataLength = buf.readVarInt();
            if (sectionsDataLength > 0x200000) throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
            this.sectionsData = new byte[sectionsDataLength];
            buf.readBytes(this.sectionsData);
        }

        public void write(PacketByteBuf buf) {
            buf.writeByte(this.heightmap.size()); //write heightmap type count
            for (var heightmapEntry : this.heightmap.byte2ObjectEntrySet()) {
                buf.writeByte(heightmapEntry.getByteKey()); //write heightmap enum ordinal
                buf.writeLongArray(heightmapEntry.getValue()); //write heightmap data
            }

            buf.writeVarInt(this.sectionsData.length);
            buf.writeBytes(this.sectionsData);
        }

        public ByteBuf getWritableSectionsDataBuf() {
            ByteBuf byteBuf = Unpooled.wrappedBuffer(this.sectionsData);
            byteBuf.writerIndex(0);
            return byteBuf;
        }

        public PacketByteBuf getSectionsDataBuf() {
            return new PacketByteBuf(Unpooled.wrappedBuffer(this.sectionsData));
        }

        private static int getSectionsPacketSize(WorldChunk chunk) {
            int i = 0;
            for (var section : chunk.getSectionArray()) {
                i += section.getPacketSize();
            }
            return i;
        }

        public static void writeSectionsData(PacketByteBuf buf, WorldChunk chunk) {
            for (var section : chunk.getSectionArray()) {
                section.toPacket(buf);
            }
        }

        public static <T> void writePalettedContainer(PacketByteBuf buf, PalettedContainer<T> container) {
            PalettedContainer.Data<T> data = ((IPalettedContainer) container).tokyo$getData();
            byte bitsPerElement = (byte) data.storage().getElementBits();

            buf.writeByte(bitsPerElement);
            if (bitsPerElement == 0) return; //Empty section

            PaletteWriter.write(buf, data.palette());

            int blocksPerShardEdge = MathHelper.smallestEncompassingPowerOfTwo(MathHelper.floor(MathUtils.log(bitsPerElement, Long.SIZE)));
            if (blocksPerShardEdge <= 1) buf.writeLongArray(data.storage().getData());
            else {
                //morton packing
            }
        }

        public static abstract class PaletteWriter {
            private static final PaletteWriter BYTE = new PaletteWriter() {
                @Override
                public <T> void writeSize(PacketByteBuf buf, Palette<T> palette) {
                    buf.writeByte(palette.getSize());
                }
            };

            private static final PaletteWriter SHORT = new PaletteWriter() {
                @Override
                public <T> void writeSize(PacketByteBuf buf, Palette<T> palette) {
                    buf.writeShort(palette.getSize());
                }
            };

            private static final PaletteWriter DEFAULT = new PaletteWriter() {
                @Override
                public <T> void writePalette(PacketByteBuf buf, Palette<T> palette) {
                    palette.writePacket(buf);
                }
            };

            public static <T> void write(PacketByteBuf buf, Palette<T> palette) {
                getWriter(palette).writePalette(buf, palette);
            }

            private static PaletteWriter getWriter(Palette<?> palette) {
                if (palette instanceof IdListPalette<?> || palette instanceof SingularPalette<?> || palette.getSize() > Short.MAX_VALUE) return DEFAULT;
                if (palette.getSize() > Byte.MAX_VALUE) return SHORT;
                return BYTE;
            }

            protected <T> void writeSize(PacketByteBuf buf, Palette<T> palette) {}

            @SuppressWarnings("unchecked")
            public <T> void writePalette(PacketByteBuf buf, Palette<T> palette) {
                writeSize(buf, palette);
                if (palette instanceof ArrayPalette<T> arrayPalette) {
                    IArrayPalette<T> iArrayPalette = (IArrayPalette<T>) arrayPalette;
                    IdListWriter writer = IdListWriter.getWriter(iArrayPalette.tokyo$getIdList());
                    for (int i = 0; i < arrayPalette.getSize(); i++) {
                        writer.write(buf, iArrayPalette.tokyo$getIdList().getRawId(iArrayPalette.tokyo$getArray()[i]));
                    }
                } else if (palette instanceof BiMapPalette<T> biMapPalette) {
                    IBiMapPalette<T> iBiMapPalette = (IBiMapPalette<T>) biMapPalette;
                    IdListWriter writer = IdListWriter.getWriter(iBiMapPalette.tokyo$getIdList());
                    for (int i = 0; i < biMapPalette.getSize(); i++) {
                        writer.write(buf, iBiMapPalette.tokyo$getIdList().getRawId(iBiMapPalette.tokyo$getMap().get(i)));
                    }
                } else palette.writePacket(buf);
            }
        }

        public static abstract class IdListWriter {
            private static final IdListWriter BYTE = new IdListWriter() {
                @Override
                protected void write(PacketByteBuf buf, int value) {
                    buf.writeByte(value);
                }
            };

            private static final IdListWriter SHORT = new IdListWriter() {
                @Override
                protected void write(PacketByteBuf buf, int value) {
                    buf.writeShort(value);
                }
            };

            private static final IdListWriter DEFAULT = new IdListWriter() {
                @Override
                protected void write(PacketByteBuf buf, int value) {
                    buf.writeVarInt(value);
                }
            };

            protected abstract void write(PacketByteBuf buf, int value);

            public static IdListWriter getWriter(IndexedIterable<?> idList) {
                if (idList.size() > Short.MAX_VALUE) return DEFAULT;
                if (idList.size() > Byte.MAX_VALUE) return SHORT;
                return BYTE;
            }
        }
    }
}
