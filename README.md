# PacketLib
A Bukkit plugin that injects custom handlers into the netty pipeline to allow reading and writing of raw packets.

## How to use:
Intercept an inbound packet:
```java
@EventHandler
public void onPacketIn(PacketInEvent event) {
    Packet packet = event.getPacket();
    
    Player player = event.getPlayer();
    // Get player who sent the packet
    
    if(!packet.getType().equals(PacketType.fromName("PacketPlayInUseEntity")))
        return;
    // Filter for specific packet type
    
    PacketDataSerializer data = packet.getData();
    // Read raw inbound data before it's processed by the server
    
    event.setCancelled(true);
    // Prevent server from receiving packet
}
```

Intercept an outbound packet:
```java
public void onPacketOut(PacketOutEvent event) {
    Packet packet = event.getPacket();
    
    Player player = event.getPlayer();
    // Get player the packet was sent to
    
    event.setCancelled(true);
    // Prevent packet from being sent
}
```

Send a custom packet:
```java
PacketDataSerializer data = new PacketDataSerializer();

// ... write custom data to packet

Packet packet = new PacketOutContainer(PacketType.fromName("PacketPlayOutSpawnEntity"), data);
packet.send(player); // send packet to player
```

### Additional notes:
The names and formats of packet types can be found in a decompiled Bukkit server.

This plugin can read and write custom Minecraft data types like VarInt and a special format for strings. Use PacketDataSerializer to read and write data.

An example plugin using PacketLib can be found here: https://github.com/AndrewSumsion/AnimatedFrames
