package de.nevergate.city;
import de.nevergate.city.economy.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value="ng_city",namespace="ng_city")
public final class EquipmentShopGameTests {
 private static void ok(GameTestHelper h,boolean value,String message){h.assertTrue(value,message);}
 @GameTest(batch="equipment",template="ng_city:empty",timeoutTicks=100) public static void allTenFirstAndRegularPrices(GameTestHelper h){
  var p=EconomyGameTests.player(h);var a=EconomyGameTests.account(h,p);long[] regular={100,160,140,80,80,120,120,60,80,80};
  for(int i=0;i<10;i++){var offer=EquipmentShop.OFFERS.get(i);ok(h,EquipmentShop.price(a,offer)==50,"First price is not five euros");EquipmentShop.purchase(p,i,50,false,"0427");ok(h,p.getInventory().countItem(offer.item())==1&&EquipmentShop.price(a,offer)==regular[i],"First delivery or regular price wrong");EquipmentShop.purchase(p,i,regular[i],false,"0427");ok(h,p.getInventory().countItem(offer.item())==2,"Repeat delivery missing");}
  ok(h,a.balance==980&&a.starterPurchases.size()==10&&a.sales.isEmpty(),"Total should be 50 + 102 euros; buying must not use sell allowance");h.succeed();
 }
 @GameTest(batch="equipment",template="ng_city:empty",timeoutTicks=100) public static void firstPurchaseIsPersonalAndSurvivesDiskReload(GameTestHelper h)throws Exception{
  var p=EconomyGameTests.player(h);var a=EconomyGameTests.account(h,p);EquipmentShop.purchase(p,0,50,false,"0427");for(var stack:p.getInventory().items)if(stack.is(Items.IRON_HELMET))stack.shrink(1);
  var q=EconomyGameTests.player(h);var other=EconomyGameTests.account(h,q);ok(h,EquipmentShop.price(other,EquipmentShop.OFFERS.get(0))==50,"Another player lost starter offer");Banking.replace(p,true);
  var data=EconomyData.get(h.getLevel());var folder=java.nio.file.Files.createTempDirectory("ng-equipment").toFile();var storage=new net.minecraft.world.level.storage.DimensionDataStorage(folder,h.getLevel().getServer().getFixerUpper());storage.set("economy",data);data.setDirty();storage.save();
  var factory=new net.minecraft.world.level.saveddata.SavedData.Factory<>(EconomyData::new,EconomyData::load,net.minecraft.util.datafix.DataFixTypes.LEVEL);
  var read=new net.minecraft.world.level.storage.DimensionDataStorage(folder,h.getLevel().getServer().getFixerUpper()).computeIfAbsent(factory,"economy");
  ok(h,EquipmentShop.price(read.accounts.get(p.getUUID()),EquipmentShop.OFFERS.get(0))==100&&EquipmentShop.price(read.accounts.get(p.getUUID()),EquipmentShop.OFFERS.get(1))==50,"Reload lost per-item purchases");
  var old=data.save(new CompoundTag());for(var tag:old.getList("Accounts",10))((CompoundTag)tag).remove("StarterPurchases");ok(h,EconomyData.load(old).accounts.get(p.getUUID()).starterPurchases.isEmpty(),"Old saves not compatible");h.succeed();
 }
 @GameTest(batch="equipment",template="ng_city:empty",timeoutTicks=100) public static void invalidPurchaseNeverChargesOrUsesDiscount(GameTestHelper h){
  var p=EconomyGameTests.player(h);EquipmentShop.purchase(p,0,50,false,"0427");ok(h,p.getInventory().countItem(Items.IRON_HELMET)==0,"No-account delivery");var a=EconomyGameTests.account(h,p);
  EquipmentShop.purchase(p,-1,50,false,"0427");EquipmentShop.purchase(p,10,50,false,"0427");EquipmentShop.purchase(p,0,1,false,"0427");EquipmentShop.purchase(p,0,50,false,"9999");EquipmentShop.purchase(p,0,50,false,null);
  ok(h,a.balance==2500&&a.starterPurchases.isEmpty()&&p.getInventory().countItem(Items.IRON_HELMET)==0,"Invalid request mutated state");a.balance=49;EquipmentShop.purchase(p,0,50,false,"0427");ok(h,a.balance==49&&a.starterPurchases.isEmpty(),"Insufficient balance used discount");h.succeed();
 }
 @GameTest(batch="equipment",template="ng_city:empty",timeoutTicks=100) public static void fullInventoryProtectsBothPayments(GameTestHelper h){
  var p=EconomyGameTests.player(h);var a=EconomyGameTests.account(h,p);for(int i=2;i<36;i++)p.getInventory().setItem(i,new ItemStack(Items.STONE,64));a.wallet.set(0,new ItemStack(NgCity.CASH.get(0).get()));
  EquipmentShop.purchase(p,0,50,false,"0427");EquipmentShop.purchase(p,0,50,true,null);
  ok(h,a.balance==2500&&Money.total(a.wallet)==500&&a.starterPurchases.isEmpty(),"Full inventory charged money or used discount");h.succeed();
 }
 @GameTest(batch="equipment",template="ng_city:empty",timeoutTicks=100) public static void cashChangeAndFreedCoinSlotAreAtomic(GameTestHelper h){
  var p=EconomyGameTests.player(h);var a=EconomyGameTests.account(h,p);a.wallet.set(0,new ItemStack(NgCity.CASH.get(0).get()));EquipmentShop.purchase(p,1,50,true,null);
  ok(h,a.balance==2500&&Money.total(a.wallet)==450&&p.getInventory().countItem(Items.IRON_CHESTPLATE)==1,"Wallet change or delivery wrong");
  a.wallet.clear();for(int i=0;i<54;i++)a.wallet.add(ItemStack.EMPTY);p.getInventory().clearContent();p.getInventory().setItem(0,new ItemStack(NgCity.CASH.get(5).get(),10));for(int i=1;i<36;i++)p.getInventory().setItem(i,new ItemStack(Items.STONE,64));
  EquipmentShop.purchase(p,0,50,true,null);ok(h,p.getInventory().countItem(Items.IRON_HELMET)==1&&Money.total(Money.inventory(p))==0&&a.balance==2500,"Paying exact cash should free delivery slot");h.succeed();
 }
 @GameTest(batch="equipment_menu",template="ng_city:empty",timeoutTicks=100) public static void shopMenuRequiresNewPinForEachPurchase(GameTestHelper h){
  var pos=h.absolutePos(new BlockPos(8,3,8));var p=CityGameTests.player(h,pos.north(2),Direction.SOUTH);var a=EconomyGameTests.account(h,p);CardPinGameTests.playProtocol(p);
  var npc=new net.minecraft.world.entity.npc.Villager(net.minecraft.world.entity.EntityType.VILLAGER,h.getLevel());npc.setPos(pos.getX(),pos.getY(),pos.getZ());ServiceMenu.open(p,npc,BuildingKind.BUYER);
  CardPinGameTests.click(p,46);ok(h,p.containerMenu.getSlot(9).getItem().is(Items.IRON_HELMET)&&p.containerMenu.getSlot(18).getItem().is(Items.SHIELD),"Shop catalogue or tab missing");
  CardPinGameTests.click(p,9);ok(h,a.balance==2500,"Charged before PIN");CardPinGameTests.pin(p,"9999");ok(h,a.balance==2500&&a.starterPurchases.isEmpty(),"Wrong PIN bought item");CardPinGameTests.pin(p,"0427");ok(h,a.balance==2450&&p.getInventory().countItem(Items.IRON_HELMET)==1,"First menu purchase failed");
  CardPinGameTests.click(p,9);CardPinGameTests.click(p,9);ok(h,a.balance==2450,"Repeat purchase reused PIN");CardPinGameTests.pin(p,"0427");ok(h,a.balance==2350&&p.getInventory().countItem(Items.IRON_HELMET)==2,"Repeat regular purchase failed");
  CardPinGameTests.click(p,45);a.wallet.set(0,new ItemStack(NgCity.CASH.get(0).get()));CardPinGameTests.click(p,18);ok(h,p.getInventory().countItem(Items.SHIELD)==1&&Money.total(a.wallet)==450,"Cash shield menu failed");p.closeContainer();h.succeed();
 }
 @GameTest(batch="equipment_menu",template="ng_city:empty",timeoutTicks=100) public static void pendingPriceCannotSilentlyIncrease(GameTestHelper h){
  var pos=h.absolutePos(new BlockPos(8,3,8));var p=CityGameTests.player(h,pos.north(2),Direction.SOUTH);var a=EconomyGameTests.account(h,p);CardPinGameTests.playProtocol(p);var npc=new net.minecraft.world.entity.npc.Villager(net.minecraft.world.entity.EntityType.VILLAGER,h.getLevel());npc.setPos(pos.getX(),pos.getY(),pos.getZ());ServiceMenu.open(p,npc,BuildingKind.BUYER);CardPinGameTests.click(p,46);CardPinGameTests.click(p,9);
  EquipmentShop.purchase(p,0,50,false,"0427");CardPinGameTests.pin(p,"0427");ok(h,a.balance==2450&&p.getInventory().countItem(Items.IRON_HELMET)==1,"Stale first-price confirmation silently charged regular price");p.closeContainer();h.succeed();
 }
}
