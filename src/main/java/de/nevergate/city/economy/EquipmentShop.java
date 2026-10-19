package de.nevergate.city.economy;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;

/** Approved catalogue, one starter price per account and item. All validation precedes mutation. */
public final class EquipmentShop {
 public record Offer(Item item,long regularUnits){public String id(){return BuiltInRegistries.ITEM.getKey(item).toString();}}
 public static final List<Offer> OFFERS=List.of(
  new Offer(Items.IRON_HELMET,100),new Offer(Items.IRON_CHESTPLATE,160),new Offer(Items.IRON_LEGGINGS,140),new Offer(Items.IRON_BOOTS,80),
  new Offer(Items.IRON_SWORD,80),new Offer(Items.IRON_PICKAXE,120),new Offer(Items.IRON_AXE,120),new Offer(Items.IRON_SHOVEL,60),new Offer(Items.IRON_HOE,80),new Offer(Items.SHIELD,80));
 public static boolean first(EconomyData.Account account,Offer offer){return account==null||!account.starterPurchases.contains(offer.id());}
 public static long price(EconomyData.Account account,Offer offer){return first(account,offer)?50:offer.regularUnits();}
 public static String purchase(ServerPlayer p,int index,long displayedPrice,boolean cash,String pin){
  if(index<0||index>=OFFERS.size())return "Unbekannter Artikel.";
  var data=EconomyData.get(p.serverLevel());var account=data.accounts.get(p.getUUID());
  if(account==null)return "Bitte zuerst ein Konto eröffnen.";
  var offer=OFFERS.get(index);long cost=price(account,offer);
  if(cost!=displayedPrice)return "Der Preis hat sich geändert. Bitte den Artikel erneut auswählen.";
  Money.CashPlan cashPlan=null;List<ItemStack> inventory;
  if(cash){cashPlan=Money.planCash(p,cost);if(cashPlan==null)return "Nicht genug Bargeld oder kein Platz für Wechselgeld.";inventory=cashPlan.inventory();}
  else{if(!CardPin.verify(p,pin))return "Gültige Karte und richtige PIN erforderlich.";if(account.balance<cost)return "Nicht genug Kontoguthaben.";inventory=Money.inventory(p);}
  if(!Money.insert(inventory,List.of(new ItemStack(offer.item()))))return "Kein Platz für die Ausrüstung. Es wurde nichts bezahlt.";
  // Card debit uses the same central PIN-protected payment path as other services.
  if(!cash&&!Money.debit(p,cost,false,pin))return "Kartenzahlung abgelehnt.";
  account.starterPurchases.add(offer.id());
  if(cash)Money.applyCash(p,cashPlan);else Money.commit(p,inventory);
  data.flush(p.serverLevel());
  return "Gekauft: "+new ItemStack(offer.item()).getHoverName().getString()+" · "+CityConfig.money(cost)+(cash?" bar":" mit Karte");
 }
 private EquipmentShop(){}
}
