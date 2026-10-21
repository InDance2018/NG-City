package net.minecraft.server.level;
import net.minecraft.world.level.storage.DimensionDataStorage;
public final class ServerLevel {
 private final DimensionDataStorage storage;
 public ServerLevel(java.io.File root) { storage=new DimensionDataStorage(root,null); }
 public DimensionDataStorage m_8895_() { return storage; }
}
