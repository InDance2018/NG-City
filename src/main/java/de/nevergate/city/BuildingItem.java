package de.nevergate.city;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.BlockSnapshot;

public final class BuildingItem extends Item {
    public final BuildingKind kind;
    public BuildingItem(BuildingKind kind) { super(new Properties().stacksTo(1)); this.kind=kind; }
    @Override public void appendHoverText(ItemStack stack,Level level,List<Component> lines,TooltipFlag flag) {
        lines.add(Component.literal("Fertiges begehbares Gebäude mit Mitarbeiter"));
        lines.add(Component.literal(kind.width+" × "+(kind.depth+1)+" × "+kind.height+" Blöcke · Rechtsklick auf freien Boden"));
    }
    @Override public InteractionResult useOn(UseOnContext ctx) {
        if(ctx.getClickedFace()!=Direction.UP || ctx.getPlayer()==null) return InteractionResult.FAIL;
        if(ctx.getLevel().isClientSide) return InteractionResult.SUCCESS;
        ServerLevel level=(ServerLevel)ctx.getLevel(); ServerPlayer player=(ServerPlayer)ctx.getPlayer();
        BlockPos origin=ctx.getClickedPos().above(); Direction into=ctx.getHorizontalDirection();
        BuildingPlan blueprint=new BuildingPlan(kind);
        var plan=blueprint.transformed(origin,into);
        String failure=Placement.validate(level,plan,player,ctx);
        if(failure!=null) { player.displayClientMessage(Component.literal(failure),true); return InteractionResult.FAIL; }
        // Construct employee first; failed entity insertion rolls back blocks as well.
        Villager npc=Placement.employee(level,kind,blueprint.employeePos(origin,into),into,player.getUUID());
        if(npc==null) return InteractionResult.FAIL;
        List<BlockSnapshot> before=new ArrayList<>();
        for(BlockPos pos:plan.keySet()) before.add(BlockSnapshot.create(level.dimension(),level,pos));
        if(!Placement.apply(level,plan,player,Direction.UP)) {
            player.displayClientMessage(Component.literal("Platzierung abgebrochen – Bauplatz unverändert."),true); return InteractionResult.FAIL;
        }
        BlockPos signPos=blueprint.worldPos(origin,into,new BlockPos(kind.width/2,3,-1));
        if(level.getBlockEntity(signPos) instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign) {
            sign.setText(sign.getFrontText().setMessage(0,Component.literal("NG-City")).setMessage(1,Component.literal(kind.title)),true);
            sign.setWaxed(true); sign.setChanged();
            level.sendBlockUpdated(signPos,level.getBlockState(signPos),level.getBlockState(signPos),3);
        }
        if(!level.addFreshEntity(npc)) { Placement.rollback(before); return InteractionResult.FAIL; }
        if(!player.getAbilities().instabuild) ctx.getItemInHand().shrink(1);
        player.sendSystemMessage(Component.literal(kind.title+" aufgebaut. Der Eingang zeigt zu dir. Sprich den Mitarbeiter per Rechtsklick an."));
        return InteractionResult.CONSUME;
    }
}
