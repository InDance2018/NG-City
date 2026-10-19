package de.nevergate.city.client;

import de.nevergate.city.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

/** Only presentation and vanilla container clicks; the server owns all money and PIN checks. */
@Mod.EventBusSubscriber(modid=NgCity.ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class DigitalServiceScreen extends AbstractContainerScreen<ServiceMenu> {
 private static final int W=480,H=350,INK=0xffe4eef7,MUTED=0xff9bb0c5,CYAN=0xff3ddbd9;
 private float scale;private int ox,oy,focus=-1,slice=0;
 public DigitalServiceScreen(ServiceMenu m,Inventory i,Component title){super(m,i,title);}
 @SubscribeEvent public static void register(FMLClientSetupEvent e){e.enqueueWork(()->MenuScreens.register(CityMenus.SERVICE.get(),DigitalServiceScreen::new));}
 @Override protected void init(){super.init();scale=Math.min(1f,Math.min((width-12f)/W,(height-12f)/H));ox=(int)((width-W*scale)/2);oy=(int)((height-H*scale)/2);}
 private boolean pin(){return menu.getSlot(4).getItem().getHoverName().getString().contains("Karten-PIN");}
 private record Box(int x,int y,int w,int h){boolean contains(double px,double py){return px>=x&&py>=y&&px<x+w&&py<y+h;}}
 private Box box(int slot){
  if(slot==4||slot==49||slot==0||slot==8)return null;
  if(slot==22&&!pin()&&menu.getSlot(slot).getItem().is(net.minecraft.world.item.Items.BARRIER))return new Box(32,116,416,54);
  if(pin()){
   for(int n=1;n<=9;n++)if(slot==12+(n-1)/3*9+(n-1)%3)return new Box(148+(n-1)%3*62,88+(n-1)/3*43,58,39);
   if(slot==39)return new Box(148,217,58,39);if(slot==40)return new Box(210,217,58,39);if(slot==41)return new Box(272,217,58,39);
   if(slot==45)return new Box(148,262,182,24);if(slot==22)return new Box(32,116,416,54);return null;
  }
  if(slot==46&&menu.mode().equals("ankauf"))return new Box(260,45,204,28);
  if(slot==45)return new Box(16,278,236,26);
  if(slot==47)return new Box(260,278,98,26);if(slot==51)return new Box(366,278,98,26);
  if(menu.mode().equals("ankauf")&&slot>=9&&slot<36){if((slot-9)/9!=slice)return null;int n=(slot-9)%9;return new Box(16+n%3*152,88+n/3*61,144,57);}
  if(menu.mode().equals("bank")){
   int[] ids={19,21,23,25,31};for(int n=0;n<ids.length;n++)if(slot==ids[n])return new Box(16+n%2*228,88+n/2*59,220,51);
  }else if(menu.mode().equals("atm")){
   if(slot>=18&&slot<=25)return new Box(16+(slot-18)%4*114,88+(slot-18)/4*48,106,42);
   if(slot>=28&&slot<=31)return new Box(16+(slot-28)*114,188,106,37);
   if(slot==34)return new Box(16,234,294,35);if(slot==40)return new Box(318,234,146,35);
  }else{
   if(slot==19||slot==23)return new Box(slot==19?16:244,88,220,54);
   if(slot==28)return new Box(16,151,42,34);if(slot==30)return new Box(66,151,218,34);if(slot==32)return new Box(292,151,42,34);
   if(slot==34)return new Box(342,151,122,34);
   if(slot==37)return new Box(16,200,144,66);if(slot==39)return new Box(168,200,144,66);if(slot==41)return new Box(320,200,144,66);
  }return null;
 }
 private String text(int slot){return menu.getSlot(slot).getItem().getHoverName().getString();}
 private String caption(int slot){
  if(pin()){for(int n=1;n<=9;n++)if(slot==12+(n-1)/3*9+(n-1)%3)return ""+n;
   return switch(slot){case 40->"0";case 39->"Löschen";case 41->"Bestätigen";case 45->"Abbrechen / Leeren";default->text(slot);};}
  if(slot==28&&!menu.mode().equals("atm")&&!menu.mode().equals("ankauf"))return "−";if(slot==32&&!menu.mode().equals("ankauf"))return "+";
  if(slot==34&&!menu.mode().equals("atm")&&!menu.mode().equals("ankauf"))return "Jetzt bezahlen";
  if(slot==47)return "< Zurück";if(slot==51)return "Weiter >";
  return text(slot).replace(" · Klick verkauft passende Gesamtmenge","");
 }
 private void lines(GuiGraphics g,String text,int x,int y,int width,int rows,int color){var ls=font.split(Component.literal(text),width);for(int n=0;n<Math.min(rows,ls.size());n++)g.drawString(font,ls.get(n),x,y+n*11,color,false);}
 @Override public void render(GuiGraphics g,int mx,int my,float partial){
  renderBackground(g,mx,my,partial);double px=(mx-ox)/scale,py=(my-oy)/scale;int hovered=-1;
  g.pose().pushPose();g.pose().translate(ox,oy,0);g.pose().scale(scale,scale,1);
  g.fill(0,0,W,H,0xff09121e);g.fill(0,0,5,H,CYAN);g.fill(5,0,W,42,0xff142639);
  g.drawString(font,"NG / "+switch(menu.mode()){case "atm"->"GELDAUTOMAT";case "bank"->"BANKING";case "ankauf"->"ANKAUF & AUSRÜSTUNG";default->"HOTEL & SERVICE";},18,13,INK,false);
  g.drawString(font,"SCHLIESSEN ×",W-98,14,MUTED,false);
  if(!menu.getSlot(4).getItem().isEmpty())lines(g,text(4),18,51,menu.mode().equals("ankauf")&&!pin()?230:444,2,pin()?CYAN:INK);
  if(!menu.getSlot(8).getItem().isEmpty())lines(g,text(8),18,73,444,1,CYAN);
  if(!menu.getSlot(0).getItem().isEmpty())lines(g,text(0),18,73,444,1,MUTED);
  for(int n=0;n<54;n++){
   ItemStack item=menu.getSlot(n).getItem();Box b=box(n);if(item.isEmpty()||b==null)continue;
   boolean hot=b.contains(px,py);if(hot)hovered=n;int accent=(n==34||pin()&&n==41)?0xff43dbb2:CYAN;
   g.fill(b.x,b.y,b.x+b.w,b.y+b.h,hot||focus==n?0xff29465d:0xff192c3f);g.fill(b.x,b.y,b.x+2,b.y+b.h,accent);
   if(!pin()&&b.h>=40){g.renderItem(item,b.x+7,b.y+7);lines(g,caption(n),b.x+28,b.y+7,b.w-32,Math.max(1,(b.h-10)/11),INK);}
   else lines(g,caption(n),b.x+7,b.y+Math.max(5,(b.h-10)/2),b.w-12,1,INK);
  }
  g.fill(16,310,464,311,0xff294258);if(!menu.getSlot(49).getItem().isEmpty())lines(g,text(49),18,318,444,2,MUTED);
  g.pose().popPose();if(hovered>=0)g.renderTooltip(font,menu.getSlot(hovered).getItem(),mx,my);
 }
 private void click(int slot){if(menu.mode().equals("ankauf")){if(slot==46)slice=0;if(slot==51&&slice<2&&!menu.getSlot(9+(slice+1)*9).getItem().isEmpty()){slice++;return;}if(slot==47&&slice>0){slice--;return;}if(slot==47||slot==51)slice=0;}if(minecraft.gameMode!=null&&minecraft.player!=null){minecraft.gameMode.handleInventoryMouseClick(menu.containerId,slot,0,ClickType.PICKUP,minecraft.player);}}
 @Override public boolean mouseClicked(double x,double y,int button){double px=(x-ox)/scale,py=(y-oy)/scale;if(button==0){if(px>=W-105&&px<W&&py>=0&&py<42){onClose();return true;}for(int n=0;n<54;n++){Box b=box(n);if(b!=null&&!menu.getSlot(n).getItem().isEmpty()&&b.contains(px,py)){focus=n;click(n);return true;}}}return false;}
 @Override public boolean keyPressed(int key,int scan,int mods){
  if(pin()){
   int digit=key>=GLFW.GLFW_KEY_0&&key<=GLFW.GLFW_KEY_9?key-GLFW.GLFW_KEY_0:key>=GLFW.GLFW_KEY_KP_0&&key<=GLFW.GLFW_KEY_KP_9?key-GLFW.GLFW_KEY_KP_0:-1;
   if(digit>=0){click(digit==0?40:12+(digit-1)/3*9+(digit-1)%3);return true;}
   if(key==GLFW.GLFW_KEY_BACKSPACE){click(39);return true;}
   if(key==GLFW.GLFW_KEY_ENTER||key==GLFW.GLFW_KEY_KP_ENTER){click(41);return true;}
  }
  if(key==GLFW.GLFW_KEY_TAB){for(int n=1;n<=54;n++){int next=Math.floorMod(focus+n,54);if(box(next)!=null&&!menu.getSlot(next).getItem().isEmpty()){focus=next;minecraft.getNarrator().sayNow(menu.getSlot(next).getItem().getHoverName());break;}}return true;}
  if((key==GLFW.GLFW_KEY_ENTER||key==GLFW.GLFW_KEY_SPACE)&&focus>=0){click(focus);return true;}
  return super.keyPressed(key,scan,mods);
 }
 @Override protected void renderBg(GuiGraphics g,float partial,int mx,int my){}
}
