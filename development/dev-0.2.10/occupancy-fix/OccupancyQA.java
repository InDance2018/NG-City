package qa;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import de.nevergate.city.*;
@Mod("city_qa") public class OccupancyQA {
 public OccupancyQA(){MinecraftForge.EVENT_BUS.addListener(this::started);}
 void pass(boolean ok,String label){if(!ok)throw new IllegalStateException(label);System.out.println("OCC_QA_PASS "+label);}
 public void started(ServerStartedEvent e){e.getServer().m_18709_(()->{try{run();}catch(Throwable x){System.out.println("OCC_QA_FAILED "+x);x.printStackTrace();}});}
 void run()throws Exception{
  World w=Bukkit.getWorlds().get(0);ServerLevel level=(ServerLevel)w.getClass().getMethod("getHandle").invoke(w);
  pass(Bukkit.getPluginManager().getPlugin("NG-TestWorld")==null,"MC02 plugin set without NG-TestWorld");
  w.getChunkAt(1,1).load();w.getBlockAt(20,63,20).setType(org.bukkit.Material.STONE,false);
  QAPlayer player=new QAPlayer(level.m_7654_(),level,new com.mojang.authlib.GameProfile(UUID.randomUUID(),"OccupancyQA"));player.m_6034_(20,64,10);
  Map<BlockPos,BlockState> blocks=Map.of(new BlockPos(20,64,20),Blocks.f_50016_.m_49966_());
  pass(Placement.validate(level,blocks,player,null)==null,"empty place accepted");
  org.bukkit.entity.Villager villager=w.spawn(new Location(w,20.5,64,20.5),org.bukkit.entity.Villager.class,v->v.setCustomName("QA_hidden_villager"));
  String reason=Placement.validate(level,blocks,player,null);System.out.println("OCC_QA_REASON "+reason);
  pass(reason!=null&&reason.contains("minecraft:villager")&&reason.contains("QA_hidden_villager")&&reason.contains("20 / 64 / 20"),"exact villager type name and coordinates");
  villager.setInvisible(true);reason=Placement.validate(level,blocks,player,null);
  pass(reason!=null&&reason.contains("QA_hidden_villager"),"invisible server entity identified");villager.remove();
  pass(Placement.validate(level,blocks,player,null)==null,"no stale reason after removal");
  org.bukkit.entity.Entity boat=w.spawnEntity(new Location(w,20.5,64,20.5),EntityType.BOAT);
  reason=Placement.validate(level,blocks,player,null);System.out.println("OCC_QA_REASON "+reason);
  pass(reason!=null&&reason.contains("minecraft:boat"),"vehicle identified instead of generic living entity message");boat.remove();
  ItemEntity item=new ItemEntity(level,20.5,64,20.5,new ItemStack(NgCity.COMPLEX.get()));pass(level.m_7967_(item),"dropped item added");
  pass(Placement.validate(level,blocks,player,null)==null,"dropped items remain allowed");item.m_146870_();
  villager=w.spawn(new Location(w,24.5,64,20.5),org.bukkit.entity.Villager.class);
  pass(Placement.validate(level,blocks,player,null)==null,"entity outside actual query bounds remains allowed");villager.remove();
  int x=400,z=400,y=64;
  for(int a=x-100;a<=x+100;a+=16)for(int b=z-100;b<=z+100;b+=16)w.getChunkAt(a>>4,b>>4).load();
  for(int a=x-90;a<=x+90;a++)for(int b=z-90;b<=z+90;b++)w.getBlockAt(a,y-1,b).setType(org.bukkit.Material.STONE,false);
  player.m_6034_(x,y+2,z-10);player.m_21008_(InteractionHand.MAIN_HAND,new ItemStack(NgCity.COMPLEX.get()));
  UseOnContext ctx=new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(new Vec3(x+.5,y,z+.5),Direction.UP,new BlockPos(x,y-1,z),false));
  InteractionResult result=ComplexPlacement.place(ctx);
  long count=w.getEntitiesByClass(org.bukkit.entity.Villager.class).stream().filter(v->v.getLocation().getX()>300&&v.getLocation().getX()<500&&v.getLocation().getZ()>300&&v.getLocation().getZ()<500).count();
  pass(result==InteractionResult.CONSUME&&count==13,"complete complex placed with thirteen employees");
  System.out.println("OCC_QA_FINISHED");
 }
}
