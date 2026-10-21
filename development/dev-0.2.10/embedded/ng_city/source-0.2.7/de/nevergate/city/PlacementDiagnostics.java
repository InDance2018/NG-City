package de.nevergate.city;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Diagnostics only. Never permits a rejected placement or changes protection decisions. */
public final class PlacementDiagnostics {
    private static final Logger LOG=LoggerFactory.getLogger("ng_city.placement");
    private static final ThreadLocal<State> CURRENT=new ThreadLocal<>();
    private static final class State { String stage="Bauplatz prüfen",reason; }
    public static void begin(UseOnContext context) {
        CURRENT.set(new State());
        if(context.m_43723_() instanceof ServerPlayer player)
            player.m_213846_(Component.m_237113_("NG-City: Bauanfrage empfangen. Der Server prüft den Bauplatz."));
    }
    public static void stage(String value) { State state=CURRENT.get(); if(state!=null)state.stage=value; }
    public static String validation(String value) { if(value!=null) reason(value); return value; }
    public static void reason(String value) { State state=CURRENT.get();if(state!=null)state.reason=value; }
    public static boolean protectedEvent(boolean cancelled) {
        if(cancelled) reason("Die serverseitige Bau-/Schutzprüfung hat das Setzen abgelehnt. Die Vorschau prüft diese Freigabe nicht.");
        return cancelled;
    }
    public static void exception(Throwable error) {
        State state=CURRENT.get();
        reason("Fehler bei „"+(state==null?"Platzierung":state.stage)+"“: "+error.getClass().getSimpleName()+
            (error.getMessage()==null?"":" – "+error.getMessage()));
        LOG.error("NG-City complex placement failed during {}",state==null?"unknown":state.stage,error);
    }
    public static InteractionResult finish(InteractionResult result,UseOnContext context) {
        State state=CURRENT.get();
        try {
            if(result==InteractionResult.FAIL && context.m_43723_() instanceof ServerPlayer player) {
                String message=state!=null&&state.reason!=null?state.reason:
                    "Platzierung abgebrochen bei „"+(state==null?"Serverprüfung":state.stage)+"“.";
                player.m_213846_(Component.m_237113_("NG-City: "+message));
                LOG.warn("NG-City rejected complex placement in {} at {}: {}",context.m_43725_().m_46472_().m_135782_(),context.m_8083_(),message);
            }
            return result;
        } finally { CURRENT.remove(); }
    }
    public static InteractionResult failed(RuntimeException error,UseOnContext context) {
        exception(error); return finish(InteractionResult.FAIL,context);
    }
    private PlacementDiagnostics() {}
}
