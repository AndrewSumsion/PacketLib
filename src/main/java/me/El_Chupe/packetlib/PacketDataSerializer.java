package me.El_Chupe.packetlib;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.util.ByteProcessor;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PacketDataSerializer extends ByteBuf {

    private final ByteBuf data;

    public PacketDataSerializer(ByteBuf bytebuf) {
        this.data = bytebuf;
    }

    public PacketDataSerializer() {
        data = ByteBufAllocator.DEFAULT.buffer();
    }

    public static int i(int i) {
        return (i & -128) == 0 ? 1 : ((i & -16384) == 0 ? 2 : ((i & -2097152) == 0 ? 3 : ((i & -268435456) == 0 ? 4 : 5)));
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        byte b0;

        do {
            b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public void writeVarInt(int i) {
        PacketDataSerializer.writeVarInt(this, i);
    }

    public static void writeVarInt(ByteBuf byteBuf, int value) {
        while ((value & -128) != 0) {
            byteBuf.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        byteBuf.writeByte(value);
    }

    /*
    public void data(NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            this.writeShort(-1);
        } else {
            byte[] abyte = NBTCompressedStreamTools.data(nbttagcompound);

            this.writeShort((short) abyte.length);
            this.writeBytes(abyte);
        }
    }

    public NBTTagCompound writeVarInt() {
        short short1 = this.readShort();

        if (short1 < 0) {
            return null;
        } else {
            byte[] abyte = new byte[short1];

            this.readBytes(abyte);
            return NBTCompressedStreamTools.data(abyte, new NBTReadLimiter(2097152L));
        }
    }
    */

    public void writeItemStack(ItemStack value) {
        if (value == null) {
            this.data.writeShort(-1);
            return;
        }
        this.data.writeShort(value.getType().getId());
        this.data.writeByte(value.getAmount());
        this.data.writeShort(value.getDurability());
        this.data.writeByte(0); // TODO: Should be writing an nms NBTTagCompound. 0 means that it's null
            /*if (itemstack == null) {
                this.writeShort(-1);
            } else {
                this.writeShort(Item.getId(itemstack.getItem()));
                this.writeByte(itemstack.count);
                this.writeShort(itemstack.getData());
                NBTTagCompound nbttagcompound = null;

                if (itemstack.getItem().usesDurability() || itemstack.getItem().s()) {
                    nbttagcompound = itemstack.tag;
                }

                this.data(nbttagcompound);
            }*/
    }

    /*
    public ItemStack c() {
        ItemStack itemstack = null;
        short short1 = this.readShort();

        if (short1 >= 0) {
            byte b0 = this.readByte();
            short short2 = this.readShort();

            itemstack = new ItemStack(Item.getById(short1), b0, short2);
            itemstack.tag = this.writeVarInt();
        }

        return itemstack;
    }
    */

    public String readString(int i) {
        int j = this.readVarInt();

        if (j > i * 4) {
            throw new RuntimeException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
        } else if (j < 0) {
            throw new RuntimeException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String s = new String(this.readBytes(j).array(), StandardCharsets.UTF_8);

            if (s.length() > i) {
                throw new RuntimeException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
            } else {
                return s;
            }
        }
    }

    public void writeString(String s) {
        byte[] abyte = s.getBytes(StandardCharsets.UTF_8);

        if (abyte.length > 32767) {
            throw new RuntimeException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            this.writeVarInt(abyte.length);
            this.writeBytes(abyte);
        }
    }

    public int capacity() {
        return this.data.capacity();
    }

    public ByteBuf capacity(int i) {
        return this.data.capacity(i);
    }

    public int maxCapacity() {
        return this.data.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.data.alloc();
    }

    public ByteOrder order() {
        return this.data.order();
    }

    public ByteBuf order(ByteOrder byteorder) {
        return this.data.order(byteorder);
    }

    public ByteBuf unwrap() {
        return this.data;
    }

    public boolean isDirect() {
        return this.data.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ByteBuf asReadOnly() {
        return null;
    }

    public int readerIndex() {
        return this.data.readerIndex();
    }

    public ByteBuf readerIndex(int i) {
        return this.data.readerIndex(i);
    }

    public int writerIndex() {
        return this.data.writerIndex();
    }

    public ByteBuf writerIndex(int i) {
        return this.data.writerIndex(i);
    }

    public ByteBuf setIndex(int i, int j) {
        return this.data.setIndex(i, j);
    }

    public int readableBytes() {
        return this.data.readableBytes();
    }

    public int writableBytes() {
        return this.data.writableBytes();
    }

    public int maxWritableBytes() {
        return this.data.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.data.isReadable();
    }

    public boolean isReadable(int i) {
        return this.data.isReadable(i);
    }

    public boolean isWritable() {
        return this.data.isWritable();
    }

    public boolean isWritable(int i) {
        return this.data.isWritable(i);
    }

    public ByteBuf clear() {
        return this.data.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.data.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.data.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.data.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.data.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.data.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.data.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int i) {
        return this.data.ensureWritable(i);
    }

    public int ensureWritable(int i, boolean flag) {
        return this.data.ensureWritable(i, flag);
    }

    public boolean getBoolean(int i) {
        return this.data.getBoolean(i);
    }

    public byte getByte(int i) {
        return this.data.getByte(i);
    }

    public short getUnsignedByte(int i) {
        return this.data.getUnsignedByte(i);
    }

    public short getShort(int i) {
        return this.data.getShort(i);
    }

    @Override
    public short getShortLE(int i) {
        return 0;
    }

    public int getUnsignedShort(int i) {
        return this.data.getUnsignedShort(i);
    }

    @Override
    public int getUnsignedShortLE(int i) {
        return 0;
    }

    public int getMedium(int i) {
        return this.data.getMedium(i);
    }

    @Override
    public int getMediumLE(int i) {
        return 0;
    }

    public int getUnsignedMedium(int i) {
        return this.data.getUnsignedMedium(i);
    }

    @Override
    public int getUnsignedMediumLE(int i) {
        return 0;
    }

    public int getInt(int i) {
        return this.data.getInt(i);
    }

    @Override
    public int getIntLE(int i) {
        return 0;
    }

    public long getUnsignedInt(int i) {
        return this.data.getUnsignedInt(i);
    }

    @Override
    public long getUnsignedIntLE(int i) {
        return 0;
    }

    public long getLong(int i) {
        return this.data.getLong(i);
    }

    @Override
    public long getLongLE(int i) {
        return 0;
    }

    public char getChar(int i) {
        return this.data.getChar(i);
    }

    public float getFloat(int i) {
        return this.data.getFloat(i);
    }

    public double getDouble(int i) {
        return this.data.getDouble(i);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        return this.data.getBytes(i, bytebuf);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        return this.data.getBytes(i, bytebuf, j);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.data.getBytes(i, bytebuf, j, k);
    }

    public ByteBuf getBytes(int i, byte[] abyte) {
        return this.data.getBytes(i, abyte);
    }

    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        return this.data.getBytes(i, abyte, j, k);
    }

    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        return this.data.getBytes(i, bytebuffer);
    }

    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        return this.data.getBytes(i, outputstream, j);
    }

    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.data.getBytes(i, gatheringbytechannel, j);
    }

    @Override
    public int getBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
        return 0;
    }

    @Override
    public CharSequence getCharSequence(int i, int i1, Charset charset) {
        return null;
    }

    public ByteBuf setBoolean(int i, boolean flag) {
        return this.data.setBoolean(i, flag);
    }

    public ByteBuf setByte(int i, int j) {
        return this.data.setByte(i, j);
    }

    public ByteBuf setShort(int i, int j) {
        return this.data.setShort(i, j);
    }

    @Override
    public ByteBuf setShortLE(int i, int i1) {
        return null;
    }

    public ByteBuf setMedium(int i, int j) {
        return this.data.setMedium(i, j);
    }

    @Override
    public ByteBuf setMediumLE(int i, int i1) {
        return null;
    }

    public ByteBuf setInt(int i, int j) {
        return this.data.setInt(i, j);
    }

    @Override
    public ByteBuf setIntLE(int i, int i1) {
        return null;
    }

    public ByteBuf setLong(int i, long j) {
        return this.data.setLong(i, j);
    }

    @Override
    public ByteBuf setLongLE(int i, long l) {
        return null;
    }

    public ByteBuf setChar(int i, int j) {
        return this.data.setChar(i, j);
    }

    public ByteBuf setFloat(int i, float f) {
        return this.data.setFloat(i, f);
    }

    public ByteBuf setDouble(int i, double d0) {
        return this.data.setDouble(i, d0);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        return this.data.setBytes(i, bytebuf);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        return this.data.setBytes(i, bytebuf, j);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.data.setBytes(i, bytebuf, j, k);
    }

    public ByteBuf setBytes(int i, byte[] abyte) {
        return this.data.setBytes(i, abyte);
    }

    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        return this.data.setBytes(i, abyte, j, k);
    }

    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        return this.data.setBytes(i, bytebuffer);
    }

    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        return this.data.setBytes(i, inputstream, j);
    }

    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        return this.data.setBytes(i, scatteringbytechannel, j);
    }

    @Override
    public int setBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
        return 0;
    }

    public ByteBuf setZero(int i, int j) {
        return this.data.setZero(i, j);
    }

    @Override
    public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
        return 0;
    }

    public boolean readBoolean() {
        return this.data.readBoolean();
    }

    public byte readByte() {
        return this.data.readByte();
    }

    public short readUnsignedByte() {
        return this.data.readUnsignedByte();
    }

    public short readShort() {
        return this.data.readShort();
    }

    @Override
    public short readShortLE() {
        return 0;
    }

    public int readUnsignedShort() {
        return this.data.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return 0;
    }

    public int readMedium() {
        return this.data.readMedium();
    }

    @Override
    public int readMediumLE() {
        return 0;
    }

    public int readUnsignedMedium() {
        return this.data.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.data.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.data.readInt();
    }

    @Override
    public int readIntLE() {
        return this.data.readIntLE();
    }

    public long readUnsignedInt() {
        return this.data.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return 0;
    }

    public long readLong() {
        return this.data.readLong();
    }

    @Override
    public long readLongLE() {
        return 0;
    }

    public char readChar() {
        return this.data.readChar();
    }

    public float readFloat() {
        return this.data.readFloat();
    }

    public double readDouble() {
        return this.data.readDouble();
    }

    public ByteBuf readBytes(int i) {
        return this.data.readBytes(i);
    }

    public ByteBuf readSlice(int i) {
        return this.data.readSlice(i);
    }

    @Override
    public ByteBuf readRetainedSlice(int i) {
        return null;
    }

    public ByteBuf readBytes(ByteBuf bytebuf) {
        return this.data.readBytes(bytebuf);
    }

    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        return this.data.readBytes(bytebuf, i);
    }

    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        return this.data.readBytes(bytebuf, i, j);
    }

    public ByteBuf readBytes(byte[] abyte) {
        return this.data.readBytes(abyte);
    }

    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        return this.data.readBytes(abyte, i, j);
    }

    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        return this.data.readBytes(bytebuffer);
    }

    public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        return this.data.readBytes(outputstream, i);
    }

    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        return this.data.readBytes(gatheringbytechannel, i);
    }

    @Override
    public CharSequence readCharSequence(int i, Charset charset) {
        return null;
    }

    @Override
    public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return 0;
    }

    public ByteBuf skipBytes(int i) {
        return this.data.skipBytes(i);
    }

    public ByteBuf writeBoolean(boolean flag) {
        return this.data.writeBoolean(flag);
    }

    public ByteBuf writeByte(int i) {
        return this.data.writeByte(i);
    }

    public ByteBuf writeShort(int i) {
        return this.data.writeShort(i);
    }

    @Override
    public ByteBuf writeShortLE(int i) {
        return null;
    }

    public ByteBuf writeMedium(int i) {
        return this.data.writeMedium(i);
    }

    @Override
    public ByteBuf writeMediumLE(int i) {
        return null;
    }

    public ByteBuf writeInt(int i) {
        return this.data.writeInt(i);
    }

    @Override
    public ByteBuf writeIntLE(int i) {
        return null;
    }

    public ByteBuf writeLong(long i) {
        return this.data.writeLong(i);
    }

    @Override
    public ByteBuf writeLongLE(long l) {
        return null;
    }

    public ByteBuf writeChar(int i) {
        return this.data.writeChar(i);
    }

    public ByteBuf writeFloat(float f) {
        return this.data.writeFloat(f);
    }

    public ByteBuf writeDouble(double d0) {
        return this.data.writeDouble(d0);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf) {
        return this.data.writeBytes(bytebuf);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        return this.data.writeBytes(bytebuf, i);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        return this.data.writeBytes(bytebuf, i, j);
    }

    public ByteBuf writeBytes(byte[] abyte) {
        return this.data.writeBytes(abyte);
    }

    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        return this.data.writeBytes(abyte, i, j);
    }

    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        return this.data.writeBytes(bytebuffer);
    }

    public int writeBytes(InputStream inputstream, int i) throws IOException {
        return this.data.writeBytes(inputstream, i);
    }

    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
        return this.data.writeBytes(scatteringbytechannel, i);
    }

    @Override
    public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return 0;
    }

    public ByteBuf writeZero(int i) {
        return this.data.writeZero(i);
    }

    @Override
    public int writeCharSequence(CharSequence charSequence, Charset charset) {
        return 0;
    }

    public int indexOf(int i, int j, byte b0) {
        return this.data.indexOf(i, j, b0);
    }

    public int bytesBefore(byte b0) {
        return this.data.bytesBefore(b0);
    }

    public int bytesBefore(int i, byte b0) {
        return this.data.bytesBefore(i, b0);
    }

    public int bytesBefore(int i, int j, byte b0) {
        return this.data.bytesBefore(i, j, b0);
    }

    @Override
    public int forEachByte(ByteProcessor byteProcessor) {
        return 0;
    }

    @Override
    public int forEachByte(int i, int i1, ByteProcessor byteProcessor) {
        return 0;
    }

    @Override
    public int forEachByteDesc(ByteProcessor byteProcessor) {
        return 0;
    }

    @Override
    public int forEachByteDesc(int i, int i1, ByteProcessor byteProcessor) {
        return 0;
    }

    public int forEachByte(ByteBufProcessor bytebufprocessor) {
        return this.data.forEachByte(bytebufprocessor);
    }

    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.data.forEachByte(i, j, bytebufprocessor);
    }

    public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
        return this.data.forEachByteDesc(bytebufprocessor);
    }

    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.data.forEachByteDesc(i, j, bytebufprocessor);
    }

    public ByteBuf copy() {
        return this.data.copy();
    }

    public ByteBuf copy(int i, int j) {
        return this.data.copy(i, j);
    }

    public ByteBuf slice() {
        return this.data.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return null;
    }

    public ByteBuf slice(int i, int j) {
        return this.data.slice(i, j);
    }

    @Override
    public ByteBuf retainedSlice(int i, int i1) {
        return null;
    }

    public ByteBuf duplicate() {
        return this.data.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return null;
    }

    public int nioBufferCount() {
        return this.data.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.data.nioBuffer();
    }

    public ByteBuffer nioBuffer(int i, int j) {
        return this.data.nioBuffer(i, j);
    }

    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.data.internalNioBuffer(i, j);
    }

    public ByteBuffer[] nioBuffers() {
        return this.data.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.data.nioBuffers(i, j);
    }

    public boolean hasArray() {
        return this.data.hasArray();
    }

    public byte[] array() {
        return this.data.array();
    }

    public int arrayOffset() {
        return this.data.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.data.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.data.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.data.toString(charset);
    }

    public String toString(int i, int j, Charset charset) {
        return this.data.toString(i, j, charset);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public boolean equals(Object object) {
        return this.data.equals(object);
    }

    public int compareTo(ByteBuf bytebuf) {
        return this.data.compareTo(bytebuf);
    }

    public String toString() {
        return this.data.toString();
    }

    public ByteBuf retain(int i) {
        return this.data.retain(i);
    }

    public ByteBuf retain() {
        return this.data.retain();
    }

    @Override
    public ByteBuf touch() {
        return null;
    }

    @Override
    public ByteBuf touch(Object o) {
        return null;
    }

    public int refCnt() {
        return this.data.refCnt();
    }

    public boolean release() {
        return this.data.release();
    }

    public boolean release(int i) {
        return this.data.release(i);
    }
}
