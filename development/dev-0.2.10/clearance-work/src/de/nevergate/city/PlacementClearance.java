package de.nevergate.city;
import java.util.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfigSpec;

/** Clears only eligible mobs, after all placement operations have returned success. */
public final class PlacementClearance {
 public static ForgeConfigSpec.BooleanValue CLEAR;
 private static final ThreadLocal<Deque<Session>> ACTIVE=new ThreadLocal<>();
 private static final class Session {
  final UseOnContext context;AABB bounds;
  Session(UseOnContext context){this.context=context;}
 }
 public static ForgeConfigSpec.Builder configure(ForgeConfigSpec.Builder b){
  CLEAR=b.comment("Tiere und Monster im Bauplatz erst nach erfolgreichem Aufbau entfernen. Keine Drops oder Slime-Teilung; Spieler und Dorfbewohner bleiben geschuetzt.").define("clearAnimalsAndMonstersOnPlacement",true);return b;
 }
 public static boolean enabled(){return CLEAR!=null&&CLEAR.get();}
 public static boolean eligible(Entity e){
  return !(e instanceof Player)&&!(e instanceof AbstractVillager)
   &&(e instanceof Animal||e instanceof WaterAnimal||e instanceof AmbientCreature||e instanceof Enemy);
 }
 public static List<Entity> blocking(Level level,AABB bounds,List<Entity> found){
  Deque<Session> stack=ACTIVE.get();
  if(!level.f_46443_&&stack!=null&&!stack.isEmpty()&&stack.peek().context.m_43725_()==level)stack.peek().bounds=bounds;
  return enabled()?found.stream().filter(e->!eligible(e)).toList():found;
 }
 public static String previewHint(){return enabled()?"Tiere/Monster im Bauplatz werden beim Setzen entfernt · Spieler/NPCs bleiben geschützt":"Bauplatz frei · Eingang türkis · Rechtsklick zum Setzen";}
 public static void begin(UseOnContext context){
  Deque<Session> stack=ACTIVE.get();if(stack==null){stack=new ArrayDeque<>();ACTIVE.set(stack);}stack.push(new Session(context));
 }
 private static Session pop(UseOnContext context){
  Deque<Session> stack=ACTIVE.get();if(stack==null||stack.isEmpty())return null;
  Session s=stack.pop();if(stack.isEmpty())ACTIVE.remove();
  if(s.context!=context)throw new IllegalStateException("Placement clearance scope mismatch");return s;
 }
 public static void abort(UseOnContext context){pop(context);}
 public static InteractionResult finish(InteractionResult result,UseOnContext context){
  Session s=pop(context);Level level=context.m_43725_();
  if(result==InteractionResult.CONSUME&&!level.f_46443_&&s!=null&&s.bounds!=null&&enabled()){
   List<Entity> mobs=level.m_6249_((Entity)null,s.bounds,e->e.m_6084_()&&eligible(e));
   for(Entity e:mobs)e.m_146870_();
   if(!mobs.isEmpty()&&context.m_43723_()!=null)context.m_43723_().m_213846_(Component.m_237113_("Bauplatz freigeräumt: "+mobs.size()+" Tiere/Monster entfernt."));
  }
  return result;
 }
 private PlacementClearance(){}
}
