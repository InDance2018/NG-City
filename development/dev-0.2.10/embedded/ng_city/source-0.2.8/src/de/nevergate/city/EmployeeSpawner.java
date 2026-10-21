package de.nevergate.city;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public final class EmployeeSpawner {
    public static boolean add(ServerLevel level,Entity employee) {
        return CustomSpawnAdapter.add(level,employee,Entity.class,()->level.m_7967_(employee));
    }
    private EmployeeSpawner() {}
}
