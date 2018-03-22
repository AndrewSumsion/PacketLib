package me.El_Chupe.packetlib;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ReflectionUtils {
    private static ReflectionUtils instance;
    private final String versionString;

    private static ReflectionUtils getInstance() {
        if(instance == null) {
            instance = new ReflectionUtils();
        }
        return instance;
    }

    private ReflectionUtils() {
        versionString = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private Class<?> getClass1(String path) {
        try {
            return Class.forName(path.replaceAll("org\\.bukkit\\.craftbukkit", "org.bukkit.craftbukkit."+versionString)
                    .replaceAll("net\\.minecraft\\.server", "net.minecraft.server."+versionString));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getClass(String path) {
        return getInstance().getClass1(path);
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        try {
            return clazz.getMethod(name, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getVersionString() {
        return instance.versionString;
    }
    public static Object getNmsPlayer(Player player) {
        Class<?> craftPlayerClass = getClass("org.bukkit.craftbukkit.entity.CraftPlayer");
        Method getHandleMethod = getMethod(craftPlayerClass, "getHandle");
        return invokeMethod(getHandleMethod, craftPlayerClass.cast(player));
    }
    /*public static Channel getChannel(Player player) {
        Class<?> craftPlayerClass = getClass("org.bukkit.craftbukkit.entity.CraftPlayer");
        Class<?> nmsPlayerClass = getClass("net.minecraft.server.EntityPlayer");
        Class<?> playerConnectionClass = getClass("net.minecraft.server.PlayerConnection");
        Class<?> networkManagerClass = getClass("net.minecraft.server.NetworkManager");

        Method getHandleMethod = getMethod(craftPlayerClass, "getHandle");
        Object nmsPlayer = invokeMethod(getHandleMethod, craftPlayerClass.cast(player));
        Object playerConnection = getField(nmsPlayerClass, "playerConnection").get(nmsPlayer);


    }*/
}
