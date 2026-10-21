package de.nevergate.city;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
/** Real server world references; names and coordinates never choose a world. */
public final class StarterWorldAccess {
    public static StarterLedger login(MinecraftServer server,PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            return SharedStarter.ensureWorld(server,player.m_284548_());
        return SharedStarter.ensure(server);
    }
    public static StarterLedger command(MinecraftServer server,CommandContext<CommandSourceStack> context) {
        return SharedStarter.ensureWorld(server,context.getSource().m_81372_());
    }
    private StarterWorldAccess() {}
}
