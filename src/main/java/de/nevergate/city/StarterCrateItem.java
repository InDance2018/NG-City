package de.nevergate.city;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.BlockSnapshot;

public final class StarterCrateItem extends Item {
    public StarterCrateItem(Properties props) { super(props); }
    @Override public InteractionResult useOn(UseOnContext ctx) {
        if(ctx.getClickedFace()!=Direction.UP || ctx.getPlayer()==null) return InteractionResult.FAIL;
        if(ctx.getLevel().isClientSide) return InteractionResult.SUCCESS;
        ServerPlayer player=(ServerPlayer)ctx.getPlayer(); ServerLevel level=(ServerLevel)ctx.getLevel();
        BlockPos pos=ctx.getClickedPos().above();
        Map<BlockPos,BlockState> plan=Map.of(pos,Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING,ctx.getHorizontalDirection().getOpposite()));
        String failure=Placement.validate(level,plan,player,ctx);
        if(failure!=null) { player.displayClientMessage(Component.literal(failure),true); return InteractionResult.FAIL; }
        BlockSnapshot before=BlockSnapshot.create(level.dimension(),level,pos);
        if(!Placement.apply(level,plan,player,Direction.UP)) return InteractionResult.FAIL;
        if(!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)) { before.restore(true,false); return InteractionResult.FAIL; }
        chest.setCustomName(Component.literal("NG-City · Startkiste"));
        chest.setItem(10,new ItemStack(NgCity.BANK.get())); chest.setItem(13,new ItemStack(NgCity.HOTEL.get()));
        chest.setItem(16,new ItemStack(NgCity.BUYER.get())); chest.setItem(22,Guide.create()); chest.setChanged();
        if(!player.getAbilities().instabuild) ctx.getItemInHand().shrink(1);
        return InteractionResult.CONSUME;
    }
}
