package de.nevergate.city;
import java.util.*;import de.nevergate.city.economy.CityBlocks;import net.minecraft.core.Direction;import net.minecraft.world.level.block.Block;import net.minecraft.world.level.block.state.BlockState;import net.minecraft.world.phys.shapes.*;
/** Seven visible model cuboids, rotated exactly as geldautomat.json. No full-cube selection shell. */
public final class DeviceShapes {
 public static final double[][] ATM_BOXES={{1,0,3,15,2,15},{2,2,5,14,16,15},{1,15,2,15,16,15},{3,9,3,10.5,14.5,5},{11,9.5,2.8,13.5,11.7,5},{3,3.5,3,13,6,5},{3,7,1,13,8,7}};
 private static final VoxelShape FULL=Block.m_49796_(0,0,0,16,16,16);
 private static final Map<Direction,VoxelShape> ATM=new EnumMap<>(Direction.class);
 static {for(var direction:Direction.Plane.HORIZONTAL){VoxelShape result=null;for(var box:ATM_BOXES){double x0=box[0],z0=box[2],x1=box[3],z1=box[5];if(direction==Direction.EAST){x0=16-box[5];x1=16-box[2];z0=box[0];z1=box[3];}else if(direction==Direction.SOUTH){x0=16-box[3];x1=16-box[0];z0=16-box[5];z1=16-box[2];}else if(direction==Direction.WEST){x0=box[2];x1=box[5];z0=16-box[3];z1=16-box[0];}var part=Block.m_49796_(x0,box[1],z0,x1,box[4],z1);result=result==null?part:Shapes.m_83110_(result,part);}ATM.put(direction,result);}}
 public static VoxelShape shape(String mode,BlockState state){return "atm".equals(mode)?ATM.get(state.m_61143_(CityBlocks.FACING)):FULL;}
 private DeviceShapes(){}
}
