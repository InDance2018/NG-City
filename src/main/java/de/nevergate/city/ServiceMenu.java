package de.nevergate.city;
import de.nevergate.city.economy.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
/** Vanilla container packets; every action is validated on the logical server. */
public final class ServiceMenu extends ChestMenu {
 private final ServerPlayer owner;private final Villager npc;private final BlockPos device;private final String mode;private final SimpleContainer ui;
 private final Map<Integer,Runnable> actions=new HashMap<>();private boolean cash=false,shopping=false;private int days=1,room=0,page=0;private String notice="";private String pinEntry="";private final CardPin.Session pinSession=new CardPin.Session();private EconomyData.Hotel hotel;private java.util.function.Consumer<String> pendingPayment;private UUID paymentCard;private String pinContext="";
 private ServiceMenu(int id,Inventory inv,ServerPlayer p,Villager npc,BlockPos device,String mode,SimpleContainer ui){super(CityMenus.SERVICE.get(),id,inv,ui,6);this.owner=p;this.npc=npc;this.device=device;this.mode=mode;this.ui=ui;if(npc!=null&&mode.equals("hotel"))hotel=HotelService.ensure(npc);if(hotel==null&&device!=null)hotel=HotelEvents.at(p.serverLevel(),device);refresh();}
 public String mode(){return mode;}
 public ServiceMenu(int id,Inventory inv,net.minecraft.network.FriendlyByteBuf data){this(id,inv,data.readUtf(32),new SimpleContainer(54));}
 private ServiceMenu(int id,Inventory inv,String mode,SimpleContainer ui){super(CityMenus.SERVICE.get(),id,inv,ui,6);owner=null;npc=null;device=null;this.mode=mode;this.ui=ui;}
 public ServiceMenu(int id,Inventory inv,Villager npc,BuildingKind kind){this(id,inv,(ServerPlayer)inv.player,npc,null,kind.id,new SimpleContainer(54));}
 public static SimpleContainer contents(BuildingKind kind){var c=new SimpleContainer(27);c.setItem(4,label(Items.NAME_TAG,kind.employee));return c;}
 public static void open(ServerPlayer p,Villager npc,BuildingKind kind){if(p.distanceToSqr(npc)>64)return;p.openMenu(new SimpleMenuProvider((id,inv,x)->new ServiceMenu(id,inv,p,npc,null,kind.id,new SimpleContainer(54)),Component.literal("NG-City · "+kind.title)),buf->buf.writeUtf(kind.id));}
 public static void openDevice(ServerPlayer p,BlockPos pos,String mode){if(p.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)>64)return;p.openMenu(new SimpleMenuProvider((id,inv,x)->new ServiceMenu(id,inv,p,null,pos.immutable(),mode,new SimpleContainer(54)),Component.literal("NG-City · "+(mode.equals("atm")?"Geldautomat":mode.equals("luggage")?"Fundsachen":"Hotelterminal"))),buf->buf.writeUtf(mode));}
 public static ItemStack label(Item item,String name){var s=new ItemStack(item);s.setHoverName(Component.literal(name));return s;}
 private void button(int slot,Item item,String name,Runnable action){ui.setItem(slot,label(item,name));if(action!=null)actions.put(slot,action);}
 private void say(String message){notice=message;owner.sendSystemMessage(Component.literal(message));}
 private void requestPaymentPin(String context,java.util.function.Consumer<String> action){
  var card=Money.cardStack(owner);pinEntry="";notice="";pinContext=context;paymentCard=card.isEmpty()?null:card.getTag().getUUID("Card");pendingPayment=action;
 }
 private void pinPanel(){
  var card=Money.cardStack(owner);
  if(pendingPayment!=null)button(0,Items.PAPER,pinContext,null);
  if(card.isEmpty()||pendingPayment!=null&&!CardPin.has(card)){
   button(22,Items.BARRIER,card.isEmpty()?"Deine gültige Kreditkarte muss im Inventar sein":"PIN bitte zuerst am Geldautomaten festlegen",null);
   if(pendingPayment!=null)button(45,Items.BARRIER,"Kartenzahlung abbrechen",()->{pendingPayment=null;pinEntry="";});return;
  }
  boolean first=!CardPin.has(card);
  button(4,Items.NAME_TAG,(first?"Neue vierstellige Karten-PIN festlegen: ":"Vierstellige Karten-PIN eingeben: ")+"*".repeat(pinEntry.length())+"_".repeat(4-pinEntry.length()),null);
  for(int digit=1;digit<=9;digit++){final int value=digit;int slot=12+((digit-1)/3)*9+(digit-1)%3;
   button(slot,Items.PAPER,"Ziffer "+digit,()->{if(pinEntry.length()<4)pinEntry+=value;});ui.getItem(slot).setCount(digit);
  }
  button(40,Items.SNOWBALL,"Ziffer 0",()->{if(pinEntry.length()<4)pinEntry+="0";});
  button(39,Items.REDSTONE,"Letzte Ziffer löschen",()->{if(!pinEntry.isEmpty())pinEntry=pinEntry.substring(0,pinEntry.length()-1);});
  button(45,Items.BARRIER,pendingPayment!=null?"Kartenzahlung abbrechen":"Eingabe leeren",()->{pinEntry="";if(pendingPayment!=null)pendingPayment=null;});
  button(41,Items.LIME_DYE,pendingPayment!=null?"PIN bestätigen und Kartenzahlung ausführen":first?"PIN auf Karte speichern und Geldautomat öffnen":"PIN bestätigen und Geldautomat öffnen",()->{
   if(!CardPin.format(pinEntry)){say("Bitte genau vier Ziffern eingeben.");return;}
   var current=Money.cardStack(owner);
   if(pendingPayment!=null){
    if(current.isEmpty()||paymentCard==null||!current.getTag().getUUID("Card").equals(paymentCard)||!CardPin.verify(owner,pinEntry)){pinEntry="";say("PIN falsch oder Kreditkarte geändert. Bitte erneut prüfen.");return;}
    var action=pendingPayment;String entered=pinEntry;pendingPayment=null;pinEntry="";action.accept(entered);return;
   }
   if(!CardPin.has(current)&&!CardPin.initialise(owner,pinEntry)){pinEntry="";say("Keine gültige Karte zum Speichern der PIN vorhanden.");return;}
   boolean opened=pinSession.unlock(owner,pinEntry);pinEntry="";
   say(opened?(first?"PIN auf deiner Kreditkarte gespeichert. Geldautomat freigeschaltet.":"Geldautomat freigeschaltet."):"PIN falsch. Bitte erneut eingeben.");
  });
  button(49,Items.BOOK,notice.isEmpty()?"Ziffern anklicken und mit Grün bestätigen · ESC beendet":notice,null);
 }
 private void refresh(){ui.clearContent();actions.clear();var d=EconomyData.get(owner.serverLevel());var a=d.accounts.get(owner.getUUID());if(pendingPayment!=null||mode.equals("atm")&&!pinSession.valid(owner)){pinPanel();return;}button(4,Items.GOLD_INGOT,a==null?"Noch kein Konto": "Kontostand: "+CityConfig.money(a.balance),null);
  button(45,Items.PAPER,"Zahlungsart: "+(cash?"Bargeld":"Kreditkarte / Konto"),()->cash=!cash);button(49,Items.BOOK,notice.isEmpty()?"Schaltflächen anklicken · Inventar bleibt geschützt":notice,null);
  if(mode.equals("bank")){button(19,Items.WRITABLE_BOOK,"Konto eröffnen · einmalig "+CityConfig.money(CityConfig.START.get()),()->say(Banking.open(owner)));button(21,NgCity.WALLET.get(),"Geldbörse öffnen",()->WalletMenu.open(owner));button(23,NgCity.CARD.get(),"Verlorene Karte ersetzen · alte Karte sperren",()->say(Banking.replace(owner,true)));button(25,NgCity.WALLET.get(),"Verlorene Geldbörse ersetzen",()->say(Banking.replace(owner,false)));button(31,CityBlocks.ATM.get().asItem(),"Geldautomat: Abheben / Einzahlen",()->{owner.closeContainer();openDevice(owner,npc.blockPosition(),"atm");});}
  else if(mode.equals("atm")){for(int i=0;i<Money.IDS.length;i++){final int index=i;int v=Money.VALUES[i];button(18+i,NgCity.CASH.get(i).get(),CityConfig.money(v)+" abheben",()->say(Banking.withdraw(owner,Money.VALUES[index])));}int[] amounts={100,200,500,1000};for(int i=0;i<amounts.length;i++){final int amount=amounts[i];button(28+i,Items.GOLD_NUGGET,CityConfig.money(amount)+" abheben",()->say(Banking.withdraw(owner,amount)));}button(34,Items.HOPPER,"Loses Bargeld vollständig einzahlen",()->say(Banking.deposit(owner)));button(40,NgCity.WALLET.get(),"Geldbörse öffnen",()->WalletMenu.open(owner));}
  else if(mode.equals("ankauf")){
   button(46,shopping?Items.EMERALD:Items.IRON_CHESTPLATE,shopping?"Zum Warenverkauf":"Ausrüstung kaufen",()->{shopping=!shopping;page=0;notice="";});
   if(shopping){
    button(8,Items.IRON_SWORD,"Je Teil einmal 5 Euro · danach regulärer Preis",null);
    for(int i=0;i<EquipmentShop.OFFERS.size();i++){final int index=i;var offer=EquipmentShop.OFFERS.get(i);long cost=EquipmentShop.price(a,offer);
     button(9+i,offer.item(),new ItemStack(offer.item()).getHoverName().getString()+" · "+CityConfig.money(cost)+(EquipmentShop.first(a,offer)?" · Erstkauf":" · regulär"),()->{
      if(cash)say(EquipmentShop.purchase(owner,index,cost,true,null));
      else requestPaymentPin("Ausrüstung kaufen · "+new ItemStack(offer.item()).getHoverName().getString()+" · "+CityConfig.money(cost),pin->say(EquipmentShop.purchase(owner,index,cost,false,pin)));
     });
    }
    button(47,Items.ARROW,"Vorherige Seite",null);button(51,Items.ARROW,"Nächste Seite",null);
    return;
   }
   long left=a==null?0:Math.max(0,CityConfig.LIMIT.get()-a.sold(System.currentTimeMillis()));button(8,Items.CLOCK,"Stundenlimit: noch "+CityConfig.money(left)+" · bar und Konto gemeinsam",null);var unique=new LinkedHashMap<Item,ItemStack>();for(var s:owner.getInventory().items)if(BuyPrices.quote(owner.serverLevel(),s)!=null)unique.putIfAbsent(s.getItem(),s.copy());var entries=new ArrayList<>(unique.values());int pages=Math.max(1,(entries.size()+26)/27);page=Math.min(page,pages-1);
   for(int j=page*27;j<Math.min(entries.size(),page*27+27);j++){var s=entries.get(j);var price=BuyPrices.quote(owner.serverLevel(),s);button(9+j-page*27,s.getItem(),s.getHoverName().getString()+" · "+price.quantity()+" Stück = "+CityConfig.money(price.units())+" · Klick verkauft passende Gesamtmenge",()->say(Banking.sell(owner,s,cash)));}button(47,Items.ARROW,"Vorherige Seite",()->page=Math.max(0,page-1));button(51,Items.ARROW,"Nächste Seite",()->page++);}
  else if(hotel!=null&&(mode.equals("hotel")||mode.equals("luggage")||mode.equals("display"))){HotelService.tick(owner.serverLevel(),hotel);for(int i=0;i<2;i++){final int index=i;var r=hotel.rooms[i];String status=r.owner==null?"§aFrei":"§6Belegt · "+r.name+" · "+Math.max(0,(r.until-owner.serverLevel().getDayTime()+23999)/24000)+" Tage";button(19+i*4,Items.BLUE_BED,"Zimmer "+(i+1)+" · "+status+(room==i?" §e[ausgewählt]":""),()->room=index);}
   button(28,Items.REDSTONE,"Einen Tag weniger",()->days=Math.max(1,days-1));button(30,Items.CLOCK,"Buchung: "+days+" Tage · "+CityConfig.money((long)days*CityConfig.HOTEL.get()),null);button(32,Items.GLOWSTONE_DUST,"Einen Tag mehr (maximal 7)",()->days=Math.min(7,days+1));button(34,CityBlocks.TERMINAL.get().asItem(),"Jetzt bezahlen und Zimmerschlüssel erhalten",()->{if(cash)say(HotelService.book(owner,hotel,room,days,true));else{final int selectedRoom=room,selectedDays=days;requestPaymentPin("Zimmer "+(room+1)+" · "+days+" Tage · "+CityConfig.money((long)days*CityConfig.HOTEL.get()),pin->say(HotelService.book(owner,hotel,selectedRoom,selectedDays,false,pin)));}});
   long fee=0;int count=0;for(var r:hotel.rooms)for(var bag:r.stored)if(bag.owner.equals(owner.getUUID())){fee+=HotelService.fee(owner.serverLevel(),bag);count+=bag.items.size();}button(0,Items.CHEST,count>0?"Du hattest noch Sachen im Zimmer · Abholen: "+CityConfig.money(fee):"Keine Fundsachen für dich vorhanden",null);
   button(37,Items.CHEST,"Zurückgelassene Gegenstände gegen Tagespauschale abholen",()->{if(cash)say(HotelService.reclaim(owner,hotel,false,true));else requestPaymentPin("Fundsachen mit Kreditkarte abholen",pin->say(HotelService.reclaim(owner,hotel,false,false,pin)));});button(39,Items.LAVA_BUCKET,"Fundsachen entsorgen · zunächst bestätigen",()->{notice="Zum endgültigen Vernichten erneut auf Lava klicken";});button(41,NgCity.KEY.get(),"Verlorenen Schlüssel für eigenes gebuchtes Zimmer ersetzen",()->{var selected=hotel.rooms[room];if(selected.owner!=null&&selected.owner.equals(owner.getUUID()))say(HotelService.giveKey(owner,hotel,room)?"Schlüssel ersetzt.":"Kein freier Inventarplatz.");else say("Kein eigenes gültiges Zimmer ausgewählt.");});
   if(notice.equals("Zum endgültigen Vernichten erneut auf Lava klicken"))actions.put(39,()->say(HotelService.reclaim(owner,hotel,true,cash)));
  }else button(22,Items.BARRIER,"Kein registriertes Hotel an diesem Standort",null);
 }
 @Override public void clicked(int slot,int button,ClickType type,Player player){if(player!=owner||!stillValid(player))return;if(slot>=0&&slot<54&&type==ClickType.PICKUP&&button==0){if(pendingPayment!=null||mode.equals("atm")&&!pinSession.valid(owner))refresh();var run=actions.get(slot);if(run!=null)run.run();if(owner.containerMenu==this){refresh();sendAllDataToRemote();}}else sendAllDataToRemote();}
 @Override public void broadcastChanges(){if(owner!=null&&owner.tickCount%20==0)refresh();super.broadcastChanges();}
 @Override public ItemStack quickMoveStack(Player player,int index){return ItemStack.EMPTY;}
 @Override public boolean stillValid(Player p){return owner==null||p==owner&&(npc!=null?npc.isAlive()&&p.distanceToSqr(npc)<=64:device!=null&&p.distanceToSqr(device.getX()+.5,device.getY()+.5,device.getZ()+.5)<=64);}
}
