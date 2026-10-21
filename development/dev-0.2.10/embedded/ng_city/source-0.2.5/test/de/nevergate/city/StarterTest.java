package de.nevergate.city;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;

public final class StarterTest {
    static int checks;
    static void ok(boolean value,String why) { checks++; if(!value) throw new AssertionError(why); }
    static final class Crash extends RuntimeException {}
    static class Fake implements StarterMove.World {
        StarterMove.State from=StarterMove.State.CONTENTS,to=StarterMove.State.AIR;
        boolean done,flushed,allow=true; int crashAt=-1,steps,writes;
        void step(){writes++;if(++steps==crashAt) throw new Crash();}
        public StarterMove.State source(){return from;}
        public StarterMove.State target(){return to;}
        public boolean reserve(){if(!allow)return false;to=StarterMove.State.RESERVED;step();return true;}
        public boolean removeSource(){from=StarterMove.State.AIR;step();return true;}
        public void restoreTarget(){to=StarterMove.State.CONTENTS;step();}
        public void flush(){flushed=true;step();}
        public void complete(){ok(flushed,"journal must outlive durable chunks");done=true;step();}
    }
    public static void main(String[] args){
        for(var source:StarterMove.State.values()) for(var target:StarterMove.State.values()){
            Fake f=new Fake();f.from=source;f.to=target;
            boolean expected=source!=StarterMove.State.OTHER && target!=StarterMove.State.OTHER;
            ok(StarterMove.resume(f)==expected,"state pair");
            if(expected){ok(f.from==StarterMove.State.AIR && f.to==StarterMove.State.CONTENTS && f.done,"one final crate");}
            else ok(f.writes==0,"conflicting source/target untouched");
        }
        for(int step=1;step<=5;step++){
            Fake f=new Fake();f.crashAt=step;
            try{StarterMove.resume(f);}catch(Crash ignored){}
            f.crashAt=-1;
            if(!f.done)ok(StarterMove.resume(f),"interrupted move resumes at "+step);
            ok(f.from==StarterMove.State.AIR && f.to==StarterMove.State.CONTENTS,"no duplicate after recovery "+step);
        }
        Fake blocked=new Fake();blocked.allow=false;
        ok(!StarterMove.resume(blocked),"no safe target");
        ok(blocked.from==StarterMove.State.CONTENTS && !blocked.done && blocked.writes==0,"blocked move retains original");

        BlockPos old=new BlockPos(2,65,0),spawn=new BlockPos(10000,90,-20000);
        StarterLedger ledger=new StarterLedger();ledger.markCommunal(old);
        CompoundTag legacy=ledger.m_7176_(new CompoundTag());
        StarterLedger loaded=StarterLedger.load(legacy);
        ok(loaded.distributed() && loaded.location().equals(old) && loaded.observedSpawn()==null,"legacy ledger migration");
        loaded.observeSpawn(spawn);
        var again=StarterLedger.load(loaded.m_7176_(new CompoundTag()));
        ok(again.observedSpawn().equals(spawn),"spawn survives restart");
        CompoundTag payload=new CompoundTag();payload.m_128359_("CustomName","saved chest contents");
        BlockPos destination=new BlockPos(10002,91,-20000);
        again.beginRelocation(destination,spawn,payload);
        var pending=StarterLedger.load(again.m_7176_(new CompoundTag()));
        ok(pending.location().equals(old) && pending.relocation()!=null,"durable source retained while pending");
        ok(pending.relocation().m_128469_("contents").equals(payload),"recovery payload persisted");
        pending.finishRelocation();
        var moved=StarterLedger.load(pending.m_7176_(new CompoundTag()));
        ok(moved.location().equals(destination) && moved.observedSpawn().equals(spawn) && moved.relocation()==null && moved.distributed(),"completed move persists without reissue");
        StarterLedger historical=new StarterLedger();historical.mark(UUID.randomUUID());
        var historicalReload=StarterLedger.load(historical.m_7176_(new CompoundTag()));
        ok(historicalReload.distributed() && historicalReload.location()==null,"personal starter never becomes second communal crate");
        for(int protection:new int[]{0,1,16,100,100000,Integer.MAX_VALUE}){
            long offset=Math.max(2L,(long)protection+2L);
            if(offset+8>Integer.MAX_VALUE)continue;
            for(int dx=-8;dx<=8;dx++)for(int dz=-8;dz<=8;dz++)
                ok(SpawnStarter.inLegacyFootprint(new BlockPos((int)offset+dx,80,dz),BlockPos.f_121853_,protection),"legacy candidate preserved");
            ok(!SpawnStarter.inLegacyFootprint(new BlockPos((int)offset+9,80,0),BlockPos.f_121853_,protection),"outside old footprint");
        }
        ok(!SpawnStarter.inLegacyFootprint(old,spawn,0),"old crate follows distant new spawn");
        System.out.println("STARTER TESTS PASSED: "+checks);
    }
}
