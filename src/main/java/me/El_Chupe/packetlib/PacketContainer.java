package me.El_Chupe.packetlib;

public class PacketContainer {
    private PacketType type;
    private PacketDataSerializer data;

    public PacketContainer(PacketType type, PacketDataSerializer data) {
        this.type = type;
        this.data = data;
    }

    public PacketType getType() {
        return type;
    }

    public PacketDataSerializer getData() {
        return data;
    }
}
