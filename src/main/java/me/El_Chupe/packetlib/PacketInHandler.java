package me.El_Chupe.packetlib;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import me.El_Chupe.packetlib.event.PacketInEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class PacketInHandler extends ChannelInboundHandlerAdapter {
    private static final AttributeKey protocolKey = AttributeKey.valueOf("protocol");
    private Player player;

    public PacketInHandler(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(read(ctx, msg)) ctx.fireChannelRead(msg);
    }
    /*
    private boolean read(ChannelHandlerContext ctx, Object msg) {
        if(!ByteBuf.class.isAssignableFrom(msg.getClass())) return true;
        ByteBuf byteBuf = (ByteBuf) msg;
        PacketDataSerializer data = new PacketDataSerializer(byteBuf);
        PacketType type = PacketType.get(data.readVarInt(),
                PacketType.Protocol.fromNMSEnumProtocol(ctx.channel().attr(protocolKey).get()),
                PacketType.Protocol.Direction.SERVERBOUND
        );
        PacketInContainer packet = new PacketInContainer(type, new PacketDataSerializer(data.copy().discardReadBytes()));
        data.resetReaderIndex();
        PacketInEvent event = new PacketInEvent(player, packet);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }*/

    private boolean read(ChannelHandlerContext ctx, Object msg) {
        if(!ByteBuf.class.isAssignableFrom(msg.getClass())) return true;
        PacketDataSerializer data = new PacketDataSerializer(((ByteBuf) msg).copy());
        PacketType type = PacketType.get(
                data.readVarInt(),
                PacketType.Protocol.fromNMSEnumProtocol(ctx.channel().attr(protocolKey).get()),
                PacketType.Protocol.Direction.SERVERBOUND
        );
        data.discardReadBytes();
        PacketInContainer packet = new PacketInContainer(type, data);
        PacketInEvent event = new PacketInEvent(player, packet);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

    public static void injectPlayer(Player player) {
        ChannelPipeline pipeline = InjectionUtils.getChannel(player).pipeline();
        pipeline.addBefore("decoder", "PacketLib_PacketInHandler", new PacketInHandler(player));
    }
}
