package me.El_Chupe.packetlib;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToByteEncoder;
import me.El_Chupe.packetlib.event.PacketOutEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class PacketEncoder extends MessageToByteEncoder<Object> {
    private Player player;

    public static void injectPlayer(Player player) {
        ChannelPipeline pipeline = InjectionUtils.getChannel(player).pipeline();
        if(pipeline.get("encoder").getClass().equals(PacketEncoder.class)) return;
        pipeline.addBefore("encoder", "temphandler", new ChannelOutboundHandlerAdapter());
        pipeline.remove("encoder");
        pipeline.addAfter("temphandler", "encoder", new PacketEncoder(player));
        pipeline.remove("temphandler");
    }

    public PacketEncoder(Player player) {
        this.player = player;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        try {
            Class<?> packetClass = ReflectionUtils.getClass("net.minecraft.server.Packet");
            if (PacketOutContainer.class.isAssignableFrom(o.getClass())) {
                PacketOutContainer packet = (PacketOutContainer) o;
                if (!callEvent(packet)) return;
                PacketDataSerializer data = new PacketDataSerializer(byteBuf);
                data.writeVarInt(packet.getType().getId());
                data.writeBytes(packet.getData());
            } else if (packetClass.isAssignableFrom(o.getClass())) {
                PacketDataSerializer data = new PacketDataSerializer();
                Class<?> nmsDataClass = ReflectionUtils.getClass("net.minecraft.server.PacketDataSerializer");
                Object nmsData = nmsDataClass.getDeclaredConstructor(ByteBuf.class).newInstance(data);
                o.getClass().getDeclaredMethod("b", nmsDataClass).invoke(o, nmsData);
                PacketType type = PacketType.fromClass(o.getClass());
                if (!callEvent(PacketType.fromClass(o.getClass()), data, false)) return;
                PacketDataSerializer.writeVarInt(byteBuf, type.getId());
                byteBuf.writeBytes(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean callEvent(PacketType type, PacketDataSerializer data, boolean custom) {
        return callEvent(new PacketOutContainer(type, data, custom));
    }

    private boolean callEvent(PacketOutContainer packet) {
        PacketOutEvent event = new PacketOutEvent(player, packet, packet.isCustom());
        Bukkit.getPluginManager().callEvent(event);
        packet.getData().resetReaderIndex();
        return !event.isCancelled();
    }
}
