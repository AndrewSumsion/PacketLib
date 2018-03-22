package me.El_Chupe.packetlib;

public class PacketInContainer extends PacketContainer{
    PacketInContainer(PacketType type, PacketDataSerializer data) {
        super(type, data);
    }

    @Override
    public PacketDataSerializer getData() {
        PacketDataSerializer data = super.getData();
        data.resetReaderIndex();
        return data;
    }
}
