package de.nevergate.city.economy;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
/** Logical server only. Units are exact integer multiples of ten cents. */
public final class EconomyData extends SavedData {
 public static final long DAY=24000L, REAL_HOUR=3600000L;
 public static final class Account {
  public long balance; public UUID card=UUID.randomUUID(); public String name="";
  public final Set<String> starterPurchases=new HashSet<>();
  public final List<Receipt> sales=new ArrayList<>(); public final List<ItemStack> wallet=new ArrayList<>();
  public Account(){for(int i=0;i<54;i++)wallet.add(ItemStack.EMPTY);}
  public long sold(long now){return sales.stream().filter(r->r.time>now-REAL_HOUR).mapToLong(Receipt::amount).sum();}
 }
 public record Receipt(long time,long amount){}
 public static final class Room {
  public UUID owner, key; public String name=""; public long until;
  public final List<Stored> stored=new ArrayList<>();
 }
 public static final class Stored { public UUID owner; public long since; public final List<ItemStack> items=new ArrayList<>(); }
 public static final class Hotel {
  public UUID id; public String dimension; public long origin; public int direction; public int layout=1; public boolean installed;
  public final Room[] rooms={new Room(),new Room()};
 }
 public final Map<UUID,Account> accounts=new HashMap<>();
 public final Map<UUID,Hotel> hotels=new HashMap<>();
 public Account open(UUID id,String name,long start){return accounts.computeIfAbsent(id,k->{var a=new Account();a.name=name;a.balance=start;setDirty();return a;});}
 public static EconomyData get(ServerLevel l){return l.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(EconomyData::new,EconomyData::load,net.minecraft.util.datafix.DataFixTypes.LEVEL),"ng_city_economy");}
 public void flush(ServerLevel l){setDirty();l.getServer().overworld().getDataStorage().save();}
 private static ListTag items(List<ItemStack> items){var out=new ListTag();for(var s:items)out.add(s.save(new CompoundTag()));return out;}
 private static void readItems(ListTag tags,List<ItemStack> out){out.clear();for(var t:tags)out.add(ItemStack.of((CompoundTag)t));}
 public static EconomyData load(CompoundTag root){var d=new EconomyData();
  for(var tag:root.getList("Accounts",10)){var t=(CompoundTag)tag;var a=new Account();a.balance=Math.max(0,t.getLong("Balance"));a.card=t.getUUID("Card");a.name=t.getString("Name");readItems(t.getList("Wallet",10),a.wallet);while(a.wallet.size()<54)a.wallet.add(ItemStack.EMPTY);
   for(var bought:t.getList("StarterPurchases",8))a.starterPurchases.add(bought.getAsString());
   for(var rt:t.getList("Sales",10)){var r=(CompoundTag)rt;a.sales.add(new Receipt(r.getLong("Time"),r.getLong("Amount")));}d.accounts.put(t.getUUID("Owner"),a);}
  for(var tag:root.getList("Hotels",10)){var t=(CompoundTag)tag;var h=new Hotel();h.id=t.getUUID("Id");h.dimension=t.getString("Dimension");h.origin=t.getLong("Origin");h.direction=t.getInt("Direction");h.layout=t.contains("Layout")?t.getInt("Layout"):1;h.installed=t.getBoolean("Installed");var rooms=t.getList("Rooms",10);
   for(int i=0;i<Math.min(2,rooms.size());i++){var r=rooms.getCompound(i);var room=h.rooms[i];if(r.hasUUID("Owner")){room.owner=r.getUUID("Owner");room.key=r.getUUID("Key");}room.name=r.getString("Name");room.until=r.getLong("Until");
    for(var st:r.getList("Stored",10)){var s=(CompoundTag)st;var bag=new Stored();bag.owner=s.getUUID("Owner");bag.since=s.getLong("Since");readItems(s.getList("Items",10),bag.items);room.stored.add(bag);}}
   d.hotels.put(h.id,h);}
  return d;
 }
 @Override public CompoundTag save(CompoundTag root){var accountsTag=new ListTag();accounts.forEach((id,a)->{var t=new CompoundTag();t.putUUID("Owner",id);t.putUUID("Card",a.card);t.putString("Name",a.name);t.putLong("Balance",a.balance);t.put("Wallet",items(a.wallet));var bought=new ListTag();a.starterPurchases.stream().sorted().forEach(item->bought.add(StringTag.valueOf(item)));t.put("StarterPurchases",bought);var sales=new ListTag();for(var r:a.sales){var s=new CompoundTag();s.putLong("Time",r.time);s.putLong("Amount",r.amount);sales.add(s);}t.put("Sales",sales);accountsTag.add(t);});root.put("Accounts",accountsTag);
  var hs=new ListTag();for(var h:hotels.values()){var t=new CompoundTag();t.putUUID("Id",h.id);t.putString("Dimension",h.dimension);t.putLong("Origin",h.origin);t.putInt("Direction",h.direction);t.putInt("Layout",h.layout);t.putBoolean("Installed",h.installed);var rs=new ListTag();for(var r:h.rooms){var rt=new CompoundTag();if(r.owner!=null){rt.putUUID("Owner",r.owner);rt.putUUID("Key",r.key);}rt.putString("Name",r.name);rt.putLong("Until",r.until);var bags=new ListTag();for(var s:r.stored){var st=new CompoundTag();st.putUUID("Owner",s.owner);st.putLong("Since",s.since);st.put("Items",items(s.items));bags.add(st);}rt.put("Stored",bags);rs.add(rt);}t.put("Rooms",rs);hs.add(t);}root.put("Hotels",hs);return root;
 }
}
