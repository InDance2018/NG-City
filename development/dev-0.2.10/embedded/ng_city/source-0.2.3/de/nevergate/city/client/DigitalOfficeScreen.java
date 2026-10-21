package de.nevergate.city.client;
import java.util.*;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;import net.minecraft.network.chat.Component;import net.minecraft.world.entity.player.Inventory;import net.minecraft.world.inventory.ChestMenu;import net.minecraft.world.inventory.ClickType;import net.minecraft.world.item.ItemStack;
/** Presentation only: server-owned slots remain the sole source of text, actions and authorization. */
public final class DigitalOfficeScreen extends AbstractContainerScreen<ChestMenu>{
 private static final int W=520,H=370;private float scale;private int ox,oy,page,focus=-999;private final int count;private final List<Hit> hits=new ArrayList<>();
 private record Hit(int id,int x,int y,int w,int h){boolean has(double a,double b){return a>=x&&b>=y&&a<x+w&&b<y+h;}}
 public DigitalOfficeScreen(ChestMenu m,Inventory inv,Component title){super(m,inv,title);count=m.f_38839_.size()-36;}
 protected void m_7856_(){super.m_7856_();scale=Math.min(1.2f,Math.min((f_96543_-16f)/W,(f_96544_-16f)/H));ox=(int)((f_96543_-W*scale)/2);oy=(int)((f_96544_-H*scale)/2);}
 private ItemStack item(int slot){return f_97732_.m_38853_(slot).m_7993_();}private String text(int slot){return item(slot).m_41786_().getString();}
 private void lines(GuiGraphics g,String text,int x,int y,int width,int max,int color){var all=f_96547_.m_92923_(Component.m_237113_(text),width);for(int i=0;i<Math.min(max,all.size());i++)g.m_280649_(f_96547_,all.get(i),x,y+11*i,color,false);}
 private boolean footer(int s){return count==54&&(s==45||s==46||s==47||s==49||s==51);}
 public void m_88315_(GuiGraphics g,int mx,int my,float dt){this.m_280273_(g,mx,my,dt);double x=(mx-ox)/scale,y=(my-oy)/scale;hits.clear();var body=new ArrayList<Integer>();for(int i=0;i<count;i++)if(i!=4&&!footer(i)&&!(count==27&&i==26)&&!item(i).m_41619_())body.add(i);int pages=Math.max(1,(body.size()+8)/9);page=Math.min(page,pages-1);
  g.m_280168_().m_85836_();g.m_280168_().m_252880_(ox,oy,0);g.m_280168_().m_85841_(scale,scale,1);
  g.m_280509_(0,0,W,H,0xFF080E15);g.m_280509_(0,0,W,42,0xFF142737);g.m_280509_(0,0,4,H,0xFF36CADA);
  g.m_280056_(f_96547_,"NG / VERWALTUNGSTERMINAL",17,15,0xFFEAF7FA,false);g.m_280056_(f_96547_,"SCHLIESSEN ×",410,15,0xFF9CB4C5,false);hits.add(new Hit(-3,400,0,120,42));
  lines(g,f_96539_.getString(),17,52,320,1,0xFF59D5E5);if(count>4&&!item(4).m_41619_())lines(g,text(4),17,68,335,1,0xFFBDD0DD);
  tile(g,new Hit(-1,361,50,31,27),"<",x,y);lines(g,(page+1)+" / "+pages,404,59,52,1,0xFFBDD0DD);tile(g,new Hit(-2,473,50,31,27),">",x,y);
  for(int i=page*9;i<Math.min(body.size(),page*9+9);i++){int k=i-page*9,s=body.get(i);tile(g,new Hit(s,16+(k%3)*164,92+(k/3)*64,156,56),text(s),x,y);}
  if(count==54){int[] foot={45,46,47,51};for(int i=0;i<foot.length;i++)if(!item(foot[i]).m_41619_())tile(g,new Hit(foot[i],16+i*124,289,116,27),text(foot[i]),x,y);}
  g.m_280509_(16,324,504,325,0xFF294354);String status=count==54&&!item(49).m_41619_()?text(49):count==27&&!item(26).m_41619_()?text(26):"Serverdaten · Berechtigungen werden zentral geprüft";lines(g,status,18,335,484,2,0xFF9CB4C5);g.m_280168_().m_85849_();
  for(var h:hits)if(h.id>=0&&h.has(x,y)){g.m_280153_(f_96547_,item(h.id),mx,my);break;}
 }
 private void tile(GuiGraphics g,Hit h,String title,double mx,double my){hits.add(h);boolean hover=h.has(mx,my)||focus==h.id;g.m_280509_(h.x,h.y,h.x+h.w,h.y+h.h,hover?0xFF234252:0xFF142733);g.m_280509_(h.x,h.y,h.x+2,h.y+h.h,0xFF36CADA);lines(g,title,h.x+9,h.y+8,h.w-17,Math.max(1,(h.h-12)/11),0xFFEAF7FA);}
 private void activate(int id){if(id==-3){m_7379_();return;}if(id==-1){page=Math.max(0,page-1);return;}if(id==-2){page++;return;}if(id>=0&&f_96541_.f_91072_!=null&&f_96541_.f_91074_!=null)f_96541_.f_91072_.m_171799_(f_97732_.f_38840_,id,0,ClickType.PICKUP,f_96541_.f_91074_);}
 public boolean m_6375_(double mx,double my,int button){if(button!=0)return false;double x=(mx-ox)/scale,y=(my-oy)/scale;for(var h:hits)if(h.has(x,y)){focus=h.id;activate(h.id);return true;}return false;}
 public boolean m_7933_(int key,int scan,int mods){if(key==258){int i=0;for(int k=0;k<hits.size();k++)if(hits.get(k).id==focus){i=(k+1)%hits.size();break;}if(!hits.isEmpty())focus=hits.get(i).id;return true;}if((key==257||key==32)&&focus!=-999){activate(focus);return true;}if(key==256||key==69){m_7379_();return true;}return false;}
 protected void m_7286_(GuiGraphics g,float dt,int x,int y){}
}
