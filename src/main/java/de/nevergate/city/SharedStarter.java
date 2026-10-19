package de.nevergate.city;

import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;

/** One shared survival starter set for the entire overworld, never replenished. */
public final class SharedStarter {
 public static StarterLedger ensure(MinecraftServer server){
  var level=server.overworld();var ledger=StarterLedger.get(level);if(ledger.distributed())return ledger;
  var spawn=level.getSharedSpawnPos();int protection=server.isDedicatedServer()?server.getSpawnProtectionRadius():0;
  // Put the chest just outside vanilla spawn protection so ordinary players can open it.
  int distance=protection>=Integer.MAX_VALUE-2?Integer.MAX_VALUE:Math.max(2,protection+2);
  for(int ring=0;ring<=8;ring++)for(int dx=-ring;dx<=ring;dx++)for(int dz=-ring;dz<=ring;dz++){
   if(Math.max(Math.abs(dx),Math.abs(dz))!=ring)continue;
   long x=(long)spawn.getX()+distance+dx,z=(long)spawn.getZ()+dz;if(Math.abs(x)>29999984||Math.abs(z)>29999984)continue;
   if(Math.max(Math.abs(x-spawn.getX()),Math.abs(z-spawn.getZ()))<=protection)continue;
   var column=new BlockPos((int)x,spawn.getY(),(int)z);if(!level.getWorldBorder().isWithinBounds(column))continue;
   var pos=level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,column);
   if(place(level,ledger,pos)){level.getDataStorage().save();return ledger;}
  }
  return ledger;
 }
 public static boolean place(ServerLevel level,StarterLedger ledger,BlockPos pos){
  if(ledger.distributed()||level.isOutsideBuildHeight(pos)||level.isOutsideBuildHeight(pos.above())||!level.getWorldBorder().isWithinBounds(pos))return false;
  if(!level.getBlockState(pos).isAir()||!level.getBlockState(pos.above()).isAir()||level.getBlockEntity(pos)!=null)return false;
  var floor=level.getBlockState(pos.below());
  if(!floor.isFaceSturdy(level,pos.below(),Direction.UP)||!floor.getFluidState().isEmpty()||floor.is(BlockTags.LEAVES))return false;
  for(var direction:Direction.Plane.HORIZONTAL){var adjacent=level.getBlockState(pos.relative(direction));if(adjacent.is(Blocks.CHEST)||adjacent.is(Blocks.TRAPPED_CHEST))return false;}
  if(!level.setBlock(pos,Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING,Direction.WEST),3))return false;
  if(!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)){level.removeBlock(pos,false);return false;}
  chest.setCustomName(Component.literal("NG-City · Gemeinsame Startkiste"));
  chest.setItem(10,new ItemStack(NgCity.BANK.get()));chest.setItem(13,new ItemStack(NgCity.HOTEL.get()));chest.setItem(16,new ItemStack(NgCity.BUYER.get()));chest.setItem(22,Guide.create());chest.setChanged();
  ledger.markCommunal(pos);return true;
 }
 public static String message(StarterLedger ledger){
  var pos=ledger.location();if(pos!=null)return "Gemeinsame NG-City-Startkiste beim Weltspawn (Oberwelt): "+pos.getX()+" / "+pos.getY()+" / "+pos.getZ()+". Sie wird nicht aufgefüllt.";
  return ledger.distributed()?"Die NG-City-Startausstattung wurde in dieser Welt bereits vergeben. Nutzt die vorhandenen Gebäude gemeinsam.":"Beim Weltspawn ist noch kein sicherer freier Platz für die gemeinsame NG-City-Startkiste vorhanden.";
 }
 private SharedStarter(){}
}
