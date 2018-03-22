package me.El_Chupe.packetlib.event;

import me.El_Chupe.packetlib.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class PacketEvent extends Event {
    private Player player;
    private PacketContainer packet;
    private boolean cancelled = false;

    protected PacketEvent(Player player, PacketContainer packet) {
        this.player = player;
        this.packet = packet;
    }

    public PacketContainer getPacket() {
        return packet;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
