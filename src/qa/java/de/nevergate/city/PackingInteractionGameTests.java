package de.nevergate.city;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value="ng_city_packfix",namespace="ng_city")
public final class PackingInteractionGameTests {
 static {
  GlobalTestReporter.replaceWith(new TestReporter(){
   private final LogTestReporter log=new LogTestReporter();
   private void write(GameTestInfo test,String status){
    var row=new com.google.gson.JsonObject();row.addProperty("name",test.getTestName());row.addProperty("status",status);
    if(test.getError()!=null)row.addProperty("error",test.getError().toString());
    try{java.nio.file.Files.writeString(java.nio.file.Path.of("game-tests.ndjson"),row.toString()+"\n",java.nio.file.StandardOpenOption.CREATE,java.nio.file.StandardOpenOption.APPEND);}catch(java.io.IOException e){throw new RuntimeException(e);}
   }
   public void onTestFailed(GameTestInfo test){write(test,"failed");log.onTestFailed(test);}
   public void onTestSuccess(GameTestInfo test){write(test,"passed");log.onTestSuccess(test);}
   public void finish(){try{java.nio.file.Files.writeString(java.nio.file.Path.of("game-tests.ndjson"),"{\"complete\":true}\n",java.nio.file.StandardOpenOption.CREATE,java.nio.file.StandardOpenOption.APPEND);}catch(java.io.IOException e){throw new RuntimeException(e);}}
  });
 }

 private static void check(GameTestHelper h,BuildingKind kind,Direction dir,boolean porch){
  var level=h.getLevel();var origin=CityGameTests.origin(h);var bp=new BuildingPlan(kind);var plan=bp.transformed(origin,dir);CityGameTests.support(level,plan);
  var player=CityGameTests.player(h,origin.relative(dir.getOpposite(),4),dir);
  var item=switch(kind){case BANK->NgCity.BANK.get();case HOTEL->NgCity.HOTEL.get();case BUYER->NgCity.BUYER.get();};player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(item));
  h.assertTrue(item.useOn(CityGameTests.context(player,origin.below()))==InteractionResult.CONSUME,"Kit failed");
  h.runAfterDelay(20,()->{
   var sign=bp.worldPos(origin,dir,new BlockPos(kind.width/2,3,-1));var target=BuildingPacking.resolve(level,sign);h.assertTrue(target!=null,"Unresolved employee/sign");
   var at=porch?origin.relative(dir.getOpposite()):origin.relative(dir.getOpposite(),3);player.setPos(at.getX()+.5,at.getY()+1,at.getZ()+.5);player.setShiftKeyDown(true);
   String reason=BuildingPacking.validate(level,target,player);
   h.assertTrue(reason==null,"Settled building refused: "+reason+"; "+firstDifference(level,target));
   var event=new PlayerInteractEvent.RightClickBlock(player,InteractionHand.MAIN_HAND,sign,new BlockHitResult(Vec3.atCenterOf(sign),dir.getOpposite(),sign,false));
   net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
   h.assertTrue(event.isCanceled(),"Click was not handled");h.assertTrue(player.getMainHandItem().is(item),"No kit returned");
   for(var p:target.geometry().blocks().keySet())h.assertTrue(level.getBlockState(p).isAir(),"Remnant at "+p);
   h.succeed();
  });
 }
 private static String firstDifference(net.minecraft.server.level.ServerLevel l,BuildingPacking.Target t){for(var e:t.geometry().blocks().entrySet())if(!l.getBlockState(e.getKey()).equals(e.getValue()))return e.getKey()+" actual "+l.getBlockState(e.getKey())+" expected "+e.getValue();return "no block differences";}
 @GameTest(batch="interaction",template="ng_city:empty",timeoutTicks=100) public static void bankFromPorch(GameTestHelper h){check(h,BuildingKind.BANK,Direction.SOUTH,true);}
 @GameTest(batch="interaction",template="ng_city:empty",timeoutTicks=100) public static void bankSettled(GameTestHelper h){check(h,BuildingKind.BANK,Direction.WEST,false);}
 @GameTest(batch="interaction",template="ng_city:empty",timeoutTicks=100) public static void buyerFromPorch(GameTestHelper h){check(h,BuildingKind.BUYER,Direction.WEST,true);}
 @GameTest(batch="interaction",template="ng_city:empty",timeoutTicks=100) public static void buyerSettled(GameTestHelper h){check(h,BuildingKind.BUYER,Direction.NORTH,false);}
 @GameTest(batch="interaction",template="ng_city:empty",timeoutTicks=100) public static void hotelFromPorch(GameTestHelper h){check(h,BuildingKind.HOTEL,Direction.EAST,true);}
 @GameTest(batch="interaction",template="ng_city:empty",timeoutTicks=100) public static void hotelSettled(GameTestHelper h){check(h,BuildingKind.HOTEL,Direction.SOUTH,false);}

 private static void assertFacing(GameTestHelper h,net.minecraft.world.entity.npc.Villager npc,Direction into){
  float yaw=into.getOpposite().toYRot();
  h.assertTrue(Math.abs(net.minecraft.util.Mth.wrapDegrees(npc.getYRot()-yaw))<.01,"Wrong employee rotation");
  h.assertTrue(Math.abs(net.minecraft.util.Mth.wrapDegrees(npc.getYHeadRot()-yaw))<.01,"Employee head faces backwards");
  h.assertTrue(Math.abs(net.minecraft.util.Mth.wrapDegrees(npc.yBodyRot-yaw))<.01,"Employee body faces backwards");
  var packet=new net.minecraft.network.protocol.game.ClientboundAddEntityPacket(npc);
  h.assertTrue(Math.abs(net.minecraft.util.Mth.wrapDegrees(packet.getYRot()-yaw))<.01&&Math.abs(net.minecraft.util.Mth.wrapDegrees(packet.getYHeadRot()-yaw))<.01,"Spawn packet sends backwards NPC");
 }
 @GameTest(batch="employees",template="ng_city:empty",timeoutTicks=100) public static void employeesFaceCustomersInAllDirections(GameTestHelper h){
  for(var kind:BuildingKind.values())for(var into:Direction.Plane.HORIZONTAL){
   var npc=Placement.employee(h.getLevel(),kind,CityGameTests.origin(h),into,UUID.randomUUID());assertFacing(h,npc,into);
  }h.succeed();
 }
 @GameTest(batch="employees",template="ng_city:empty",timeoutTicks=100) public static void existingEmployeeDirectionRecoveredFromSign(GameTestHelper h){
  var l=h.getLevel();var o=CityGameTests.origin(h);var dir=Direction.WEST;var bp=new BuildingPlan(BuildingKind.BANK);CityGameTests.support(l,bp.transformed(o,dir));
  var player=CityGameTests.player(h,o.relative(dir.getOpposite(),4),dir);player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(NgCity.BANK.get()));NgCity.BANK.get().useOn(CityGameTests.context(player,o.below()));
  var sign=bp.worldPos(o,dir,new BlockPos(5,3,-1));
  h.runAfterDelay(4,()->{var target=BuildingPacking.resolve(l,sign);h.assertTrue(target!=null,"No employee");var npc=target.employee();npc.getPersistentData().remove("ng_city_direction");npc.setYRot(0);npc.setYHeadRot(0);npc.setYBodyRot(0);
   h.runAfterDelay(5,()->{assertFacing(h,npc,dir);h.assertTrue(Placement.employeeDirection(npc)==dir,"Stored direction was not recovered");h.succeed();});
  });
 }
 @GameTest(batch="benches",template="ng_city:empty",timeoutTicks=100) public static void benchesMoveOneBlockLeftFromEntrance(GameTestHelper h){
  for(var kind:new BuildingKind[]{BuildingKind.BANK,BuildingKind.HOTEL}){
   var old=new BuildingPlan(kind,2);var next=new BuildingPlan(kind);int x=kind==BuildingKind.BANK?8:9;
   for(int z=2;z<=3;z++){
    h.assertTrue(old.blocks.get(new BlockPos(x,1,z)).is(Blocks.OAK_STAIRS),"Old bench missing");
    h.assertTrue(next.blocks.get(new BlockPos(x,1,z)).isAir()&&next.blocks.get(new BlockPos(x+1,1,z)).is(Blocks.OAK_STAIRS),"Bench not moved left");
   }
  }h.succeed();
 }
 @GameTest(batch="benches",template="ng_city:empty",timeoutTicks=100) public static void oldBankCanStillBePacked(GameTestHelper h){
  var l=h.getLevel();var o=CityGameTests.origin(h);var dir=Direction.SOUTH;var bp=new BuildingPlan(BuildingKind.BANK,2);var map=bp.transformed(o,dir);CityGameTests.support(l,map);Placement.apply(l,map,null,Direction.UP);
  var player=CityGameTests.player(h,o.relative(dir.getOpposite(),3),dir);player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);var signPos=bp.worldPos(o,dir,new BlockPos(5,3,-1));
  var sign=(net.minecraft.world.level.block.entity.SignBlockEntity)l.getBlockEntity(signPos);sign.setText(sign.getFrontText().setMessage(0,net.minecraft.network.chat.Component.literal("NG-City")).setMessage(1,net.minecraft.network.chat.Component.literal("Bank")),true);sign.setWaxed(true);
  var npc=Placement.employee(l,BuildingKind.BANK,bp.employeePos(o,dir),dir,player.getUUID());l.addFreshEntity(npc);
  h.runAfterDelay(4,()->{var target=BuildingPacking.resolve(l,signPos);h.assertTrue(target!=null,"Old bank not found");String reason=BuildingPacking.validate(l,target,player);h.assertTrue(reason==null,"Old bank validation: "+reason+"; "+firstDifference(l,target));h.assertTrue(BuildingPacking.pack(l,target,player),"Old bank could not be packed after successful validation");h.assertTrue(player.getMainHandItem().is(NgCity.BANK.get()),"Old bank kit missing");h.succeed();});
 }
}
