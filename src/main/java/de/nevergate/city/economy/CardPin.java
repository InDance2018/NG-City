package de.nevergate.city.economy;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
/** Four-digit in-game card PIN. Leading zeroes are retained on the actual card item. */
public final class CardPin {
 private CardPin(){}
 public static boolean format(String pin){return pin!=null&&pin.matches("[0-9]{4}");}
 public static boolean has(ItemStack card){return !card.isEmpty()&&card.hasTag()&&format(card.getTag().getString("Pin"));}
 public static boolean verify(ServerPlayer player,String pin){
  var card=Money.cardStack(player);return format(pin)&&has(card)&&card.getTag().getString("Pin").equals(pin);
 }
 public static boolean initialise(ServerPlayer player,String pin){
  var card=Money.cardStack(player);if(card.isEmpty()||has(card)||!format(pin))return false;
  card.getOrCreateTag().putString("Pin",pin);player.getInventory().setChanged();return true;
 }
 public static final class Session {
  private UUID cardId;private String pin;
  public boolean valid(ServerPlayer player){
   var card=Money.cardStack(player);
   return cardId!=null&&has(card)&&card.getTag().getUUID("Card").equals(cardId)&&card.getTag().getString("Pin").equals(pin);
  }
  public boolean unlock(ServerPlayer player,String entered){
   cardId=null;pin=null;var card=Money.cardStack(player);
   if(!format(entered)||!has(card)||!card.getTag().getString("Pin").equals(entered))return false;
   cardId=card.getTag().getUUID("Card");pin=entered;return true;
  }
 }
}
