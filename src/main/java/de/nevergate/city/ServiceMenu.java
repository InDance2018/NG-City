package de.nevergate.city;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;

/** Uses vanilla chest networking; server denies extraction, shift-click, dragging and hotkeys. */
public final class ServiceMenu extends ChestMenu {
    private final Villager npc;
    public ServiceMenu(int id,Inventory inventory,Villager npc,BuildingKind kind) {
        super(MenuType.GENERIC_9x3,id,inventory,contents(kind),3);
        this.npc=npc;
    }
    public static SimpleContainer contents(BuildingKind kind) {
        SimpleContainer contents=new SimpleContainer(27);
        contents.setItem(4,label(Items.NAME_TAG,kind.employee));
        contents.setItem(11,label(kind==BuildingKind.BANK ? Items.GOLD_INGOT : kind==BuildingKind.HOTEL ? Items.BLUE_BED : Items.CHEST,
            kind==BuildingKind.BANK ? "Bankschalter: Konten und Währung folgen" : kind==BuildingKind.HOTEL ? "Zwei begehbare Zimmer im Obergeschoss" : "Annahmetheke: Warenliste und Preise folgen"));
        contents.setItem(13,label(Items.WRITABLE_BOOK,"Gebäude betreten, erkunden und selbst einrichten"));
        contents.setItem(15,label(Items.BARRIER,"Wirtschaft noch nicht aktiviert · keine Zahlung oder Warenabgabe"));
        return contents;
    }
    private static ItemStack label(Item item,String name) { ItemStack stack=new ItemStack(item); stack.setHoverName(Component.literal(name)); return stack; }
    public static void open(ServerPlayer player,Villager npc,BuildingKind kind) {
        if(player.distanceToSqr(npc)>64) return;
        player.openMenu(new SimpleMenuProvider((id,inv,p)->new ServiceMenu(id,inv,npc,kind),Component.literal("NG-City · "+kind.title)));
    }
    @Override public void clicked(int slot,int button,ClickType type,Player player) { sendAllDataToRemote(); }
    @Override public ItemStack quickMoveStack(Player player,int index) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(Player player) { return npc.isAlive() && player.distanceToSqr(npc)<=64; }
}
