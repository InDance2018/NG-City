package de.nevergate.city;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value="ng_city_packing",namespace="ng_city")
public final class PreviewPackingGameTests {
    private record Fixture(net.minecraft.server.level.ServerPlayer player,BuildingPacking.Target target,BuildingPlan plan,BlockPos origin,Direction direction) { }
    private static Fixture fixture(GameTestHelper h,BuildingKind kind,Direction dir) {
        var l=h.getLevel();var o=CityGameTests.origin(h);var bp=new BuildingPlan(kind);
        CityGameTests.support(l,bp.transformed(o,dir));
        var p=CityGameTests.player(h,o.relative(dir.getOpposite(),4),dir);
        var item=switch(kind){case BANK->NgCity.BANK.get();case HOTEL->NgCity.HOTEL.get();case BUYER->NgCity.BUYER.get();};
        p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(item));
        h.assertTrue(item.useOn(CityGameTests.context(p,o.below()))==InteractionResult.CONSUME,"Fixture placement failed");
        var sign=bp.worldPos(o,dir,new BlockPos(kind.width/2,3,-1));
        return new Fixture(p,BuildingPacking.resolve(l,sign),bp,o,dir);
    }
    private static void withFixture(GameTestHelper h,BuildingKind kind,Direction dir,java.util.function.Consumer<Fixture> action){
        var initial=fixture(h,kind,dir);
        h.runAfterDelay(4,()->awaitFixture(h,initial,0,action));
    }
    private static void awaitFixture(GameTestHelper h,Fixture initial,int attempt,java.util.function.Consumer<Fixture> action){
        var sign=initial.plan.worldPos(initial.origin,initial.direction,new BlockPos(initial.plan.kind.width/2,3,-1));
        var target=BuildingPacking.resolve(h.getLevel(),sign);
        if(target==null&&attempt<60){h.runAfterDelay(1,()->awaitFixture(h,initial,attempt+1,action));return;}
        h.assertTrue(target!=null,"Fixture employee not registered: "+diagnostic(h,initial));
        action.accept(new Fixture(initial.player,target,initial.plan,initial.origin,initial.direction));
    }
    private static String diagnostic(GameTestHelper h,Fixture f){
        var sign=f.plan.worldPos(f.origin,f.direction,new BlockPos(f.plan.kind.width/2,3,-1));var anchor=f.plan.employeePos(f.origin,f.direction);
        return "origin="+f.origin+", sign="+h.getLevel().getBlockState(sign)+", kind="+BuildingPacking.signKind(h.getLevel(),sign)+", anchor="+anchor+", NPCs="+h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.npc.Villager.class,new AABB(anchor).inflate(8)).stream().map(n->n.position()+" "+n.getPersistentData()).toList();
    }
    private static void packed(GameTestHelper h,BuildingKind kind,Direction dir) {
        withFixture(h,kind,dir,f -> {var target=f.target;
        // Open the entry: normal door use must not prevent packing.
        BlockPos door=f.plan.worldPos(f.origin,dir,new BlockPos(kind.width/2,1,0));
        h.getLevel().setBlock(door,h.getLevel().getBlockState(door).setValue(DoorBlock.OPEN,true),2);
        h.assertTrue(BuildingPacking.pack(h.getLevel(),target,f.player),"Packing refused intact prefab");
        for(var pos:target.geometry().blocks().keySet())h.assertTrue(h.getLevel().getBlockState(pos).isAir(),"Building remnant");
        h.assertTrue(target.employee().isRemoved(),"Employee left behind");
        h.assertTrue(f.player.getMainHandItem().getItem() instanceof BuildingItem && f.player.getMainHandItem().getCount()==1,"Expected one kit in hand");
        h.assertTrue(!BuildingPacking.pack(h.getLevel(),target,f.player),"Repeated packing duplicated kit");h.succeed();
        });
    }
    @GameTest(batch="packing0",template="ng_city:empty",timeoutTicks=100)
    public static void bankPacksIntoSingleKit(GameTestHelper h){packed(h,BuildingKind.BANK,Direction.SOUTH);}
    @GameTest(batch="packing0",template="ng_city:empty",timeoutTicks=100)
    public static void rotatedHotelPacksIntoSingleKit(GameTestHelper h){packed(h,BuildingKind.HOTEL,Direction.WEST);}
    @GameTest(batch="packing0",template="ng_city:empty",timeoutTicks=100)
    public static void rotatedBuyerPacksIntoSingleKit(GameTestHelper h){packed(h,BuildingKind.BUYER,Direction.EAST);}
    @GameTest(batch="packing0",template="ng_city:empty",timeoutTicks=100)
    public static void inventoryItemsBlockPackingWithoutLoss(GameTestHelper h){
        withFixture(h,BuildingKind.BUYER,Direction.SOUTH,f -> {var p=f.plan.worldPos(f.origin,f.direction,new BlockPos(1,1,7));
        var barrel=(BarrelBlockEntity)h.getLevel().getBlockEntity(p);barrel.setItem(0,new ItemStack(Items.DIAMOND,7));
        h.assertTrue(!BuildingPacking.pack(h.getLevel(),f.target,f.player),"Packed occupied storage");
        h.assertTrue(barrel.getItem(0).getCount()==7&&f.player.getMainHandItem().isEmpty()&&f.target.employee().isAlive(),"Storage or employee lost");h.succeed();

        });
    }
    @GameTest(batch="packing0",template="ng_city:empty",timeoutTicks=100)
    public static void addedAndMissingBlocksBlockPacking(GameTestHelper h){
        withFixture(h,BuildingKind.BANK,Direction.SOUTH,f -> {var p=f.origin.above(2).south(2);
        h.getLevel().setBlock(p,Blocks.DIAMOND_BLOCK.defaultBlockState(),3);
        h.assertTrue(!BuildingPacking.pack(h.getLevel(),f.target,f.player)&&h.getLevel().getBlockState(p).is(Blocks.DIAMOND_BLOCK),"Removed added block");
        h.getLevel().removeBlock(p,false);h.getLevel().removeBlock(f.origin,false);
        h.assertTrue(!BuildingPacking.pack(h.getLevel(),f.target,f.player)&&f.target.employee().isAlive(),"Packed damaged structure");h.succeed();

        });
    }
    @GameTest(batch="packing1",template="ng_city:empty",timeoutTicks=100)
    public static void peopleAndLooseItemsBlockPacking(GameTestHelper h){
        withFixture(h,BuildingKind.BANK,Direction.SOUTH,f -> {f.player.setPos(f.origin.getX()+.5,f.origin.getY()+1,f.origin.getZ()+2.5);
        h.assertTrue(!BuildingPacking.pack(h.getLevel(),f.target,f.player),"Packed around player");
        f.player.setPos(f.origin.getX()+.5,f.origin.getY(),f.origin.getZ()-4+.5);
        var drop=new net.minecraft.world.entity.item.ItemEntity(h.getLevel(),f.origin.getX()+.5,f.origin.getY()+1,f.origin.getZ()+2.5,new ItemStack(Items.DIAMOND));h.getLevel().addFreshEntity(drop);
        h.assertTrue(!BuildingPacking.pack(h.getLevel(),f.target,f.player)&&drop.isAlive(),"Packed around loose items");h.succeed();

        });
    }
    @GameTest(batch="packing1",template="ng_city:empty",timeoutTicks=100)
    public static void canceledBreakPreservesWholeBuilding(GameTestHelper h){
        withFixture(h,BuildingKind.BANK,Direction.SOUTH,f -> {
        var listener=new Object(){@net.minecraftforge.eventbus.api.SubscribeEvent public void cancel(BlockEvent.BreakEvent e){if(e.getPlayer()==f.player)e.setCanceled(true);}};
        MinecraftForge.EVENT_BUS.register(listener);
        try{h.assertTrue(!BuildingPacking.pack(h.getLevel(),f.target,f.player),"Ignored protected break");}finally{MinecraftForge.EVENT_BUS.unregister(listener);}
        for(var e:f.target.geometry().blocks().entrySet())h.assertTrue(h.getLevel().getBlockState(e.getKey()).equals(e.getValue()),"Protection left partial removal");
        h.assertTrue(f.target.employee().isAlive()&&f.player.getMainHandItem().isEmpty(),"Protected removal changed employee or inventory");h.succeed();

        });
    }
    @GameTest(batch="packing1",template="ng_city:empty",timeoutTicks=100)
    public static void previewUsesExactDimensionsAndEntranceInEveryDirection(GameTestHelper h){
        for(var kind:BuildingKind.values())for(var dir:new Direction[]{Direction.SOUTH,Direction.WEST,Direction.NORTH,Direction.EAST}){
            var o=new BlockPos(100,80,200);var g=PreviewGeometry.create(kind,o,dir);boolean sideways=dir.getAxis()==Direction.Axis.X;
            h.assertTrue(g.bounds().getXsize()==(sideways?kind.depth+1:kind.width)&&g.bounds().getZsize()==(sideways?kind.width:kind.depth+1)&&g.bounds().getYsize()==kind.height,"Preview dimensions wrong for "+kind+dir);
            h.assertTrue(g.bounds().contains(g.entrance().getCenter()),"Entrance outside preview");
            var entrance=g.entrance().getCenter();h.assertTrue((entrance.x-o.getX()-.5)*dir.getStepX()+(entrance.z-o.getZ()-.5)*dir.getStepZ()<0,"Entrance not facing player");
        }h.succeed();
    }
    @GameTest(batch="packing1",template="ng_city:empty",timeoutTicks=100)
    public static void previewValidationMatchesBlockedAndClearSites(GameTestHelper h){
        var o=CityGameTests.origin(h);var g=PreviewGeometry.create(BuildingKind.HOTEL,o,Direction.SOUTH);CityGameTests.support(h.getLevel(),g.blocks());
        h.assertTrue(Placement.validate(h.getLevel(),g.blocks(),null,null)==null,"Clear preview marked blocked");
        h.getLevel().setBlock(o.above(3),Blocks.STONE.defaultBlockState(),3);
        h.assertTrue(Placement.validate(h.getLevel(),g.blocks(),null,null)!=null,"Obstruction shown as clear");h.succeed();
    }
}
