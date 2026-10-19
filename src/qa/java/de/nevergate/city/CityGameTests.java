package de.nevergate.city;

import java.util.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.gametest.*;

@GameTestHolder(value="ng_city",namespace="ng_city")
public final class CityGameTests {
    private static BlockPos origin(GameTestHelper h) { return h.absolutePos(new BlockPos(16,3,16)); }
    private static void support(ServerLevel level,Map<BlockPos,net.minecraft.world.level.block.state.BlockState> plan) {
        BlockPos first=plan.keySet().iterator().next(); AABB bounds=new AABB(first);
        for(BlockPos p:plan.keySet()) bounds=bounds.minmax(new AABB(p));
        for(Entity entity:level.getEntities((Entity)null,bounds,e->true)) entity.discard();
        for(BlockPos p:plan.keySet()) {
            level.setBlock(p,Blocks.AIR.defaultBlockState(),2);
            if(p.getY()==first.getY()) level.setBlock(p.below(),Blocks.STONE.defaultBlockState(),3);
        }
    }
    private static ServerPlayer player(GameTestHelper h,BlockPos p,Direction into) {
        ServerPlayer player=h.makeMockServerPlayerInLevel();
        player.setPos(p.getX()+.5,p.getY(),p.getZ()+.5); player.setYRot(into.toYRot());
        player.getAbilities().mayBuild=true; player.getAbilities().instabuild=false;
        return player;
    }
    private static UseOnContext context(ServerPlayer p,BlockPos floor) {
        return new UseOnContext(p,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(floor).add(0,.5,0),Direction.UP,floor,false));
    }
    private static void build(GameTestHelper h,BuildingKind kind,Direction dir) {
        var l=h.getLevel();var o=origin(h);var bp=new BuildingPlan(kind);var plan=bp.transformed(o,dir);support(l,plan);
        var player=player(h,o.relative(dir.getOpposite(),4),dir);
        Item item=kind==BuildingKind.BANK ? NgCity.BANK.get() : kind==BuildingKind.HOTEL ? NgCity.HOTEL.get() : NgCity.BUYER.get();
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(item));
        String preflight=Placement.validate(l,plan,player,context(player,o.below()));
        h.assertTrue(preflight==null,"Preflight rejected clear supported site: "+preflight);
        h.assertTrue(item.useOn(context(player,o.below()))==InteractionResult.CONSUME,"Kit placement failed");
        h.assertTrue(player.getMainHandItem().isEmpty(),"Survival kit not consumed exactly once");
        for(var e:plan.entrySet()) h.assertTrue(l.getBlockState(e.getKey()).equals(e.getValue()),"Wrong placed state at "+e.getKey());
        BlockPos door=bp.worldPos(o,dir,new BlockPos(kind.width/2,1,0));
        h.assertTrue(l.getBlockState(door).getBlock() instanceof DoorBlock,"Entrance missing");
        h.assertTrue(l.getBlockState(door.above()).getValue(DoorBlock.HALF)==DoubleBlockHalf.UPPER,"Door incomplete");
        h.runAfterDelay(2,()-> {
        var employees=l.getEntitiesOfClass(Villager.class,new AABB(bp.employeePos(o,dir)).inflate(1));
        h.assertTrue(employees.size()==1,"Exactly one employee required; found "+employees.size()+" at "+bp.employeePos(o,dir));
        Villager npc=employees.get(0);
        h.assertTrue(npc.getPersistentData().getString("ng_city_role").equals(kind.id)&&npc.isNoAi()&&npc.isPersistenceRequired(),"Employee role/persistence missing");
        if(kind==BuildingKind.HOTEL) {
            long beds=plan.values().stream().filter(s->s.getBlock() instanceof BedBlock && s.getValue(BedBlock.PART)==BedPart.FOOT).count();
            h.assertTrue(beds==2,"Hotel requires two beds");
            // Headroom over all five steps; last step reaches the upper landing.
            for(int z=2;z<=6;z++) {
                BlockPos step=bp.worldPos(o,dir,new BlockPos(1,z-1,z));
                h.assertTrue(l.getBlockState(step).getBlock() instanceof StairBlock,"Hotel stair missing");
                h.assertTrue(l.getBlockState(step.above()).isAir()&&l.getBlockState(step.above(2)).isAir(),"Stair lacks two blocks of headroom");
            }
        }
        h.succeed();
        });
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void bankSouth(GameTestHelper h) { build(h,BuildingKind.BANK,Direction.SOUTH); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void bankWest(GameTestHelper h) { build(h,BuildingKind.BANK,Direction.WEST); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void bankNorth(GameTestHelper h) { build(h,BuildingKind.BANK,Direction.NORTH); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void bankEast(GameTestHelper h) { build(h,BuildingKind.BANK,Direction.EAST); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void hotelSouth(GameTestHelper h) { build(h,BuildingKind.HOTEL,Direction.SOUTH); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void hotelWest(GameTestHelper h) { build(h,BuildingKind.HOTEL,Direction.WEST); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void hotelNorth(GameTestHelper h) { build(h,BuildingKind.HOTEL,Direction.NORTH); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void hotelEast(GameTestHelper h) { build(h,BuildingKind.HOTEL,Direction.EAST); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void buyerSouth(GameTestHelper h) { build(h,BuildingKind.BUYER,Direction.SOUTH); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void buyerWest(GameTestHelper h) { build(h,BuildingKind.BUYER,Direction.WEST); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void buyerNorth(GameTestHelper h) { build(h,BuildingKind.BUYER,Direction.NORTH); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void buyerEast(GameTestHelper h) { build(h,BuildingKind.BUYER,Direction.EAST); }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void blockedSitePreservesKitAndBlocks(GameTestHelper h) {
        var l=h.getLevel();var o=origin(h);var plan=new BuildingPlan(BuildingKind.BANK).transformed(o,Direction.SOUTH);support(l,plan);
        l.setBlock(o.above(2),Blocks.DIAMOND_BLOCK.defaultBlockState(),3);
        var p=player(h,o.north(4),Direction.SOUTH);p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(NgCity.BANK.get()));
        h.assertTrue(NgCity.BANK.get().useOn(context(p,o.below()))==InteractionResult.FAIL,"Overwrote obstacle");
        h.assertTrue(p.getMainHandItem().getCount()==1&&l.getBlockState(o.above(2)).is(Blocks.DIAMOND_BLOCK)&&l.getBlockState(o).isAir(),"Partial mutation or kit lost");h.succeed();
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void unsupportedFluidAndEntityRejected(GameTestHelper h) {
        var l=h.getLevel();var o=origin(h);var plan=new BuildingPlan(BuildingKind.BUYER).transformed(o,Direction.SOUTH);
        h.assertTrue(Placement.validate(l,plan,null,null)!=null,"Unsupported foundation accepted");support(l,plan);
        l.setBlock(o.above(),Blocks.WATER.defaultBlockState(),3);
        h.assertTrue(Placement.validate(l,plan,null,null)!=null,"Water accepted");l.removeBlock(o.above(),false);
        var pig=EntityType.PIG.create(l);pig.moveTo(o.getX()+.5,o.getY()+1,o.getZ()+.5,0,0);l.addFreshEntity(pig);
        h.assertTrue(Placement.validate(l,plan,null,null)!=null,"Living entity accepted");pig.discard();h.succeed();
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void protectionCancellationRollsBack(GameTestHelper h) {
        var l=h.getLevel();var o=origin(h);var plan=new BuildingPlan(BuildingKind.BANK).transformed(o,Direction.SOUTH);support(l,plan);
        var p=player(h,o.north(4),Direction.SOUTH);p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(NgCity.BANK.get()));
        var listener=new Object() { @net.minecraftforge.eventbus.api.SubscribeEvent public void cancel(BlockEvent.EntityMultiPlaceEvent e) { if(e.getEntity()==p)e.setCanceled(true); } };
        MinecraftForge.EVENT_BUS.register(listener);
        try { h.assertTrue(NgCity.BANK.get().useOn(context(p,o.below()))==InteractionResult.FAIL,"Canceled placement succeeded"); }
        finally { MinecraftForge.EVENT_BUS.unregister(listener); }
        h.assertTrue(p.getMainHandItem().getCount()==1,"Canceled placement consumed kit");
        for(BlockPos pos:plan.keySet())h.assertTrue(l.getBlockState(pos).isAir(),"Canceled placement left blocks");h.succeed();
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void crateContainsThreeKitsAndGuide(GameTestHelper h) {
        var l=h.getLevel();var o=origin(h);l.setBlock(o.below(),Blocks.STONE.defaultBlockState(),3);
        var p=player(h,o.north(4),Direction.SOUTH);p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(NgCity.STARTER.get()));
        h.assertTrue(NgCity.STARTER.get().useOn(context(p,o.below()))==InteractionResult.CONSUME,"Crate not placed");
        var chest=(ChestBlockEntity)l.getBlockEntity(o);
        h.assertTrue(chest.getItem(10).is(NgCity.BANK.get())&&chest.getItem(13).is(NgCity.HOTEL.get())&&chest.getItem(16).is(NgCity.BUYER.get()),"Missing kits");
        h.assertTrue(chest.getItem(22).is(Items.WRITTEN_BOOK)&&chest.getItem(22).getTag().getList("pages",8).size()==14,"Missing readable guide");h.succeed();
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void employeeAndLedgerNativeNbtRoundtrip(GameTestHelper h) {
        var l=h.getLevel();var o=origin(h);var id=UUID.randomUUID();
        var npc=Placement.employee(l,BuildingKind.HOTEL,o,Direction.SOUTH,id);
        CompoundTag data=new CompoundTag();npc.save(data);
        Villager copy=EntityType.VILLAGER.create(l);copy.load(data);
        h.assertTrue(copy.getPersistentData().getString("ng_city_role").equals("hotel")&&copy.getPersistentData().getUUID("ng_city_owner").equals(id)&&copy.isNoAi()&&copy.isPersistenceRequired(),"NPC NBT roundtrip failed");
        var ledger=new StarterLedger();ledger.mark(id);var saved=ledger.save(new CompoundTag());
        h.assertTrue(StarterLedger.load(saved).has(id)&&!StarterLedger.load(saved).has(UUID.randomUUID()),"Starter ledger roundtrip failed");h.succeed();
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void employeeMenuPreventsAllExtraction(GameTestHelper h) {
        var o=origin(h);var p=player(h,o,Direction.SOUTH);var npc=Placement.employee(h.getLevel(),BuildingKind.BANK,o,Direction.SOUTH,p.getUUID());
        var menu=new ServiceMenu(1,p.getInventory(),npc,BuildingKind.BANK);
        for(ClickType click:ClickType.values())for(int slot:new int[]{-999,4,11,13,15,27,35})menu.clicked(slot,0,click,p);
        h.assertTrue(menu.getSlot(11).getItem().is(Items.GOLD_INGOT)&&menu.getCarried().isEmpty()&&menu.quickMoveStack(p,11).isEmpty(),"Menu gave out icon items");
        h.assertTrue(menu.stillValid(p),"Nearby employee menu invalid");p.setPos(o.getX()+20,o.getY(),o.getZ());h.assertTrue(!menu.stillValid(p),"Distant menu valid");h.succeed();
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void buildHeightAndReadOnlyPlayerRejected(GameTestHelper h) {
        var l=h.getLevel();var o=origin(h);var bp=new BuildingPlan(BuildingKind.BANK);var plan=bp.transformed(o,Direction.SOUTH);support(l,plan);
        var p=player(h,o.north(4),Direction.SOUTH);p.getAbilities().mayBuild=false;
        h.assertTrue(Placement.validate(l,plan,p,null)!=null,"No-build player accepted");
        h.assertTrue(Placement.validate(l,bp.transformed(new BlockPos(o.getX(),l.getMaxBuildHeight()-1,o.getZ()),Direction.SOUTH),null,null)!=null,"Invalid height accepted");h.succeed();
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void employeeReturnsToWorkplace(GameTestHelper h) {
        var o=origin(h);var npc=Placement.employee(h.getLevel(),BuildingKind.BUYER,o,Direction.SOUTH,UUID.randomUUID());
        h.getLevel().addFreshEntity(npc);npc.teleportTo(o.getX()+3,o.getY(),o.getZ()+3);
        h.runAfterDelay(3,()->{h.assertTrue(npc.distanceToSqr(o.getX()+.5,o.getY(),o.getZ()+.5)<.01,"Employee left workplace");h.succeed();});
    }
    @GameTest(template="ng_city:empty",timeoutTicks=40)
    public static void starterLedgerReloadsFromDisk(GameTestHelper h) throws Exception {
        var folder=java.nio.file.Files.createTempDirectory("ng-city-ledger-").toFile();
        var factory=new net.minecraft.world.level.saveddata.SavedData.Factory<>(StarterLedger::new,StarterLedger::load,net.minecraft.util.datafix.DataFixTypes.LEVEL);
        var storage=new net.minecraft.world.level.storage.DimensionDataStorage(folder,h.getLevel().getServer().getFixerUpper());
        var ledger=storage.computeIfAbsent(factory,"starters");var id=UUID.randomUUID();ledger.mark(id);storage.save();
        var reopened=new net.minecraft.world.level.storage.DimensionDataStorage(folder,h.getLevel().getServer().getFixerUpper());
        h.assertTrue(reopened.computeIfAbsent(factory,"starters").has(id),"Saved starter recipient lost after actual disk reload");h.succeed();
    }
}
