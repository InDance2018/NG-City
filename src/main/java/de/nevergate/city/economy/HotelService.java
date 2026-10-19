package de.nevergate.city.economy;
import de.nevergate.city.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
public final class HotelService {
 public static EconomyData.Hotel ensure(Villager npc){if(!(npc.level() instanceof ServerLevel l))return null;var n=npc.getPersistentData();if(!n.getString("ng_city_role").equals("hotel"))return null;var d=EconomyData.get(l);UUID id=n.hasUUID("ng_city_hotel")?n.getUUID("ng_city_hotel"):npc.getUUID();var h=d.hotels.get(id);
  if(h==null){h=new EconomyData.Hotel();h.id=id;h.dimension=l.dimension().location().toString();Direction dir=Placement.employeeDirection(npc);h.direction=dir.get2DDataValue();h.origin=BlockPos.of(n.getLong("ng_city_anchor")).below().relative(dir.getOpposite(),5).asLong();var candidate=new BuildingPlan(BuildingKind.HOTEL);var test=candidate.worldPos(BlockPos.of(h.origin),dir,new BlockPos(6,6,7));h.layout=l.getBlockState(test).is(CityBlocks.ROOM_DOOR.get())?(l.getBlockState(candidate.worldPos(BlockPos.of(h.origin),dir,new BlockPos(10,1,2))).is(Blocks.OAK_STAIRS)?3:2):1;d.hotels.put(id,h);n.putUUID("ng_city_hotel",id);n.putInt("ng_city_direction",h.direction);d.setDirty();}
  install(l,h);return h;
 }
 public static BlockPos pos(EconomyData.Hotel h,int x,int y,int z){return plan(h).worldPos(BlockPos.of(h.origin),Direction.from2DDataValue(h.direction),new BlockPos(x,y,z));}
 public static BuildingPlan plan(EconomyData.Hotel h){return new BuildingPlan(BuildingKind.HOTEL,h.layout);}
 public static AABB roomBounds(EconomyData.Hotel h,int room){var a=pos(h,room==0?4:(h.layout>=2?11:8),6,8);var b=pos(h,room==0?(h.layout>=2?9:6):(h.layout>=2?17:11),9,h.layout>=2?17:11);return new AABB(a).minmax(new AABB(b));}
 public static BlockPos door(EconomyData.Hotel h,int room){return pos(h,h.layout>=2?(room==0?6:14):(room==0?5:9),6,7);}
 public static void install(ServerLevel l,EconomyData.Hotel h){if(h.installed)return;var bp=plan(h);var transform=bp.transformed(BlockPos.of(h.origin),Direction.from2DDataValue(h.direction));if(transform.keySet().stream().anyMatch(p->!l.hasChunkAt(p)))return;
  // Migrate only a recognized legacy interior. Refuse occupied or modified cells; never overwrite belongings.
  var oldPlan=new BuildingPlan(BuildingKind.HOTEL,0).transformed(BlockPos.of(h.origin),Direction.from2DDataValue(h.direction));
  boolean legacy=l.getBlockState(pos(h,5,6,4)).is(Blocks.OAK_DOOR);
  if(legacy){for(var e:transform.entrySet())if(!e.getValue().equals(oldPlan.get(e.getKey()))){var actual=l.getBlockState(e.getKey());var expected=oldPlan.get(e.getKey());if(actual.getBlock() instanceof DoorBlock&&actual.getBlock()==expected.getBlock())actual=actual.setValue(DoorBlock.OPEN,expected.getValue(DoorBlock.OPEN)).setValue(DoorBlock.POWERED,expected.getValue(DoorBlock.POWERED));if(!actual.equals(expected)||l.getBlockEntity(e.getKey()) instanceof Container c&&!c.isEmpty())return;}
   for(var e:transform.entrySet())if(!e.getValue().equals(oldPlan.get(e.getKey())))l.setBlock(e.getKey(),e.getValue(),2);
  }
  if(!legacy)for(int r=0;r<2;r++)for(int y=0;y<2;y++)if(!l.getBlockState(door(h,r).above(y)).is(CityBlocks.ROOM_DOOR.get()))return;
  for(int r=0;r<2;r++)for(int y=0;y<2;y++){var p=door(h,r).above(y);var old=l.getBlockState(p);if(old.getBlock()==Blocks.OAK_DOOR||old.getBlock()==CityBlocks.ROOM_DOOR.get())l.setBlock(p,CityBlocks.ROOM_DOOR.get().defaultBlockState().setValue(DoorBlock.FACING,old.getValue(DoorBlock.FACING)).setValue(DoorBlock.HALF,old.getValue(DoorBlock.HALF)),2);}
  var display=pos(h,6,2,7);if(l.getBlockState(display).isAir()||l.getBlockState(display).is(CityBlocks.DISPLAY.get())){l.setBlock(display,CityBlocks.DISPLAY.get().defaultBlockState().setValue(CityBlocks.FACING,Direction.from2DDataValue(h.direction).getOpposite()),3);if(l.getBlockEntity(display) instanceof HotelDisplayEntity be){be.hotel=h.id;be.setChanged();}}
  var terminal=pos(h,h.layout>=2?10:7,2,4);if(l.getBlockState(terminal).is(Blocks.FLOWER_POT)||l.getBlockState(terminal).is(CityBlocks.TERMINAL.get()))l.setBlock(terminal,CityBlocks.TERMINAL.get().defaultBlockState().setValue(CityBlocks.FACING,Direction.from2DDataValue(h.direction).getOpposite()),3);
  var luggage=pos(h,3,1,7);if(l.getBlockState(luggage).isAir())l.setBlock(luggage,CityBlocks.LUGGAGE.get().defaultBlockState().setValue(CityBlocks.FACING,Direction.from2DDataValue(h.direction).getOpposite()),3);
  h.installed=true;EconomyData.get(l).setDirty();
 }
 public static String book(ServerPlayer p,EconomyData.Hotel h,int room,int days,boolean cash){return book(p,h,room,days,cash,null);}
 public static String book(ServerPlayer p,EconomyData.Hotel h,int room,int days,boolean cash,String pin){tick(p.serverLevel(),h);if(!h.installed)return "Hotelumbau blockiert: Bitte Änderungen an der ursprünglichen Zimmereinteilung entfernen.";if(room<0||room>1||days<1||days>7)return "Buchung ungültig.";var r=h.rooms[room];if(r.owner!=null)return "Dieses Zimmer ist bereits belegt.";
  long cost=(long)days*CityConfig.HOTEL.get();var d=EconomyData.get(p.serverLevel());var account=d.accounts.get(p.getUUID());var token=UUID.randomUUID();var key=keyStack(p,h,room,token);
  var plan=cash?Money.planCash(p,cost):null;if(cash&&plan==null)return "Zahlung abgelehnt: Bargeld oder Wechselgeldplatz fehlen.";
  if(!cash&&!CardPin.verify(p,pin))return "Kartenzahlung abgelehnt: Karten-PIN fehlt oder ist falsch. PIN zuerst am Geldautomaten festlegen.";
  if(!cash&&(account==null||!Money.card(p)||account.balance<cost))return "Kartenzahlung abgelehnt: Karte oder Kontoguthaben fehlen.";
  var inv=cash?plan.inventory():Money.inventory(p);if(!Money.insert(inv,List.of(key)))return "Bitte Platz für Schlüssel und gegebenenfalls Wechselgeld freimachen. Nichts wurde berechnet.";
  if(cash)Money.applyCash(p,new Money.CashPlan(inv,plan.wallet()));else{account.balance-=cost;Money.commit(p,inv);}
  r.owner=p.getUUID();r.name=p.getGameProfile().getName();r.key=token;r.until=p.serverLevel().getDayTime()+days*EconomyData.DAY;d.flush(p.serverLevel());updateDisplay(p.serverLevel(),h);return "Zimmer "+(room+1)+" für "+days+" Tage bezahlt · "+CityConfig.money(cost);}
 private static ItemStack keyStack(ServerPlayer p,EconomyData.Hotel h,int room,UUID token){var s=new ItemStack(NgCity.KEY.get());var t=s.getOrCreateTag();t.putUUID("Owner",p.getUUID());t.putUUID("Hotel",h.id);t.putUUID("Key",token);t.putInt("Room",room);s.setHoverName(Component.literal("NG-Zimmerschlüssel · Zimmer "+(room+1)+" · "+p.getGameProfile().getName()));return s;}
 public static boolean giveKey(ServerPlayer p,EconomyData.Hotel h,int room){var r=h.rooms[room];return r.owner!=null&&r.owner.equals(p.getUUID())&&p.getInventory().add(keyStack(p,h,room,r.key));}
 public static boolean key(ServerPlayer p,EconomyData.Hotel h,int room){var r=h.rooms[room];if(r.owner==null||!r.owner.equals(p.getUUID())||p.serverLevel().getDayTime()>=r.until)return false;for(var s:List.of(p.getMainHandItem(),p.getOffhandItem()))if(s.is(NgCity.KEY.get())&&Money.owned(s,p.getUUID())){var t=s.getTag();if(t.hasUUID("Hotel")&&t.getUUID("Hotel").equals(h.id)&&t.hasUUID("Key")&&t.getUUID("Key").equals(r.key)&&t.getInt("Room")==room)return true;}return false;}
 public static boolean busy(EconomyData.Hotel h){for(var r:h.rooms)if(r.owner!=null||!r.stored.isEmpty())return true;return false;}
 public static void tick(ServerLevel l,EconomyData.Hotel h){if(!h.dimension.equals(l.dimension().location().toString()))return;install(l,h);long now=l.getDayTime();boolean changed=false;
  for(int i=0;i<2;i++){var door=door(h,i);if(l.hasChunkAt(door)){var state=l.getBlockState(door);if(state.getBlock() instanceof DoorBlock&&state.getValue(DoorBlock.OPEN)&&!HotelEvents.closeAt.containsKey(new HotelEvents.DoorAddress(l.dimension(),door)))close(l,door);}}
  for(int ri=0;ri<2;ri++){var r=h.rooms[ri];if(r.owner!=null&&now>=r.until){if(!expire(l,h,ri))continue;changed=true;}if(r.stored.removeIf(s->now-s.since>=7*EconomyData.DAY))changed=true;}if(changed){EconomyData.get(l).flush(l);updateDisplay(l,h);}}
 private static boolean expire(ServerLevel l,EconomyData.Hotel h,int room){var bounds=roomBounds(h,room);var bp=plan(h);var blocks=bp.transformed(BlockPos.of(h.origin),Direction.from2DDataValue(h.direction));for(var p:blocks.keySet())if(!l.hasChunkAt(p))return false;var r=h.rooms[room];var bag=new EconomyData.Stored();bag.owner=r.owner;bag.since=r.until;
  for(var e:l.getEntities((Entity)null,bounds,Entity::isAlive)){if(e instanceof ServerPlayer player){var lobby=pos(h,6,1,2);player.closeContainer();player.teleportTo(lobby.getX()+.5,lobby.getY(),lobby.getZ()+.5);player.sendSystemMessage(Component.literal("Deine Zimmerbuchung ist abgelaufen."));}else if(e instanceof ItemEntity item){bag.items.add(item.getItem().copy());item.discard();}else if(e instanceof net.minecraft.world.entity.decoration.ItemFrame frame){if(!frame.getItem().isEmpty())bag.items.add(frame.getItem().copy());bag.items.add(new ItemStack(Items.ITEM_FRAME));frame.discard();}else if(e instanceof net.minecraft.world.entity.decoration.ArmorStand stand){for(var item:stand.getAllSlots())if(!item.isEmpty())bag.items.add(item.copy());bag.items.add(new ItemStack(Items.ARMOR_STAND));stand.discard();}}
  for(var e:blocks.entrySet()){var p=e.getKey();if(!bounds.contains(p.getX()+.5,p.getY()+.5,p.getZ()+.5))continue;var actual=l.getBlockState(p);if(l.getBlockEntity(p) instanceof Container c){for(int i=0;i<c.getContainerSize();i++)if(!c.getItem(i).isEmpty()){bag.items.add(c.getItem(i).copy());c.setItem(i,ItemStack.EMPTY);}}
   if(actual.getBlock()!=e.getValue().getBlock()){Item item=actual.getBlock().asItem();if(item!=Items.AIR&&!(actual.getBlock() instanceof DoorBlock&&actual.getValue(DoorBlock.HALF)==net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER)&&!(actual.getBlock() instanceof BedBlock&&actual.getValue(BedBlock.PART)==net.minecraft.world.level.block.state.properties.BedPart.HEAD)){int count=actual.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.SLAB_TYPE)&&actual.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.SLAB_TYPE)==net.minecraft.world.level.block.state.properties.SlabType.DOUBLE?2:1;bag.items.add(new ItemStack(item,count));}l.setBlock(p,e.getValue(),2);}}
  if(!bag.items.isEmpty())r.stored.add(bag);r.owner=null;r.key=null;r.name="";r.until=0;close(l,door(h,room));return true;
 }
 public static void close(ServerLevel l,BlockPos p){for(int y=0;y<2;y++){var s=l.getBlockState(p.above(y));if(s.getBlock() instanceof DoorBlock)l.setBlock(p.above(y),s.setValue(DoorBlock.OPEN,false),3);}}
 public static long fee(ServerLevel l,EconomyData.Stored s){long days=Math.max(1,(l.getDayTime()-s.since+EconomyData.DAY-1)/EconomyData.DAY);return days*CityConfig.STORAGE.get();}
 public static String reclaim(ServerPlayer p,EconomyData.Hotel h,boolean discard,boolean cash){return reclaim(p,h,discard,cash,null);}
 public static String reclaim(ServerPlayer p,EconomyData.Hotel h,boolean discard,boolean cash,String pin){tick(p.serverLevel(),h);var bags=new ArrayList<EconomyData.Stored>();for(var r:h.rooms)for(var s:r.stored)if(s.owner.equals(p.getUUID()))bags.add(s);if(bags.isEmpty())return "Keine zurückgelassenen Gegenstände vorhanden.";
  if(discard){for(var r:h.rooms)r.stored.removeAll(bags);EconomyData.get(p.serverLevel()).flush(p.serverLevel());return "Gegenstände endgültig vernichtet.";}
  var incoming=new ArrayList<ItemStack>();long cost=0;for(var bag:bags){incoming.addAll(bag.items);cost+=fee(p.serverLevel(),bag);}
  Money.CashPlan cashPlan=cash?Money.planCash(p,cost):null;
  if(cash&&cashPlan==null)return "Bargeld reicht nicht oder kein Platz für Wechselgeld.";
  var d=EconomyData.get(p.serverLevel());var account=d.accounts.get(p.getUUID());
  if(!cash&&!CardPin.verify(p,pin))return "Kartenzahlung abgelehnt: Karten-PIN fehlt oder ist falsch.";
  if(!cash&&(account==null||!Money.card(p)||account.balance<cost))return "Kartenzahlung abgelehnt.";
  var inv=cash?cashPlan.inventory():Money.inventory(p);
  if(!Money.insert(inv,incoming))return "Nicht genug Platz. Bitte Inventar leeren; nichts wurde berechnet.";
  if(cash)Money.applyCash(p,new Money.CashPlan(inv,cashPlan.wallet()));else{account.balance-=cost;Money.commit(p,inv);}
  for(var r:h.rooms)r.stored.removeAll(bags);d.flush(p.serverLevel());return "Gegenstände abgeholt · "+CityConfig.money(cost);

 }
 public static void updateDisplay(ServerLevel l,EconomyData.Hotel h){var p=pos(h,6,2,7);if(l.getBlockEntity(p) instanceof HotelDisplayEntity be){be.hotel=h.id;be.refresh();}}
}
