package qa;
import java.util.*;import org.bukkit.*;import org.bukkit.entity.*;
import net.minecraftforge.common.MinecraftForge;import net.minecraftforge.event.server.ServerStartedEvent;import net.minecraftforge.event.level.BlockEvent;import net.minecraftforge.event.entity.EntityJoinLevelEvent;import net.minecraftforge.fml.common.Mod;
import net.minecraft.core.*;import net.minecraft.server.level.ServerLevel;import net.minecraft.world.*;import net.minecraft.world.item.*;import net.minecraft.world.item.context.UseOnContext;import net.minecraft.world.level.block.Blocks;import net.minecraft.world.phys.*;import de.nevergate.city.*;
@Mod("city_qa") public class ClearanceQA {
 boolean cancelBlocks,cancelNpc;World w;ServerLevel level;QAPlayer player;
 public ClearanceQA(){MinecraftForge.EVENT_BUS.addListener(this::started);MinecraftForge.EVENT_BUS.addListener(this::blocks);MinecraftForge.EVENT_BUS.addListener(this::entity);}
 void blocks(BlockEvent.EntityMultiPlaceEvent e){if(cancelBlocks)e.setCanceled(true);}
 void entity(EntityJoinLevelEvent e){if(cancelNpc&&e.getEntity() instanceof net.minecraft.world.entity.npc.Villager)e.setCanceled(true);}
 void pass(boolean b,String s){if(!b)throw new IllegalStateException(s);System.out.println("CLEAR_QA_PASS "+s);}
 public void started(ServerStartedEvent e){e.getServer().m_18709_(()->{try{run();System.out.println("CLEAR_QA_FINISHED");}catch(Throwable t){System.out.println("CLEAR_QA_FAILED "+t);t.printStackTrace();}});}
 void ground(int x){for(int a=x-40;a<=x+40;a+=16)for(int b=390;b<=490;b+=16)w.getChunkAt(a>>4,b>>4).load();for(int a=x-36;a<=x+36;a++)for(int b=398;b<=480;b++)w.getBlockAt(a,63,b).setType(Material.STONE,false);}
 UseOnContext context(int x){player.m_6034_(x,66,390);player.m_21008_(InteractionHand.MAIN_HAND,new ItemStack(NgCity.COMPLEX.get()));return new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(new Vec3(x+.5,64,400.5),Direction.UP,new BlockPos(x,63,400),false));}
 InteractionResult place(int x){return NgCity.COMPLEX.get().m_6225_(context(x));}
 Cow cow(int x){return w.spawn(new Location(w,x+.5,64,410.5),Cow.class);}
 long villagers(int x){long n=level.m_6249_((net.minecraft.world.entity.Entity)null,new AABB(x-50,60,390,x+50,90,490),v->v instanceof net.minecraft.world.entity.npc.Villager&&v.m_6084_()).size();System.out.println("CLEAR_QA_EMPLOYEES "+x+" "+n);return n;}
 void run()throws Exception{
 w=Bukkit.getWorlds().get(0);level=(ServerLevel)w.getClass().getMethod("getHandle").invoke(w);player=new QAPlayer(level.m_7654_(),level,new com.mojang.authlib.GameProfile(UUID.randomUUID(),"ClearanceQA"));player.m_150110_().f_35937_=true;
 pass(PlacementClearance.enabled(),"mod config default enabled");pass(!PlacementClearance.eligible(player),"players protected");
 ground(800);Cow c=w.spawn(new Location(w,800.5,64,405.5),Cow.class);UseOnContext ctx=context(800);player.m_21008_(InteractionHand.MAIN_HAND,new ItemStack(NgCity.BANK.get()));pass(NgCity.BANK.get().m_6225_(ctx)==InteractionResult.CONSUME&&c.isDead(),"standalone bank clears animals");pass(villagers(800)==1,"bank employee preserved");
 ground(1000);Cow counterCow=w.spawn(new Location(w,1000.5,64,400.5),Cow.class);ctx=context(1000);ItemStack office=new ItemStack(NgCity.OFFICE.get());office.m_41784_().m_128362_("Owner",player.m_20148_());office.m_41784_().m_128359_("Role","bank");office.m_41784_().m_128405_("Width",3);player.m_21008_(InteractionHand.MAIN_HAND,office);ctx=new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(new Vec3(1000.5,64,400.5),Direction.UP,new BlockPos(1000,63,400),false));pass(NgCity.OFFICE.get().m_6225_(ctx)==InteractionResult.CONSUME&&counterCow.isDead(),"office counter clears animals");pass(villagers(1000)==1,"office employee preserved");
 }
}
