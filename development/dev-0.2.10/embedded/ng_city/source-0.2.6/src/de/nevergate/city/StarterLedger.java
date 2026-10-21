package de.nevergate.city;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public final class StarterLedger extends SavedData {
   private final Set<UUID> recipients = new HashSet<>();
   private boolean communalPlaced;
   private BlockPos communalPos;
   private BlockPos observedSpawn;
   private String worldIdentity="";
   public String worldIdentity() { return worldIdentity; }
   public void bindWorld(String id) { if (!worldIdentity.equals(id)) { worldIdentity=id; m_77762_(); } }

   private CompoundTag relocation;

   public BlockPos observedSpawn() { return observedSpawn; }
   public void observeSpawn(BlockPos spawn) { observedSpawn=spawn.m_7949_(); m_77762_(); }
   public CompoundTag relocation() { return relocation; }
   public void beginRelocation(BlockPos to, BlockPos spawn, CompoundTag contents) {
      relocation=new CompoundTag();
      relocation.m_128356_("from", communalPos.m_121878_());
      relocation.m_128356_("to", to.m_121878_());
      relocation.m_128356_("spawn", spawn.m_121878_());
      relocation.m_128365_("contents", contents.m_6426_());
      m_77762_();
   }
   public void finishRelocation() {
      communalPos=BlockPos.m_122022_(relocation.m_128454_("to"));
      observedSpawn=BlockPos.m_122022_(relocation.m_128454_("spawn"));
      relocation=null;
      m_77762_();
   }

   public boolean distributed() {
      return this.communalPlaced || !this.recipients.isEmpty();
   }

   public BlockPos location() {
      return this.communalPos;
   }

   public void markCommunal(BlockPos var1) {
      this.communalPlaced = true;
      this.communalPos = var1.m_7949_();
      this.m_77762_();
   }

   public boolean has(UUID var1) {
      return this.recipients.contains(var1);
   }

   public void mark(UUID var1) {
      this.recipients.add(var1);
      this.m_77762_();
   }

   public static StarterLedger get(ServerLevel var0) {
      return (StarterLedger)var0.m_8895_()
         .m_164861_(new Factory<StarterLedger>(StarterLedger::new, StarterLedger::load, DataFixTypes.LEVEL), "ng_city_starters");
   }

   public static StarterLedger load(CompoundTag var0) {
      StarterLedger var1 = new StarterLedger();
      var1.communalPlaced = var0.m_128471_("communalPlaced");
      if (var0.m_128441_("communalPos")) {
         var1.communalPos = BlockPos.m_122022_(var0.m_128454_("communalPos"));
      }

      for (Tag var4 : var0.m_128437_("recipients", 8)) {
         try {
            var1.recipients.add(UUID.fromString(var4.m_7916_()));
         } catch (IllegalArgumentException var6) {
         }
      }

      var1.worldIdentity=var0.m_128461_("worldIdentity");
      if (var0.m_128441_("observedSpawn")) var1.observedSpawn=BlockPos.m_122022_(var0.m_128454_("observedSpawn"));
      if (var0.m_128425_("relocation", 10)) var1.relocation=var0.m_128469_("relocation");
      return var1;
   }

   public CompoundTag m_7176_(CompoundTag var1) {
      ListTag var2 = new ListTag();
      this.recipients.stream().sorted().forEach(var1x -> var2.add(StringTag.m_129297_(var1x.toString())));
      var1.m_128365_("recipients", var2);
      var1.m_128379_("communalPlaced", this.communalPlaced);
      if (this.communalPos != null) {
         var1.m_128356_("communalPos", this.communalPos.m_121878_());
      }

      var1.m_128359_("worldIdentity",worldIdentity);
      if (observedSpawn!=null) var1.m_128356_("observedSpawn", observedSpawn.m_121878_());
      if (relocation!=null) var1.m_128365_("relocation", relocation);
      return var1;
   }
}
