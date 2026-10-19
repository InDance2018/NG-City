package de.nevergate.city.economy;
import java.util.*;
import de.nevergate.city.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.*;
import net.minecraftforge.event.level.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=NgCity.ID)
public final class HotelEvents {
 public static boolean packing=false;
 public record DoorAddress(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension,BlockPos pos){}
 public static final Map<DoorAddress,Long> closeAt=new HashMap<>();
 @SubscribeEvent public static void tick(TickEvent.LevelTickEvent e){if(e.phase!=TickEvent.Phase.END||!(e.level instanceof ServerLevel l)||l.getGameTime()%20!=0)return;
  for(var h:EconomyData.get(l).hotels.values())HotelService.tick(l,h);
  var iterator=closeAt.entrySet().iterator();while(iterator.hasNext()){var entry=iterator.next();if(entry.getKey().dimension().equals(l.dimension())&&l.getGameTime()>=entry.getValue()&&l.hasChunkAt(entry.getKey().pos())){HotelService.close(l,entry.getKey().pos());iterator.remove();}}
 }
 public static EconomyData.Hotel at(ServerLevel l,BlockPos p){for(var h:EconomyData.get(l).hotels.values())if(h.dimension.equals(l.dimension().location().toString())&&PreviewGeometry.create(HotelService.plan(h),BlockPos.of(h.origin),Direction.from2DDataValue(h.direction)).bounds().contains(p.getX()+.5,p.getY()+.5,p.getZ()+.5))return h;return null;}
 public static boolean roomAllowed(ServerPlayer p,EconomyData.Hotel h,BlockPos pos){for(int i=0;i<2;i++)if(HotelService.roomBounds(h,i).contains(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)){var r=h.rooms[i];return r.owner!=null&&r.owner.equals(p.getUUID())&&p.serverLevel().getDayTime()<r.until;}return true;}
 @SubscribeEvent public static void interact(PlayerInteractEvent.RightClickBlock e){if(!(e.getEntity() instanceof ServerPlayer p))return;var h=at(p.serverLevel(),e.getPos());if(h!=null&&!roomAllowed(p,h,e.getPos())){e.setCanceled(true);p.displayClientMessage(net.minecraft.network.chat.Component.literal("Dieses Zimmer ist nicht auf dich gebucht."),true);}}
 @SubscribeEvent public static void breakBlock(BlockEvent.BreakEvent e){if(packing)return;if(!(e.getLevel() instanceof ServerLevel l)||!(e.getPlayer() instanceof ServerPlayer p))return;var h=at(l,e.getPos());if(h==null)return;var expected=HotelService.plan(h).transformed(BlockPos.of(h.origin),Direction.from2DDataValue(h.direction)).get(e.getPos());if((expected!=null&&!expected.isAir())||!roomAllowed(p,h,e.getPos()))e.setCanceled(true);}
 @SubscribeEvent public static void place(BlockEvent.EntityPlaceEvent e){if(e.getLevel() instanceof ServerLevel l&&e.getEntity() instanceof ServerPlayer p){var h=at(l,e.getPos());if(h!=null&&!roomAllowed(p,h,e.getPos()))e.setCanceled(true);}}
 @SubscribeEvent public static void explosion(ExplosionEvent.Detonate e){if(e.getLevel() instanceof ServerLevel l)e.getAffectedBlocks().removeIf(p->at(l,p)!=null);}
 @SubscribeEvent public static void piston(PistonEvent.Pre e){if(e.getLevel() instanceof ServerLevel l){var helper=e.getStructureHelper();if(helper.resolve()&&(helper.getToPush().stream().anyMatch(p->at(l,p)!=null||at(l,p.relative(e.getDirection()))!=null)||helper.getToDestroy().stream().anyMatch(p->at(l,p)!=null)))e.setCanceled(true);}}
}
