package de.nevergate.city;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
/** Kept registered for old saves; personal crate items can no longer duplicate starter sets. */
public final class StarterCrateItem extends Item {
 public StarterCrateItem(Properties props){super(props);}
 @Override public InteractionResult useOn(UseOnContext ctx){
  if(ctx.getPlayer() instanceof ServerPlayer p)p.sendSystemMessage(Component.literal(SharedStarter.message(StarterLedger.get(p.serverLevel()))));
  return InteractionResult.FAIL;
 }
}
