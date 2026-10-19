package de.nevergate.city.economy;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
/** Integer micro-units avoid sub-cent rounding exploits. Crafting can only lower a quote. */
public final class BuyPrices {
 public static final long SCALE=348285173760000L; // divisible by 64, 16 and 9
 private static final Set<String> DENY=Set.of("air","bedrock","barrier","command_block","chain_command_block","repeating_command_block","structure_block","structure_void","jigsaw","light","spawner","end_portal_frame","reinforced_deepslate","budding_amethyst","petrified_oak_slab","debug_stick","knowledge_book");
 private static final Set<String> RESOURCES=Set.of("coal","charcoal","raw_copper","copper_ingot","raw_iron","iron_ingot","iron_nugget","raw_gold","gold_ingot","gold_nugget","redstone","lapis_lazuli","emerald","diamond","quartz","amethyst_shard","netherite_scrap","netherite_ingot","brick","nether_brick","clay_ball","flint","stick","glowstone_dust","prismarine_shard","prismarine_crystals");
 public record Price(int quantity,long units){}
 private static Map<Item,Long> cached;private static Object manager;
 private static boolean allowed(Item item){var id=BuiltInRegistries.ITEM.getKey(item);return id.getNamespace().equals("minecraft")&&!DENY.contains(id.getPath())&&(item instanceof BlockItem||RESOURCES.contains(id.getPath()));}
 private static long base(Item item){String id=BuiltInRegistries.ITEM.getKey(item).getPath();
  long diamond=50*SCALE,iron=2*SCALE,gold=4*SCALE,copper=SCALE/2;
  if(id.equals("diamond"))return diamond;if(id.equals("diamond_block"))return 9*diamond;
  if(id.equals("emerald"))return 20*SCALE;if(id.equals("emerald_block"))return 180*SCALE;
  if(id.equals("iron_nugget"))return iron/9;if(id.equals("gold_nugget"))return gold/9;
  if(id.equals("iron_ingot")||id.equals("raw_iron")||id.endsWith("iron_ore"))return iron;
  if(id.equals("gold_ingot")||id.equals("raw_gold")||id.endsWith("gold_ore"))return gold;
  if(id.equals("copper_ingot")||id.equals("raw_copper")||id.endsWith("copper_ore"))return copper;
  if(id.equals("raw_iron_block")||id.equals("iron_block"))return iron*9;
  if(id.equals("raw_gold_block")||id.equals("gold_block"))return gold*9;
  if(id.equals("raw_copper_block")||id.equals("copper_block"))return copper*9;
  if(id.equals("coal")||id.equals("charcoal")||id.equals("redstone"))return SCALE/2;
  if(id.equals("coal_block")||id.equals("redstone_block"))return SCALE*9/2;
  if(id.equals("lapis_lazuli"))return SCALE;if(id.equals("lapis_block"))return 9*SCALE;
  if(id.endsWith("diamond_ore"))return diamond;if(id.endsWith("emerald_ore"))return 20*SCALE;
  if(id.endsWith("coal_ore")||id.endsWith("redstone_ore"))return SCALE/2;if(id.endsWith("lapis_ore"))return SCALE;
  if(id.endsWith("_log")||id.endsWith("_wood")||id.endsWith("_stem")||id.endsWith("_hyphae"))return SCALE/4;
  if(id.endsWith("_planks"))return SCALE/16;
  if(id.equals("netherite_ingot"))return 300*SCALE;if(id.equals("netherite_block"))return 2700*SCALE;if(id.equals("netherite_scrap")||id.equals("ancient_debris"))return 70*SCALE;
  if(id.equals("stone")||id.contains("deepslate")||id.equals("sand")||id.equals("gravel")||id.equals("granite")||id.equals("diorite")||id.equals("andesite"))return SCALE/32;
  return SCALE/64;
 }
 private static boolean natural(Item item){String id=BuiltInRegistries.ITEM.getKey(item).getPath();return id.endsWith("_ore")||id.endsWith("_log")||id.endsWith("_stem")||id.contains("leaves")||id.contains("sapling")||id.contains("flower")||id.endsWith("_coral")||id.endsWith("_coral_block")||id.endsWith("_wool")||Set.of("dirt","grass_block","coarse_dirt","rooted_dirt","podzol","mycelium","mud","sand","red_sand","gravel","clay","stone","cobblestone","deepslate","cobbled_deepslate","granite","diorite","andesite","tuff","calcite","dripstone_block","netherrack","basalt","blackstone","end_stone","soul_sand","soul_soil","ice","snow_block","sandstone","red_sandstone","obsidian","crying_obsidian","ancient_debris").contains(id);}
 public static Map<Item,Long> prices(ServerLevel l){var recipes=l.getRecipeManager();if(cached!=null&&manager==recipes)return cached;var outputs=new HashSet<Item>();for(var holder:recipes.getRecipes()){var recipe=holder.value();if(recipe instanceof CraftingRecipe||recipe instanceof StonecutterRecipe){var result=recipe.getResultItem(l.registryAccess());if(!result.isEmpty())outputs.add(result.getItem());}}
  var map=new HashMap<Item,Long>();for(var item:BuiltInRegistries.ITEM){var id=BuiltInRegistries.ITEM.getKey(item);if(id.getNamespace().equals("minecraft")&&!DENY.contains(id.getPath())&&(!(item instanceof BlockItem)||allowed(item)&&(!outputs.contains(item)||natural(item)||RESOURCES.contains(id.getPath()))))map.put(item,base(item));}
  for(int pass=0;pass<32;pass++){boolean changed=false;for(var holder:recipes.getRecipes()){var recipe=holder.value();if(!(recipe instanceof CraftingRecipe)&&!(recipe instanceof StonecutterRecipe))continue;var out=recipe.getResultItem(l.registryAccess());if(out.isEmpty()||!allowed(out.getItem()))continue;long cost=0;boolean known=true;for(var ingredient:recipe.getIngredients()){if(ingredient.isEmpty())continue;long min=Long.MAX_VALUE;for(var candidate:ingredient.getItems()){Long v=map.get(candidate.getItem());if(v!=null)min=Math.min(min,v*candidate.getCount());}if(min==Long.MAX_VALUE){known=false;break;}cost+=min;}if(known&&cost>0){long cap=Math.max(1,cost/out.getCount());if(!map.containsKey(out.getItem())||cap<map.get(out.getItem())){map.put(out.getItem(),cap);changed=true;}}}if(!changed)break;}
  for(var item:BuiltInRegistries.ITEM)if(allowed(item))map.putIfAbsent(item,base(item));
  cached=Collections.unmodifiableMap(map);manager=recipes;return cached;
 }
 public static Price quote(ServerLevel l,ItemStack stack){if(stack.isEmpty()||stack.hasTag()||!allowed(stack.getItem()))return null;Long micro=prices(l).get(stack.getItem());return micro==null?null:bundle(micro,stack.getMaxStackSize());}
 public static Price bundle(long micro,int maxStack){if(micro<=0)return null;long gcd=gcd(micro,SCALE);long qty=SCALE/gcd,units=micro/gcd;
  // Non-stackable goods must fit into the inventory; never round a payout upward.
  if(qty>Math.min(64,maxStack*32)){qty=Math.max(1,(SCALE+micro-1)/micro);units=(micro/SCALE)*qty+((micro%SCALE)*qty)/SCALE;}
  if(qty>4096||units<1)return null;return new Price((int)qty,units);
 }
 private static long gcd(long a,long b){while(b!=0){long t=a%b;a=b;b=t;}return a;}
}
