package me.El_Chupe.packetlib.event;

import me.El_Chupe.packetlib.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PacketOutEvent extends PacketEvent {
    private static HandlerList handlerList = new HandlerList();
    private boolean custom;

    public PacketOutEvent(Player player, PacketContainer packet, boolean custom) {
        super(player, packet);
        this.custom = custom;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public boolean isCustom() {
        return custom;
    }
}
