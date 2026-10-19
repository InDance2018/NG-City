package de.nevergate.city.economy;
import java.util.*;
import de.nevergate.city.NgCity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
public final class Money {
 public static final int[] VALUES={500,200,100,20,10,5,2,1};
 public static final String[] IDS={"schein_50","schein_20","schein_10","muenze_2","muenze_1","muenze_50_cent","muenze_20_cent","muenze_10_cent"};
 public static int value(ItemStack s){for(int i=0;i<IDS.length;i++)if(s.is(NgCity.CASH.get(i).get()))return VALUES[i];return 0;}
 public static ItemStack bound(Item item,ServerPlayer p){var s=new ItemStack(item);s.getOrCreateTag().putUUID("Owner",p.getUUID());s.setHoverName(Component.literal(item==NgCity.CARD.get()?"NG-Kreditkarte · "+p.getGameProfile().getName():"NG-Geldbörse · "+p.getGameProfile().getName()));if(item==NgCity.CARD.get())s.getOrCreateTag().putUUID("Card",EconomyData.get(p.serverLevel()).accounts.get(p.getUUID()).card);return s;}
 public static boolean owned(ItemStack s,UUID owner){return s.hasTag()&&s.getTag().hasUUID("Owner")&&s.getTag().getUUID("Owner").equals(owner);}
 public static boolean has(ServerPlayer p,Item item){for(var s:java.util.stream.Stream.concat(p.getInventory().items.stream(),p.getInventory().offhand.stream()).toList())if(s.is(item)&&owned(s,p.getUUID()))return true;return false;}
 public static ItemStack cardStack(ServerPlayer p){var a=EconomyData.get(p.serverLevel()).accounts.get(p.getUUID());if(a==null)return ItemStack.EMPTY;for(var s:java.util.stream.Stream.concat(p.getInventory().items.stream(),p.getInventory().offhand.stream()).toList())if(s.is(NgCity.CARD.get())&&owned(s,p.getUUID())&&s.getTag().hasUUID("Card")&&s.getTag().getUUID("Card").equals(a.card))return s;return ItemStack.EMPTY;}
 public static boolean card(ServerPlayer p){return !cardStack(p).isEmpty();}
 public static List<ItemStack> coins(long units){var list=new ArrayList<ItemStack>();for(int i=0;i<VALUES.length;i++){long n=units/VALUES[i];units%=VALUES[i];while(n>0){int count=(int)Math.min(64,n);list.add(new ItemStack(NgCity.CASH.get(i).get(),count));n-=count;}}return list;}
 public static List<ItemStack> inventory(ServerPlayer p){var out=new ArrayList<ItemStack>();for(var s:p.getInventory().items)out.add(s.copy());return out;}
 public static boolean insert(List<ItemStack> slots,List<ItemStack> incoming){for(var stack:incoming){var s=stack.copy();for(var old:slots)if(!old.isEmpty()&&ItemStack.isSameItemSameTags(old,s)){int n=Math.min(s.getCount(),old.getMaxStackSize()-old.getCount());old.grow(n);s.shrink(n);}for(int i=0;i<slots.size()&&!s.isEmpty();i++)if(slots.get(i).isEmpty()){int n=Math.min(s.getCount(),s.getMaxStackSize());slots.set(i,s.copyWithCount(n));s.shrink(n);}if(!s.isEmpty())return false;}return true;}
 public static void commit(ServerPlayer p,List<ItemStack> inv){for(int i=0;i<inv.size();i++)p.getInventory().setItem(i,inv.get(i));p.getInventory().setChanged();p.containerMenu.broadcastChanges();}
 public static long total(List<ItemStack> slots){return slots.stream().mapToLong(s->(long)value(s)*s.getCount()).sum();}
 /** Simulate change and capacity before removing a single coin. */
 public record CashPlan(List<ItemStack> inventory,List<ItemStack> wallet){}
 public static CashPlan planCash(ServerPlayer p,long cost){var d=EconomyData.get(p.serverLevel());var a=d.accounts.get(p.getUUID());var inv=inventory(p);var wallet=new ArrayList<ItemStack>();if(a!=null&&has(p,NgCity.WALLET.get()))for(var s:a.wallet)wallet.add(s.copy());long total=total(inv)+total(wallet);if(cost<0||total<cost)return null;
  for(int i=0;i<inv.size();i++)if(value(inv.get(i))>0)inv.set(i,ItemStack.EMPTY);for(int i=0;i<wallet.size();i++)if(value(wallet.get(i))>0)wallet.set(i,ItemStack.EMPTY);
  var change=coins(total-cost);if(!insert(wallet.isEmpty()?inv:wallet,change))return null;return new CashPlan(inv,wallet);
 }
 public static void applyCash(ServerPlayer p,CashPlan plan){commit(p,plan.inventory());if(!plan.wallet().isEmpty()){var a=EconomyData.get(p.serverLevel()).accounts.get(p.getUUID());a.wallet.clear();a.wallet.addAll(plan.wallet());}}
 public static boolean payCash(ServerPlayer p,long cost,boolean apply){var plan=planCash(p,cost);if(plan==null)return false;if(apply){applyCash(p,plan);EconomyData.get(p.serverLevel()).flush(p.serverLevel());}return true;}
 public static boolean give(ServerPlayer p,long units){var inv=inventory(p);if(!insert(inv,coins(units)))return false;commit(p,inv);return true;}
 public static boolean debit(ServerPlayer p,long units,boolean cash){return debit(p,units,cash,null);}
 public static boolean debit(ServerPlayer p,long units,boolean cash,String pin){var d=EconomyData.get(p.serverLevel());var a=d.accounts.get(p.getUUID());if(cash)return payCash(p,units,true);if(a==null||!card(p)||!CardPin.verify(p,pin)||a.balance<units||units<0)return false;a.balance-=units;d.flush(p.serverLevel());return true;}
}
