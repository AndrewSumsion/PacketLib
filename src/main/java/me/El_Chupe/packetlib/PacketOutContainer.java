package me.El_Chupe.packetlib;

import org.bukkit.entity.Player;

public class PacketOutContainer extends PacketContainer {
    private boolean custom;

    public PacketOutContainer(PacketType type, PacketDataSerializer data) {
        this(type, data, true);
    }

    PacketOutContainer(PacketType type, PacketDataSerializer data, boolean custom) {
        super(type, data);
        this.custom = custom;
    }

    public void send(Player player) {
        InjectionUtils.getChannel(player).write(this);
    }

    boolean isCustom() {
        return custom;
    }
}
