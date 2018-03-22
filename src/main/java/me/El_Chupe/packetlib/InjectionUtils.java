package me.El_Chupe.packetlib;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;

class InjectionUtils {
    private static HashMap<Player, Channel> channels = new HashMap<Player, Channel>();

    public static Channel getChannel(Player player) {
        if(channels.containsKey(player)) {
            return channels.get(player);
        }
        Channel channel = getChannelReflect(player);
        channels.put(player, channel);
        return channel;

    }
    private static Channel getChannelReflect(Player player) {
        try {
            Class<?> craftPlayerClass = ReflectionUtils.getClass("org.bukkit.craftbukkit.entity.CraftPlayer");
            Class<?> entityPlayerClass = ReflectionUtils.getClass("net.minecraft.server.EntityPlayer");
            Class<?> playerConnectionClass = ReflectionUtils.getClass("net.minecraft.server.PlayerConnection");
            Class<?> networkManagerClass = ReflectionUtils.getClass("net.minecraft.server.NetworkManager");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object entityPlayer = craftPlayerClass.getDeclaredMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = entityPlayerClass.getDeclaredField("playerConnection").get(entityPlayer);
            Object networkManager = playerConnectionClass.getDeclaredField("networkManager").get(playerConnection);
            Field channelField = null;
            for(Field field : networkManagerClass.getDeclaredFields()) {
                if(Channel.class.isAssignableFrom(field.getType())) {
                    channelField = field;
                    break;
                }
            }
            if(channelField == null) {
                throw new RuntimeException("Channel field not found in PlayerConnection class!");
            }
            channelField.setAccessible(true);
            return (Channel) channelField.get(networkManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void injectPlayer(Player player) {
        PacketEncoder.injectPlayer(player);
        PacketInHandler.injectPlayer(player);
    }

    public static void uninjectPlayer(Player player) {
        channels.remove(player);
    }
}
