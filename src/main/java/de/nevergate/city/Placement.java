package de.nevergate.city;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;

public final class Placement {
    private Placement() { }
    public static String validate(net.minecraft.world.level.Level level, Map<BlockPos,BlockState> plan, net.minecraft.world.entity.player.Player player, UseOnContext ctx) {
        int baseY=plan.keySet().stream().mapToInt(BlockPos::getY).min().orElseThrow();
        for(BlockPos pos:plan.keySet()) {
            if(level.isOutsideBuildHeight(pos) || !level.getWorldBorder().isWithinBounds(pos)) return "Außerhalb der Bauhöhe oder Weltgrenze.";
            if(!level.hasChunkAt(pos)) return "Der gesamte Bauplatz muss geladen sein.";
            BlockState old=level.getBlockState(pos);
            if(!old.isAir() && !old.canBeReplaced() || !old.getFluidState().isEmpty() || level.getBlockEntity(pos)!=null)
                return "Bauplatz belegt – vorhandene Blöcke werden nicht überschrieben.";
            if(player!=null && (!player.mayBuild() || !level.mayInteract(player,pos)
                    || ctx!=null && !player.mayUseItemAt(pos,ctx.getClickedFace(),ctx.getItemInHand()))) return "Hier fehlt dir die Bauberechtigung.";
            if(pos.getY()==baseY && !level.getBlockState(pos.below()).isFaceSturdy(level,pos.below(),Direction.UP))
                return "Der ganze Gebäudeboden braucht einen festen Untergrund.";
        }
        BlockPos first=plan.keySet().iterator().next();
        AABB bounds=new AABB(first);
        for(BlockPos pos:plan.keySet()) bounds=bounds.minmax(new AABB(pos));
        if(!level.getEntities((Entity)null,bounds,entity -> entity.isAlive() && !(entity instanceof net.minecraft.world.entity.item.ItemEntity)).isEmpty())
            return "Eine Person oder ein Lebewesen steht im Bauplatz.";
        return null;
    }
    /** Caller runs on the logical server thread. Cancellation restores the captured volume. */
    public static boolean apply(ServerLevel level,Map<BlockPos,BlockState> plan,ServerPlayer player,Direction face) {
        List<BlockSnapshot> snapshots=new ArrayList<>();
        for(BlockPos pos:plan.keySet()) snapshots.add(BlockSnapshot.create(level.dimension(),level,pos));
        try {
            for(var entry:plan.entrySet()) {
                if(!level.setBlock(entry.getKey(),entry.getValue(),2) && !level.getBlockState(entry.getKey()).equals(entry.getValue())) {
                    rollback(snapshots); return false;
                }
            }
            if(player!=null && MinecraftForge.EVENT_BUS.post(new BlockEvent.EntityMultiPlaceEvent(snapshots,level.getBlockState(snapshots.get(0).getPos().relative(face.getOpposite())),player))) {
                rollback(snapshots); return false;
            }
            return true;
        } catch(RuntimeException error) { rollback(snapshots); throw error; }
    }
    public static void rollback(List<BlockSnapshot> snapshots) {
        for(int i=snapshots.size()-1;i>=0;i--) snapshots.get(i).restore(true,false);
    }
    public static Villager employee(ServerLevel level,BuildingKind kind,BlockPos pos,Direction into,UUID owner) {
        Villager npc=EntityType.VILLAGER.create(level);
        if(npc==null) return null;
        npc.moveTo(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,into.getOpposite().toYRot(),0);
        npc.setVillagerData(npc.getVillagerData().setProfession(kind==BuildingKind.BANK ? VillagerProfession.LIBRARIAN : kind==BuildingKind.HOTEL ? VillagerProfession.CARTOGRAPHER : VillagerProfession.TOOLSMITH));
        npc.setNoAi(true); npc.setNoGravity(true); npc.setInvulnerable(true); npc.setSilent(true); npc.setPersistenceRequired();
        npc.setCustomName(Component.literal(kind.employee)); npc.setCustomNameVisible(true);
        npc.getPersistentData().putString("ng_city_role",kind.id);
        npc.getPersistentData().putLong("ng_city_anchor",pos.asLong());
        npc.getPersistentData().putUUID("ng_city_owner",owner);
        npc.getPersistentData().putInt("ng_city_direction",into.get2DDataValue());
        alignEmployee(npc);
        return npc;
    }
    /** Building orientation is persisted separately from the rendered head/body angles. */
    public static Direction employeeDirection(Villager npc) {
        var data=npc.getPersistentData();
        if(data.contains("ng_city_direction"))return Direction.from2DDataValue(data.getInt("ng_city_direction"));
        Direction into=npc.getDirection().getOpposite();
        if(data.contains("ng_city_anchor")) {
            var anchor=BlockPos.of(data.getLong("ng_city_anchor"));
            var kind=BuildingKind.fromId(data.getString("ng_city_role"));
            for(Direction candidate:Direction.Plane.HORIZONTAL) {
                var signPos=anchor.above(2).relative(candidate.getOpposite(),6);
                if(!npc.level().hasChunkAt(signPos))continue;
                var state=npc.level().getBlockState(signPos);
                if(state.getBlock() instanceof net.minecraft.world.level.block.WallSignBlock
                    && state.getValue(net.minecraft.world.level.block.WallSignBlock.FACING)==candidate.getOpposite()
                    && npc.level().getBlockEntity(signPos) instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign
                    && sign.getFrontText().getMessage(0,false).getString().equals("NG-City")
                    && kind!=null && sign.getFrontText().getMessage(1,false).getString().equals(kind.title)) { into=candidate;break; }
            }
        }
        data.putInt("ng_city_direction",into.get2DDataValue());
        return into;
    }
    public static void alignEmployee(Villager npc) {
        float yaw=employeeDirection(npc).getOpposite().toYRot();
        npc.setYRot(yaw);npc.yRotO=yaw;
        npc.setYBodyRot(yaw);npc.yBodyRotO=yaw;
        npc.setYHeadRot(yaw);npc.yHeadRotO=yaw;
    }
}
