package de.nevergate.city;

import java.util.Map;
import net.minecraft.core.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Shared coordinates: preview and server placement use the same transformed plan. */
public record PreviewGeometry(BuildingKind kind, BlockPos origin, Direction into,
        Map<BlockPos,BlockState> blocks, AABB bounds, AABB entrance) {
    public static PreviewGeometry create(BuildingKind kind,BlockPos origin,Direction into) {
        return create(new BuildingPlan(kind),origin,into);
    }
    public static PreviewGeometry create(BuildingPlan plan,BlockPos origin,Direction into) {
        BuildingKind kind=plan.kind;
        Map<BlockPos,BlockState> blocks=plan.transformed(origin,into);
        AABB bounds=new AABB(blocks.keySet().iterator().next());
        for(BlockPos pos:blocks.keySet()) bounds=bounds.minmax(new AABB(pos));
        BlockPos step=plan.worldPos(origin,into,new BlockPos(plan.width/2,0,-1));
        return new PreviewGeometry(kind,origin.immutable(),into,blocks,bounds,new AABB(step).expandTowards(0,2,0));
    }
}
