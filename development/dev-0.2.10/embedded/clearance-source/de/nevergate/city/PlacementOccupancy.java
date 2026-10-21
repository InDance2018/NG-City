package de.nevergate.city;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** Reports blockers after the configured successful-placement mob clearance policy. */
public final class PlacementOccupancy {
 private static final ThreadLocal<String> LAST=new ThreadLocal<>();
 public static List<Entity> query(Level level,Entity except,AABB bounds,Predicate<? super Entity> filter){
  LAST.remove();List<Entity> found=PlacementClearance.blocking(level,bounds,level.m_6249_(except,bounds,filter));
  if(!found.isEmpty()){
   StringBuilder out=new StringBuilder("Bauplatz belegt: ");
   for(int i=0;i<Math.min(3,found.size());i++){
    if(i>0)out.append("; ");Entity e=found.get(i);BlockPos p=e.m_20183_();
    String name=e.m_7755_().getString().replaceAll("[\\p{Cntrl}]"," ");if(name.length()>40)name=name.substring(0,40);
    out.append(EntityType.m_20613_(e.m_6095_())).append(" [").append(name).append("] bei ").append(p.m_123341_()).append(" / ").append(p.m_123342_()).append(" / ").append(p.m_123343_());
   }
   if(found.size()>3)out.append("; weitere: ").append(found.size()-3);
   LAST.set(out.toString());
  }
  return found;
 }
 public static String message(){String s=LAST.get();LAST.remove();return s!=null?s:"Eine Person oder ein Lebewesen steht im Bauplatz.";}
 private PlacementOccupancy(){}
}
