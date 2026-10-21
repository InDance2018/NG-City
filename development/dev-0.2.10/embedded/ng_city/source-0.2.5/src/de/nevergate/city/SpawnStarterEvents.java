package de.nevergate.city;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="ng_city")
public final class SpawnStarterEvents {
    private static int ticks;
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase==TickEvent.Phase.END && ++ticks%200==0) SharedStarter.ensure(event.getServer());
    }
}
