package me.El_Chupe.packetlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PacketType {
    private Class<?> clazz;
    private Protocol protocol;
    private Protocol.Direction direction;
    private int id;

    private static final List<PacketType> packetTypes = new ArrayList<PacketType>();

    private PacketType(Class<?> clazz) {
        this.clazz = clazz;
        String protocolString = clazz.getSimpleName().substring("Packet".length());
        if(protocolString.startsWith("Handshaking")) protocol = Protocol.HANDSHAKING;
        else if(protocolString.startsWith("Play")) protocol = Protocol.PLAY;
        else if(protocolString.startsWith("Status")) protocol = Protocol.STATUS;
        else if(protocolString.startsWith("Login")) protocol = Protocol.LOGIN;
        String directionString = protocolString.substring(protocol.name().length());
        if(directionString.startsWith("In")) direction = Protocol.Direction.SERVERBOUND;
        else if(directionString.startsWith("Out")) direction = Protocol.Direction.CLIENTBOUND;
        try {
            id = (Integer) Protocol.getIdMethod().invoke(protocol.getNMSEnumProtocol(), direction.getNMSEnumProtocolDirection(), clazz.newInstance());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private PacketType(Class<?> clazz, Protocol protocol, Protocol.Direction direction) {
        this.clazz = clazz;
        this.protocol = protocol;
        this.direction = direction;
        try {
            this.id = (Integer) Protocol.getIdMethod().invoke(protocol.getNMSEnumProtocol(), direction.getNMSEnumProtocolDirection(), clazz.newInstance());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private PacketType(int id, Protocol protocol, Protocol.Direction direction) {
        this.id = id;
        this.protocol = protocol;
        this.direction = direction;

        try {
            this.clazz = Protocol.getPacketMethod().invoke(protocol.getNMSEnumProtocol(), direction.getNMSEnumProtocolDirection(), id).getClass();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static PacketType get(Class<?> clazz, Protocol protocol, Protocol.Direction direction) {
        PacketType type = getBuffered(clazz);
        if(type != null) return type;
        type = new PacketType(clazz, protocol, direction);
        packetTypes.add(type);
        return type;
    }

    public static PacketType get(int id, Protocol protocol, Protocol.Direction direction) {
        PacketType type = getBuffered(id, protocol, direction);
        if(type != null) return type;
        type = new PacketType(id, protocol, direction);
        packetTypes.add(type);
        return type;
    }

    public static PacketType fromClass(Class<?> clazz) {
        PacketType type = getBuffered(clazz);
        if(type != null) return type;
        type = new PacketType(clazz);
        packetTypes.add(type);
        return type;
    }

    public static PacketType fromName(String packetName) {
        return fromClass(ReflectionUtils.getClass("net.minecraft.server."+packetName));
    }

    private static PacketType getBuffered(Class<?> clazz) {
        for(PacketType packetType : packetTypes) {
            if(packetType.getPacketClass().equals(clazz)) {
                return packetType;
            }
        }
        return null;
    }

    private static PacketType getBuffered(int id, Protocol protocol, Protocol.Direction direction) {
        for(PacketType packetType : packetTypes) {
            if(packetType.getId() == id
                    && packetType.getProtocol().equals(protocol)
                    && packetType.getDirection().equals(direction)) {
                return packetType;
            }
        }
        return null;
    }

    public Class<?> getPacketClass() {
        return clazz;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Protocol.Direction getDirection() {
        return direction;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(!PacketType.class.isAssignableFrom(o.getClass())) return false;
        PacketType type = (PacketType) o;
        return type.getId() == this.id && type.getProtocol().equals(this.protocol) && type.getDirection().equals(this.direction);
    }

    public enum Protocol {
        HANDSHAKING,
        PLAY,
        STATUS,
        LOGIN;

        private Object nmsEnumProtocol;
        private static Class<?> enumProtocolClass = ReflectionUtils.getClass("net.minecraft.server.EnumProtocol");
        private static Method idMethod;
        private static Method packetMethod;
        static {
            for(Method method : enumProtocolClass.getMethods()) {
                if(method.getReturnType().equals(Integer.class)) {
                    idMethod = method;
                }
                if(method.getReturnType().equals(ReflectionUtils.getClass("net.minecraft.server.Packet"))) {
                    packetMethod = method;
                }
            }
        }

        Protocol() {
            try {
                nmsEnumProtocol = ReflectionUtils.getClass("net.minecraft.server.EnumProtocol").getDeclaredField(name()).get(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        Object getNMSEnumProtocol() {
            return nmsEnumProtocol;
        }

        static Class<?> getEnumProtocolClass() {
            return enumProtocolClass;
        }

        protected static Method getIdMethod() {
            return idMethod;
        }

        protected static Method getPacketMethod() {
            return packetMethod;
        }

        static Protocol fromNMSEnumProtocol(Object object) {
            if(!enumProtocolClass.isAssignableFrom(object.getClass())) throw new RuntimeException("object is not an instance of EnumProtocol!");
            for(Protocol protocol : Protocol.values()) {
                if(protocol.getNMSEnumProtocol().equals(object)) return protocol;
            }
            throw new RuntimeException("no value of Protocol matches object");
        }

        public enum Direction {
            CLIENTBOUND,
            SERVERBOUND;

            private Object nmsEnumProtocolDirection;
            private static Class<?> enumProtocolDirectionClass = ReflectionUtils.getClass("net.minecraft.server.EnumProtocolDirection");

            Direction() {
                try {
                    nmsEnumProtocolDirection = ReflectionUtils.getClass("net.minecraft.server.EnumProtocolDirection").getDeclaredField(name()).get(null);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }

            Object getNMSEnumProtocolDirection() {
                return nmsEnumProtocolDirection;
            }

            static Class<?> getEnumProtocolDirectionClass() {
                return enumProtocolDirectionClass;
            }
        }
    }

}
