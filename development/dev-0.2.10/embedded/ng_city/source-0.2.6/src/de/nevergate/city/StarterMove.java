package de.nevergate.city;

/** Restartable move. A durable journal must exist before calling resume. */
public final class StarterMove {
    public enum State { AIR, RESERVED, CONTENTS, OTHER }
    public interface World {
        State source();
        State target();
        boolean reserve();
        boolean removeSource();
        void restoreTarget();
        void flush();
        void complete();
    }
    public static boolean resume(World world) {
        if (world.source()==State.OTHER || world.target()==State.OTHER) return false;
        if (world.target()==State.AIR && !world.reserve()) return false;
        if (world.source()!=State.AIR && !world.removeSource()) return false;
        if (world.target()!=State.CONTENTS) world.restoreTarget();
        if (world.source()!=State.AIR || world.target()!=State.CONTENTS) return false;
        // Persist both chunk changes before removing the recovery journal.
        world.flush();
        world.complete();
        return true;
    }
    private StarterMove() {}
}
