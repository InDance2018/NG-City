package de.nevergate.city;
import de.nevergate.city.economy.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;
import net.minecraftforge.gametest.GameTestHolder;
@GameTestHolder(value="ng_city",namespace="ng_city")
public final class CardPinGameTests {
 static void ok(GameTestHelper h,boolean x,String message){h.assertTrue(x,message);}
 // Mock players skip the real login handshake; Forge custom-menu packets require PLAY codecs.
 static void playProtocol(ServerPlayer p){var channel=p.connection.getConnection().channel();channel.attr(net.minecraft.network.Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(net.minecraft.network.ConnectionProtocol.PLAY.codec(net.minecraft.network.protocol.PacketFlow.CLIENTBOUND));channel.attr(net.minecraft.network.Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(net.minecraft.network.ConnectionProtocol.PLAY.codec(net.minecraft.network.protocol.PacketFlow.SERVERBOUND));}
 static void click(ServerPlayer p,int slot){p.containerMenu.clicked(slot,0,ClickType.PICKUP,p);}
 static void pin(ServerPlayer p,String value){for(char c:value.toCharArray()){int d=c-'0';click(p,d==0?40:12+((d-1)/3)*9+(d-1)%3);}click(p,41);}
 @GameTest(batch="pin",template="ng_city:empty",timeoutTicks=100) public static void pinIsFourDigitsBoundAndStoredOnCard(GameTestHelper h){
  var p=EconomyGameTests.player(h);Banking.open(p);ok(h,!CardPin.has(Money.cardStack(p)),"New card already has PIN");
  ok(h,!CardPin.initialise(p,"123")&&!CardPin.initialise(p,"12345")&&!CardPin.initialise(p,"abcd"),"Invalid PIN accepted");
  ok(h,CardPin.initialise(p,"0427")&&!CardPin.initialise(p,"1234"),"PIN setup or overwrite incorrect");
  var restored=ItemStack.of(Money.cardStack(p).save(new net.minecraft.nbt.CompoundTag()));ok(h,restored.getTag().getString("Pin").equals("0427"),"Card save lost leading zero");
  var other=EconomyGameTests.player(h);other.getInventory().add(restored);ok(h,!CardPin.verify(other,"0427")&&!CardPin.initialise(other,"1234"),"Foreign card PIN accepted");h.succeed();
 }
 @GameTest(batch="pin",template="ng_city:empty",timeoutTicks=100) public static void atmRequiresPinEveryVisitAndRejectsStaleClicks(GameTestHelper h){
  var pos=h.absolutePos(new BlockPos(8,3,8));h.getLevel().setBlock(pos,CityBlocks.ATM.get().defaultBlockState(),3);
  var p=CityGameTests.player(h,pos.north(2),Direction.SOUTH);Banking.open(p);var a=EconomyData.get(h.getLevel()).accounts.get(p.getUUID());
  playProtocol(p);ServiceMenu.openDevice(p,pos,"atm");ok(h,p.containerMenu instanceof ServiceMenu,"ATM did not open");click(p,28);ok(h,a.balance==2500,"ATM paid before PIN setup");
  pin(p,"0457");ok(h,CardPin.has(Money.cardStack(p)),"ATM did not save PIN");click(p,28);ok(h,a.balance==2400,"Unlocked ATM did not pay");
  ServiceMenu.openDevice(p,pos,"atm");click(p,28);ok(h,a.balance==2400,"New visit reused PIN session");pin(p,"9999");click(p,28);ok(h,a.balance==2400,"Wrong PIN paid");
  pin(p,"0457");click(p,28);ok(h,a.balance==2300&&CardPin.verify(p,"0457"),"Correct PIN failed after inventory commit");p.closeContainer();h.succeed();
 }
 @GameTest(batch="pin",template="ng_city:empty",timeoutTicks=100) public static void everyHotelCardPaymentAndReclaimRequestsPin(GameTestHelper h){
  var hotel=EconomyGameTests.hotel(h);var terminal=HotelService.pos(hotel,10,2,4);var p=CityGameTests.player(h,terminal.north(2),Direction.SOUTH);Banking.open(p);CardPin.initialise(p,"0427");var a=EconomyData.get(h.getLevel()).accounts.get(p.getUUID());
  HotelService.book(p,hotel,0,1,false);ok(h,hotel.rooms[0].owner==null&&a.balance==2500,"Direct hotel payment bypassed PIN");
  playProtocol(p);ServiceMenu.openDevice(p,terminal,"hotel");click(p,34);ok(h,hotel.rooms[0].owner==null&&a.balance==2500,"Hotel charged before PIN");click(p,34);pin(p,"1111");ok(h,hotel.rooms[0].owner==null&&a.balance==2500,"Wrong PIN booked room");
  pin(p,"0427");ok(h,p.getUUID().equals(hotel.rooms[0].owner)&&a.balance==2450,"Correct hotel PIN did not book");
  click(p,23);click(p,34);click(p,34);ok(h,hotel.rooms[1].owner==null&&a.balance==2450,"Second payment reused PIN");click(p,45);
  var bag=new EconomyData.Stored();bag.owner=p.getUUID();bag.since=h.getLevel().getDayTime();bag.items.add(new ItemStack(Items.DIAMOND));hotel.rooms[1].stored.add(bag);
  click(p,37);ok(h,!hotel.rooms[1].stored.isEmpty()&&a.balance==2450,"Reclaim bypassed PIN");pin(p,"0427");ok(h,hotel.rooms[1].stored.isEmpty()&&a.balance==2440&&p.getInventory().countItem(Items.DIAMOND)==1,"PIN reclaim failed");p.closeContainer();h.succeed();
 }
 @GameTest(batch="pin",template="ng_city:empty",timeoutTicks=100) public static void sharedCardDebitAndReplacementRequirePin(GameTestHelper h){
  var p=EconomyGameTests.player(h);Banking.open(p);CardPin.initialise(p,"0427");var a=EconomyData.get(h.getLevel()).accounts.get(p.getUUID());
  ok(h,!Money.debit(p,50,false)&&!Money.debit(p,50,false,"9999")&&a.balance==2500,"Central card debit bypassed PIN");
  ok(h,Money.debit(p,50,false,"0427")&&a.balance==2450,"Central PIN debit failed");var session=new CardPin.Session();ok(h,session.unlock(p,"0427"),"Could not open session");
  Banking.replace(p,true);ok(h,!session.valid(p)&&!CardPin.has(Money.cardStack(p))&&!Money.debit(p,50,false,"0427")&&a.balance==2450,"Replacement card inherited PIN authorization");h.succeed();
 }
}
