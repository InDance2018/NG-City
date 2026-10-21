package de.nevergate.city;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Moves only the existing, complete communal starter. Never reissues consumed equipment. */
public final class SpawnStarter {
    private static final String NAME="NG-City · Gemeinsame Startkiste";
    private static final Logger LOG=LoggerFactory.getLogger("ng_city.spawn_starter");

    public static void reconcile(MinecraftServer server, ServerLevel level, StarterLedger ledger) {
        if (ledger.relocation()!=null) { resume(level,ledger); return; }
        BlockPos source=ledger.location();
        if (source==null) return; // Historical personal starter: no new communal equipment.
        BlockPos spawn=level.m_220360_();
        if (spawn.equals(ledger.observedSpawn())) return;
        int protection=server.m_6982_()?server.m_6396_():0;
        // Pre-0.2.5 saves have no recorded spawn. Their old search footprint is known.
        if (ledger.observedSpawn()==null && inLegacyFootprint(source,spawn,protection)) {
            ledger.observeSpawn(spawn); saveLedger(level); return;
        }
        if (!pristine(level,source)) {
            // Used, missing or modified: remember this spawn; never refill/replace the crate.
            ledger.observeSpawn(spawn); saveLedger(level); return;
        }
        BlockPos target=find(level,spawn,protection);
        if (target==null) return; // Keep source intact and retry if space becomes available.
        ChestBlockEntity chest=(ChestBlockEntity)level.m_7702_(source);
        ledger.beginRelocation(target,spawn,chest.m_187482_());
        saveLedger(level); // Durable intent precedes every block change.
        if (resume(level,ledger)) LOG.info("NG-City start crate moved from {} to {} for world spawn {}",source,target,spawn);
    }

    public static boolean inLegacyFootprint(BlockPos crate,BlockPos spawn,int protection) {
        long offset=Math.max(2L,(long)protection+2L);
        long dx=(long)crate.m_123341_()-spawn.m_123341_();
        long dz=(long)crate.m_123343_()-spawn.m_123343_();
        return Math.abs(dx-offset)<=8 && Math.abs(dz)<=8;
    }

    private static boolean namedSingle(ServerLevel level,BlockPos pos) {
        return level.m_8055_(pos).m_60713_(Blocks.f_50087_)
            && level.m_8055_(pos).m_61143_(ChestBlock.f_51479_)==ChestType.SINGLE
            && level.m_7702_(pos) instanceof ChestBlockEntity chest
            && chest.m_7755_().getString().equals(NAME);
    }
    public static boolean pristine(ServerLevel level,BlockPos pos) {
        if (!namedSingle(level,pos)) return false;
        ChestBlockEntity chest=(ChestBlockEntity)level.m_7702_(pos);
        for (int slot=0;slot<chest.m_6643_();slot++) {
            ItemStack item=chest.m_8020_(slot);
            if (slot==13) {
                if (!item.m_150930_(NgCity.COMPLEX.get()) || item.m_41613_()!=1 || item.m_41782_()) return false;
            } else if (slot==22) {
                if (!item.m_150930_(Items.f_42615_) || item.m_41613_()!=1 || !item.m_41782_()
                    || !item.m_41783_().m_128461_("author").equals("NeverGate")
                    || !item.m_41783_().m_128461_("title").equals("NG-City · Starthilfe")) return false;
            } else if (!item.m_41619_()) return false;
        }
        return true;
    }

    private static BlockPos find(ServerLevel level,BlockPos spawn,int protection) {
        long offset=Math.max(2L,(long)protection+2L);
        for (int radius=0;radius<=8;radius++) for (int dx=-radius;dx<=radius;dx++) for (int dz=-radius;dz<=radius;dz++) {
            if (Math.max(Math.abs(dx),Math.abs(dz))!=radius) continue;
            long x=(long)spawn.m_123341_()+offset+dx,z=(long)spawn.m_123343_()+dz;
            if (Math.abs(x)>29999984 || Math.abs(z)>29999984 || Math.max(Math.abs(x-spawn.m_123341_()),Math.abs(z-spawn.m_123343_()))<=protection) continue;
            BlockPos column=new BlockPos((int)x,spawn.m_123342_(),(int)z);
            if (!level.m_6857_().m_61937_(column)) continue;
            BlockPos candidate=level.m_5452_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,column);
            if (safe(level,candidate)) return candidate;
        }
        return null;
    }
    private static boolean safe(ServerLevel level,BlockPos pos) {
        if (level.m_151570_(pos) || level.m_151570_(pos.m_7494_()) || !level.m_6857_().m_61937_(pos)) return false;
        if (!level.m_8055_(pos).m_60795_() || !level.m_8055_(pos.m_7494_()).m_60795_() || level.m_7702_(pos)!=null) return false;
        var floor=level.m_8055_(pos.m_7495_());
        if (!floor.m_60783_(level,pos.m_7495_(),Direction.UP) || !floor.m_60819_().m_76178_() || floor.m_204336_(BlockTags.f_13035_)) return false;
        for (Direction side:Direction.Plane.HORIZONTAL) {
            var adjacent=level.m_8055_(pos.m_121945_(side));
            if (adjacent.m_60713_(Blocks.f_50087_) || adjacent.m_60713_(Blocks.f_50325_)) return false;
        }
        return true;
    }
    private static void saveLedger(ServerLevel level) { level.m_8895_().m_78151_(); }

    private static boolean resume(ServerLevel level,StarterLedger ledger) {
        CompoundTag journal=ledger.relocation();
        BlockPos from=BlockPos.m_122022_(journal.m_128454_("from"));
        BlockPos to=BlockPos.m_122022_(journal.m_128454_("to"));
        if (from.equals(to)) return false;
        CompoundTag payload=journal.m_128469_("contents");
        return StarterMove.resume(new StarterMove.World() {
            private StarterMove.State state(BlockPos at) {
                if (level.m_8055_(at).m_60795_() && level.m_7702_(at)==null) return StarterMove.State.AIR;
                if (!namedSingle(level,at)) return StarterMove.State.OTHER;
                var chest=(ChestBlockEntity)level.m_7702_(at);
                if (chest.m_187482_().equals(payload)) return StarterMove.State.CONTENTS;
                return chest.m_7983_()?StarterMove.State.RESERVED:StarterMove.State.OTHER;
            }
            public StarterMove.State source() { return state(from); }
            public StarterMove.State target() { return state(to); }
            public boolean reserve() {
                if (!safe(level,to)) return false;
                if (!level.m_7731_(to,Blocks.f_50087_.m_49966_().m_61124_(ChestBlock.f_51478_,Direction.WEST),3)) return false;
                if (!(level.m_7702_(to) instanceof ChestBlockEntity chest)) return false;
                chest.m_58638_(Component.m_237113_(NAME)); chest.m_6596_(); return true;
            }
            public boolean removeSource() {
                if (!(level.m_7702_(from) instanceof ChestBlockEntity chest)) return false;
                chest.m_6211_(); chest.m_6596_();
                return level.m_7731_(from,Blocks.f_50016_.m_49966_(),3);
            }
            public void restoreTarget() {
                var chest=(ChestBlockEntity)level.m_7702_(to);
                chest.m_142466_(payload.m_6426_()); chest.m_6596_();
            }
            public void flush() { level.m_8643_(null,true,false); }
            public void complete() { ledger.finishRelocation(); saveLedger(level); }
        });
    }
    private SpawnStarter() {}
}
