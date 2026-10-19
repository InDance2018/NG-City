package de.nevergate.city.economy;
import de.nevergate.city.NgCity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
public final class WalletMenu extends ChestMenu {
 private final ServerPlayer owner;
 public WalletMenu(int id,Inventory inv,ServerPlayer p){super(MenuType.GENERIC_9x6,id,inv,container(p),6);owner=p;}
 private static Container container(ServerPlayer p){var d=EconomyData.get(p.serverLevel());var a=d.accounts.get(p.getUUID());return new Container(){
 public int getContainerSize(){return 54;}public boolean isEmpty(){return a.wallet.stream().allMatch(ItemStack::isEmpty);}public ItemStack getItem(int i){return a.wallet.get(i);}
 public ItemStack removeItem(int i,int n){var out=a.wallet.get(i).split(n);setChanged();return out;}public ItemStack removeItemNoUpdate(int i){var out=a.wallet.set(i,ItemStack.EMPTY);setChanged();return out;}
 public void setItem(int i,ItemStack s){a.wallet.set(i,s);setChanged();}public void setChanged(){d.setDirty();}public boolean stillValid(Player p2){return p2.getUUID().equals(p.getUUID());}public void clearContent(){for(int i=0;i<54;i++)a.wallet.set(i,ItemStack.EMPTY);setChanged();}public boolean canPlaceItem(int i,ItemStack s){return Money.value(s)>0;}};}
 public static void open(ServerPlayer p){if(EconomyData.get(p.serverLevel()).accounts.containsKey(p.getUUID())&&Money.has(p,NgCity.WALLET.get()))p.openMenu(new SimpleMenuProvider((id,inv,x)->new WalletMenu(id,inv,p),Component.literal("NG-Geldbörse · nur Münzen und Scheine")));}
 @Override public boolean stillValid(Player p){return p==owner&&Money.has(owner,NgCity.WALLET.get());}
 @Override public void removed(Player p){super.removed(p);EconomyData.get(owner.serverLevel()).flush(owner.serverLevel());}
}
