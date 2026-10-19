package de.nevergate.city.economy;
import de.nevergate.city.NgCity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import java.util.*;
public final class Banking {
 public static String open(ServerPlayer p){var d=EconomyData.get(p.serverLevel());if(d.accounts.containsKey(p.getUUID()))return "Du hast bereits ein Konto.";
  var inv=Money.inventory(p);var a=new EconomyData.Account();a.balance=CityConfig.START.get();a.name=p.getGameProfile().getName();d.accounts.put(p.getUUID(),a);
  if(!Money.insert(inv,List.of(Money.bound(NgCity.WALLET.get(),p),Money.bound(NgCity.CARD.get(),p)))){d.accounts.remove(p.getUUID());return "Bitte zwei Inventarplätze freimachen.";}
  Money.commit(p,inv);d.flush(p.serverLevel());return "Konto eröffnet · Guthaben "+CityConfig.money(a.balance);}
 public static String replace(ServerPlayer p,boolean card){var d=EconomyData.get(p.serverLevel());var a=d.accounts.get(p.getUUID());if(a==null)return "Bitte zuerst ein Konto eröffnen.";var inv=Money.inventory(p);if(inv.stream().noneMatch(ItemStack::isEmpty))return "Kein freier Inventarplatz.";
  if(card)a.card=UUID.randomUUID();Money.insert(inv,List.of(Money.bound(card?NgCity.CARD.get():NgCity.WALLET.get(),p)));Money.commit(p,inv);d.flush(p.serverLevel());return card?"Neue Karte ausgestellt; alte Karten sind ungültig.":"Geldbörse ersetzt; vorhandenes Bargeld in deiner Geldbörse bleibt erhalten.";}
 public static String withdraw(ServerPlayer p,long units){var d=EconomyData.get(p.serverLevel());var a=d.accounts.get(p.getUUID());if(a==null||!Money.card(p))return "Deine gültige Kreditkarte muss im Inventar sein.";if(units<=0||a.balance<units)return "Nicht genug Kontoguthaben.";var inv=Money.inventory(p);if(!Money.insert(inv,Money.coins(units)))return "Nicht genug Platz für das Bargeld.";a.balance-=units;Money.commit(p,inv);d.flush(p.serverLevel());return "Abgehoben: "+CityConfig.money(units);}
 public static String deposit(ServerPlayer p){var d=EconomyData.get(p.serverLevel());var a=d.accounts.get(p.getUUID());if(a==null||!Money.card(p))return "Deine gültige Kreditkarte fehlt.";var inv=Money.inventory(p);long units=Money.total(inv);if(units==0)return "Kein loses Bargeld im Inventar. Nimm es gegebenenfalls aus der Geldbörse.";for(int i=0;i<inv.size();i++)if(Money.value(inv.get(i))>0)inv.set(i,ItemStack.EMPTY);a.balance+=units;Money.commit(p,inv);d.flush(p.serverLevel());return "Eingezahlt: "+CityConfig.money(units);}
 public static String sell(ServerPlayer p,ItemStack selected,boolean cash){var l=p.serverLevel();var d=EconomyData.get(l);var a=d.accounts.get(p.getUUID());if(a==null)return "Bitte zuerst ein Konto eröffnen.";var price=BuyPrices.quote(l,selected);if(price==null)return "Diese Ware wird nicht angenommen (nur unveränderte Vanilla-Waren).";
  long now=System.currentTimeMillis();a.sales.removeIf(r->r.time()<=now-EconomyData.REAL_HOUR);long remaining=Math.max(0,CityConfig.LIMIT.get()-a.sold(now));var inv=Money.inventory(p);int available=inv.stream().filter(s->ItemStack.isSameItemSameTags(s,selected)).mapToInt(ItemStack::getCount).sum();long batches=Math.min(available/price.quantity(),remaining/price.units());if(batches<=0)return "Mindestmenge fehlt oder dein Stundenlimit ist erreicht.";
  int remove=(int)(batches*price.quantity());long payout=batches*price.units();for(var s:inv)if(ItemStack.isSameItemSameTags(s,selected)){int n=Math.min(remove,s.getCount());s.shrink(n);remove-=n;}
  if(cash&&!Money.insert(inv,Money.coins(payout)))return "Nicht genug Platz für die Barauszahlung. Waren bleiben erhalten.";
  if(!cash)a.balance+=payout;a.sales.add(new EconomyData.Receipt(now,payout));Money.commit(p,inv);d.flush(l);return "Verkauft: "+(batches*price.quantity())+" Stück · "+CityConfig.money(payout)+(cash?" bar":" aufs Konto");
 }
}
