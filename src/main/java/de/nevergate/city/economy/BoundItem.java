package de.nevergate.city.economy;
import de.nevergate.city.NgCity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
public final class BoundItem extends Item {
 public BoundItem(){super(new Properties().stacksTo(1));}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var s=player.getItemInHand(hand);if(!level.isClientSide&&player instanceof ServerPlayer p){if(!Money.owned(s,p.getUUID()))p.displayClientMessage(Component.literal("An einen anderen Spieler gebunden."),true);else if(s.is(NgCity.WALLET.get()))WalletMenu.open(p);else {var a=EconomyData.get(p.serverLevel()).accounts.get(p.getUUID());if(a!=null)p.sendSystemMessage(Component.literal("Kontostand: "+CityConfig.money(a.balance)));}}return InteractionResultHolder.sidedSuccess(s,level.isClientSide);}
}
