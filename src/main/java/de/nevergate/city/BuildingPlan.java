package de.nevergate.city;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;

/** Complete footprint includes interior air: preflight protects the whole building volume. */
public final class BuildingPlan {
    public final BuildingKind kind;
    public final Map<BlockPos, BlockState> blocks = new LinkedHashMap<>();
    public BuildingPlan(BuildingKind kind) {
        this.kind = kind;
        int w = kind.width, d = kind.depth, h = kind.height;
        BlockState wall = (kind == BuildingKind.BANK ? Blocks.SMOOTH_QUARTZ : kind == BuildingKind.HOTEL ? Blocks.WHITE_CONCRETE : Blocks.BRICKS).defaultBlockState();
        for (int x=0; x<w; x++) for (int z=0; z<d; z++) for (int y=0; y<h; y++) {
            BlockState s = Blocks.AIR.defaultBlockState();
            if (y==0) s = Blocks.POLISHED_ANDESITE.defaultBlockState();
            else if (y==h-1) s = Blocks.SMOOTH_STONE.defaultBlockState();
            else if (x==0 || x==w-1 || z==0 || z==d-1) {
                boolean window = (y==2 || y==3 || (kind==BuildingKind.HOTEL && (y==7 || y==8)))
                        && (x>1 && x<w-2 || z>1 && z<d-2);
                s = window ? Blocks.GLASS.defaultBlockState() : wall;
            }
            put(x,y,z,s);
        }
        door(w/2,1,0,Direction.SOUTH);
        put(w/2,1,-1,Blocks.AIR); put(w/2,2,-1,Blocks.AIR);
        put(w/2,0,-1, Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING,Direction.SOUTH));
        // Two ceiling lights and a facade nameplate made from colored masonry.
        put(2,h==6 ? 4 : 4,2,Blocks.SEA_LANTERN);
        put(w-3,4,d-3,Blocks.SEA_LANTERN);
        put(w/2,3,0,kind==BuildingKind.BANK ? Blocks.GOLD_BLOCK : kind==BuildingKind.HOTEL ? Blocks.BLUE_CONCRETE : Blocks.GREEN_CONCRETE);
        put(w/2,3,-1,Blocks.OAK_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING,Direction.NORTH));
        // Standing employee, counter in front, separated from public floor by a low workstation.
        for (int x=w/2-1;x<=w/2+1;x++) put(x,1,4,Blocks.DARK_OAK_PLANKS);
        put(w/2-1,2,4,Blocks.LANTERN);
        put(w/2+1,2,4,kind==BuildingKind.BANK ? Blocks.LECTERN : Blocks.FLOWER_POT);
        if (kind==BuildingKind.BANK) {
            for (int x=1;x<4;x++) { put(x,1,d-2,Blocks.BARREL); put(x,2,d-2,Blocks.IRON_BLOCK); }
            put(w-2,1,d-2,Blocks.ENDER_CHEST);
            seats(w-3,2);
        } else if (kind==BuildingKind.BUYER) {
            for (int x=1;x<4;x++) put(x,1,d-2,Blocks.BARREL);
            put(w-2,1,d-2,Blocks.CRAFTING_TABLE);
            put(w-2,1,d-3,Blocks.CHEST);
            put(1,1,2,Blocks.COMPOSTER);
        } else {
            // Upper floor at y=5, two rooms; staircase rises along left side toward the rear.
            for(int x=1;x<w-1;x++) for(int z=1;z<d-1;z++) put(x,5,z,Blocks.OAK_PLANKS);
            for(int z=2;z<=6;z++) for(int x=1;x<=2;x++) {
                for(int y=5;y<=6;y++) put(x,y,z,Blocks.AIR);
                put(x,z-1,z,Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING,Direction.SOUTH));
                for(int y=1;y<z-1;y++) put(x,y,z,Blocks.OAK_PLANKS);
            }
            for(int x=3;x<w-1;x++) for(int y=6;y<=9;y++) put(x,y,4,Blocks.OAK_PLANKS);
            for(int z=5;z<d-1;z++) for(int y=6;y<=9;y++) put(7,y,z,Blocks.OAK_PLANKS);
            door(5,6,4,Direction.SOUTH); door(9,6,4,Direction.SOUTH);
            bed(5,6,8); bed(9,6,8);
            put(4,6,10,Blocks.BARREL); put(10,6,10,Blocks.BARREL);
            put(4,9,7,Blocks.SEA_LANTERN); put(10,9,7,Blocks.SEA_LANTERN);
            put(1,9,7,Blocks.SEA_LANTERN);
            seats(9,2);
        }
    }
    private void seats(int x,int z) {
        put(x,1,z,Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING,Direction.EAST));
        put(x,1,z+1,Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING,Direction.EAST));
    }
    private void bed(int x,int y,int z) {
        BlockState bed=Blocks.BLUE_BED.defaultBlockState().setValue(BedBlock.FACING,Direction.SOUTH);
        put(x,y,z,bed.setValue(BedBlock.PART,BedPart.FOOT));
        put(x,y,z+1,bed.setValue(BedBlock.PART,BedPart.HEAD));
    }
    private void door(int x,int y,int z,Direction direction) {
        BlockState door=Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.FACING,direction);
        put(x,y,z,door.setValue(DoorBlock.HALF,DoubleBlockHalf.LOWER));
        put(x,y+1,z,door.setValue(DoorBlock.HALF,DoubleBlockHalf.UPPER));
    }
    private void put(int x,int y,int z,Block block) { put(x,y,z,block.defaultBlockState()); }
    private void put(int x,int y,int z,BlockState state) { blocks.put(new BlockPos(x,y,z),state); }
    public static Rotation rotation(Direction into) {
        return switch(into) { case WEST -> Rotation.CLOCKWISE_90; case NORTH -> Rotation.CLOCKWISE_180; case EAST -> Rotation.COUNTERCLOCKWISE_90; default -> Rotation.NONE; };
    }
    public BlockPos worldPos(BlockPos origin,Direction into,BlockPos local) {
        return local.offset(-kind.width/2,0,0).rotate(rotation(into)).offset(origin);
    }
    public Map<BlockPos,BlockState> transformed(BlockPos origin,Direction into) {
        Map<BlockPos,BlockState> result = new LinkedHashMap<>();
        blocks.forEach((p,s)-> result.put(worldPos(origin,into,p),s.rotate(rotation(into))));
        return result;
    }
    public BlockPos employeePos(BlockPos origin,Direction into) { return worldPos(origin,into,new BlockPos(kind.width/2,1,5)); }
}
