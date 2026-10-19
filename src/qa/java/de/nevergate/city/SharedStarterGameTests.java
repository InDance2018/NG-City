package de.nevergate.city;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.gametest.GameTestHolder;
@GameTestHolder(value="ng_city_shared",namespace="ng_city")
public final class SharedStarterGameTests {
 private static void ok(GameTestHelper h,boolean value,String message){h.assertTrue(value,message);}
 @GameTest(batch="shared_starter",template="ng_city:empty",timeoutTicks=100) public static void sharedCrateNeverRefillsOrRespawns(GameTestHelper h){
  var l=h.getLevel();var o=CityGameTests.origin(h);l.setBlock(o.below(),Blocks.STONE.defaultBlockState(),3);var ledger=new StarterLedger();
  ok(h,SharedStarter.place(l,ledger,o),"First shared crate failed");var chest=(ChestBlockEntity)l.getBlockEntity(o);chest.clearContent();
  ok(h,!SharedStarter.place(l,ledger,o)&&chest.isEmpty(),"Emptied crate refilled");l.removeBlock(o,false);l.setBlock(o.east(3).below(),Blocks.STONE.defaultBlockState(),3);
  ok(h,!SharedStarter.place(l,ledger,o.east(3))&&l.getBlockState(o).isAir()&&l.getBlockState(o.east(3)).isAir(),"Broken crate or changed spawn duplicated starter set");h.succeed();
 }
 @GameTest(batch="shared_starter",template="ng_city:empty",timeoutTicks=100) public static void unsafePlacementNeverConsumesGrantOrOverwrites(GameTestHelper h){
  var l=h.getLevel();var o=CityGameTests.origin(h);var ledger=new StarterLedger();l.setBlock(o.below(),Blocks.STONE.defaultBlockState(),3);l.setBlock(o,Blocks.CHEST.defaultBlockState(),3);var chest=(ChestBlockEntity)l.getBlockEntity(o);chest.setItem(0,new ItemStack(Items.DIAMOND,3));
  ok(h,!SharedStarter.place(l,ledger,o)&&chest.getItem(0).getCount()==3&&!ledger.distributed(),"Existing chest overwritten or grant consumed");l.removeBlock(o,false);l.setBlock(o.above(),Blocks.STONE.defaultBlockState(),3);
  ok(h,!SharedStarter.place(l,ledger,o)&&!ledger.distributed(),"Blocked lid accepted");l.removeBlock(o.above(),false);l.setBlock(o.below(),Blocks.WATER.defaultBlockState(),3);
  ok(h,!SharedStarter.place(l,ledger,o)&&!ledger.distributed(),"Water accepted as floor");l.setBlock(o.below(),Blocks.STONE.defaultBlockState(),3);l.setBlock(o.east(),Blocks.CHEST.defaultBlockState(),3);
  ok(h,!SharedStarter.place(l,ledger,o)&&!ledger.distributed(),"Crate would merge with existing chest");l.removeBlock(o.east(),false);
  ok(h,SharedStarter.place(l,ledger,o)&&ledger.distributed(),"Safe retry failed");h.succeed();
 }
 @GameTest(batch="shared_starter",template="ng_city:empty",timeoutTicks=100) public static void sharedLedgerAndLegacyRecipientsSurviveDiskReload(GameTestHelper h)throws Exception{
  var o=CityGameTests.origin(h);var ledger=new StarterLedger();ledger.markCommunal(o);var folder=java.nio.file.Files.createTempDirectory("ng-shared-starter").toFile();var storage=new net.minecraft.world.level.storage.DimensionDataStorage(folder,h.getLevel().getServer().getFixerUpper());storage.set("starters",ledger);ledger.setDirty();storage.save();
  var factory=new net.minecraft.world.level.saveddata.SavedData.Factory<>(StarterLedger::new,StarterLedger::load,net.minecraft.util.datafix.DataFixTypes.LEVEL);var loaded=new net.minecraft.world.level.storage.DimensionDataStorage(folder,h.getLevel().getServer().getFixerUpper()).computeIfAbsent(factory,"starters");
  ok(h,loaded.distributed()&&o.equals(loaded.location()),"Global issue flag or position lost on disk");var old=new StarterLedger();old.mark(java.util.UUID.randomUUID());var migrated=StarterLedger.load(old.save(new net.minecraft.nbt.CompoundTag()));
  ok(h,migrated.distributed()&&migrated.location()==null,"Old world would receive another starter set");ok(h,!SharedStarter.place(h.getLevel(),migrated,o.east(4)),"Legacy issue duplicated");h.succeed();
 }
 @GameTest(batch="shared_starter_login",template="ng_city:empty",timeoutTicks=100) public static void playerLoginsAndAdminCommandNeverGivePersonalCrates(GameTestHelper h){
  var server=h.getLevel().getServer();var ledger=SharedStarter.ensure(server);ok(h,ledger.distributed()&&ledger.location()!=null,"No world-spawn crate");var location=ledger.location();
  var chest=(ChestBlockEntity)server.overworld().getBlockEntity(location);ok(h,chest!=null,"Saved spawn crate absent");int count=0;for(int i=0;i<chest.getContainerSize();i++)count+=chest.getItem(i).getCount();
  for(int n=0;n<3;n++){var p=CityGameTests.player(h,CityGameTests.origin(h).above(40+n),Direction.SOUTH);CardPinGameTests.playProtocol(p);net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent(p));ok(h,p.getInventory().countItem(NgCity.STARTER.get())==0&&p.getInventory().countItem(NgCity.BANK.get())==0,"Login gave personal starter items");}
  ok(h,server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),"ngcity startkiste")==1,"Console could not report shared crate");ok(h,location.equals(SharedStarter.ensure(server).location()),"Admin command moved or duplicated crate");int after=0;for(int i=0;i<chest.getContainerSize();i++)after+=chest.getItem(i).getCount();ok(h,count==after,"Logins or command changed spawn contents");h.succeed();
 }
 @GameTest(batch="shared_starter_login",template="ng_city:empty",timeoutTicks=100) public static void oldPersonalCrateItemCannotMakeAnotherSet(GameTestHelper h){
  var o=CityGameTests.origin(h);var l=h.getLevel();l.setBlock(o.below(),Blocks.STONE.defaultBlockState(),3);var p=CityGameTests.player(h,o.north(3),Direction.SOUTH);p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(NgCity.STARTER.get()));
  ok(h,NgCity.STARTER.get().useOn(CityGameTests.context(p,o.below()))==InteractionResult.FAIL&&l.getBlockState(o).isAir(),"Old personal crate created extra starter set");h.succeed();
 }
}
