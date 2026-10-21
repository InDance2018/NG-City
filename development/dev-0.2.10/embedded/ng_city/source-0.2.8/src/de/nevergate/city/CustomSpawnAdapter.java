package de.nevergate.city;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

/** Optional hybrid-server adapter. Explicit false and thrown errors are never retried. */
public final class CustomSpawnAdapter {
    public static boolean add(Object world,Object entity,Class<?> entityType,BooleanSupplier forgeAdd) {
        Method method;
        Object reason;
        try {
            Class<?> reasonType=Class.forName("org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason",false,world.getClass().getClassLoader());
            reason=reasonType.getField("CUSTOM").get(null);
            method=world.getClass().getMethod("bridge$addEntity",entityType,reasonType);
            if(method.getReturnType()!=boolean.class)return forgeAdd.getAsBoolean();
        } catch(ClassNotFoundException|NoSuchMethodException|NoSuchFieldException e) {
            return forgeAdd.getAsBoolean();
        } catch(IllegalAccessException e) {
            throw new IllegalStateException("NPC-Spawn-Grund konnte nicht gelesen werden",e);
        }
        try {
            return (Boolean)method.invoke(world,entity,reason);
        } catch(InvocationTargetException e) {
            Throwable cause=e.getCause();
            if(cause instanceof RuntimeException failure)throw failure;
            if(cause instanceof Error failure)throw failure;
            throw new IllegalStateException("Server hat den NPC-Spawn abgebrochen",cause);
        } catch(IllegalAccessException e) {
            throw new IllegalStateException("NPC-Spawn-Schnittstelle nicht zugänglich",e);
        }
    }
    private CustomSpawnAdapter() {}
}
