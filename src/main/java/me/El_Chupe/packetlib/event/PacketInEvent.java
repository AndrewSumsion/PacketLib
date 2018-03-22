package me.El_Chupe.packetlib.event;

import me.El_Chupe.packetlib.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PacketInEvent extends PacketEvent {
    private static HandlerList handlerList = new HandlerList();

    public PacketInEvent(Player player, PacketContainer packet) {
        super(player, packet);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
