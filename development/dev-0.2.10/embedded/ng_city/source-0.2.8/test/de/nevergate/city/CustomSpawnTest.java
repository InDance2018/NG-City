package de.nevergate.city;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public final class CustomSpawnTest {
    static int checks;
    static void ok(boolean value){checks++;if(!value)throw new AssertionError("spawn check "+checks);}
    public static final class Hybrid {
        public int calls;public SpawnReason reason;public boolean allowed=true;public boolean error;
        public boolean bridge$addEntity(Object entity,SpawnReason reason) {
            calls++;this.reason=reason;
            if(error)throw new IllegalStateException("plugin/server error");
            return allowed && reason==SpawnReason.CUSTOM;
        }
    }
    public static final class NonHybrid {}
    public static void main(String[] args) {
        Object npc=new Object();AtomicInteger fallback=new AtomicInteger();
        Hybrid h=new Hybrid();
        ok(CustomSpawnAdapter.add(h,npc,Object.class,()->{fallback.incrementAndGet();return false;}));
        ok(h.calls==1);ok(h.reason==SpawnReason.CUSTOM);ok(fallback.get()==0);
        h=new Hybrid();h.allowed=false;
        ok(!CustomSpawnAdapter.add(h,npc,Object.class,()->{fallback.incrementAndGet();return true;}));
        ok(h.calls==1);ok(fallback.get()==0); // Explicit cancellation is final.
        h=new Hybrid();h.error=true;
        try{CustomSpawnAdapter.add(h,npc,Object.class,()->{fallback.incrementAndGet();return true;});throw new AssertionError("missing error");}
        catch(IllegalStateException expected){ok(expected.getMessage().equals("plugin/server error"));}
        ok(h.calls==1);ok(fallback.get()==0); // Uncertain outcome is never replayed.
        ok(CustomSpawnAdapter.add(new NonHybrid(),npc,Object.class,()->{fallback.incrementAndGet();return true;}));
        ok(fallback.get()==1);
        ok(!CustomSpawnAdapter.add(new NonHybrid(),npc,Object.class,()->{fallback.incrementAndGet();return false;}));
        ok(fallback.get()==2);
        // Bootstrap-loaded java.lang.Object cannot see Bukkit: pure Forge fallback.
        ok(CustomSpawnAdapter.add(new Object(),npc,Object.class,()->{fallback.incrementAndGet();return true;}));
        ok(fallback.get()==3);
        System.out.println("CUSTOM NPC SPAWN TESTS PASSED: "+checks);
    }
}
