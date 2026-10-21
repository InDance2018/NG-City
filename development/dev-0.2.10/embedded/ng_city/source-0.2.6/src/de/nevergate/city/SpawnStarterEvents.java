package de.nevergate.city;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="ng_city")
public final class SpawnStarterEvents {
    private static int ticks;
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase!=TickEvent.Phase.END || ++ticks%200!=0) return;
        for (var level:event.getServer().m_129785_()) {
            if (!level.m_6907_().isEmpty() || StarterLedger.get(level).distributed()) SharedStarter.ensureWorld(event.getServer(),level);
        }
    }
}
