package de.nevergate.city.economy;
import de.nevergate.city.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.*;
public final class CityBlocks {
 public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
 public static final DeferredRegister<Block> BLOCKS=DeferredRegister.create(ForgeRegistries.BLOCKS,NgCity.ID);
 public static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,NgCity.ID);
 public static final RegistryObject<Block> ATM=BLOCKS.register("geldautomat",()->new Device("atm"));
 public static final RegistryObject<Block> TERMINAL=BLOCKS.register("zahlungsterminal",()->new Device("hotel"));
 public static final RegistryObject<Block> LUGGAGE=BLOCKS.register("fundsachenlager",()->new Device("luggage"));
 public static final RegistryObject<Block> DISPLAY=BLOCKS.register("hotel_display",Display::new);
 public static final RegistryObject<Block> ROOM_DOOR=BLOCKS.register("zimmertuer",RoomDoor::new);
 public static final RegistryObject<BlockEntityType<HotelDisplayEntity>> DISPLAY_ENTITY=ENTITIES.register("hotel_display",()->BlockEntityType.Builder.of(HotelDisplayEntity::new,DISPLAY.get()).build(null));
 public static class Device extends HorizontalDirectionalBlock {
  public final String mode;
  public Device(String mode){super(Properties.copy(Blocks.IRON_BLOCK).strength(3.5f).noOcclusion());this.mode=mode;registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));}
  @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b){b.add(FACING);}
  @Override public BlockState rotate(BlockState s,Rotation r){return s.setValue(FACING,r.rotate(s.getValue(FACING)));}
  @Override public BlockState mirror(BlockState s,Mirror m){return rotate(s,m.getRotation(s.getValue(FACING)));}
  @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c){return defaultBlockState().setValue(FACING,c.getHorizontalDirection().getOpposite());}
  @Override public InteractionResult use(BlockState s,Level l,BlockPos p,Player player,InteractionHand hand,BlockHitResult hit){if(!l.isClientSide&&player instanceof ServerPlayer sp&&hand==InteractionHand.MAIN_HAND)ServiceMenu.openDevice(sp,p,mode);return InteractionResult.sidedSuccess(l.isClientSide);}
 }
 public static final class Display extends Device implements EntityBlock {
  public Display(){super("display");}
  @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new HotelDisplayEntity(p,s);}
  @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){return l.isClientSide?null:(world,p,state,be)->{if(be instanceof HotelDisplayEntity display&&world.getGameTime()%20==0)display.refresh();};}
 }
 public static final class RoomDoor extends DoorBlock {
  public RoomDoor(){super(Properties.copy(Blocks.IRON_DOOR).strength(-1,3600000),BlockSetType.IRON);}
  @Override public void neighborChanged(BlockState s,Level l,BlockPos p,Block b,BlockPos from,boolean moving){/* keys, never redstone */}
  @Override public InteractionResult use(BlockState s,Level l,BlockPos p,Player player,InteractionHand hand,BlockHitResult hit){if(l.isClientSide)return InteractionResult.SUCCESS;if(!(player instanceof ServerPlayer sp))return InteractionResult.FAIL;
   for(var h:EconomyData.get(sp.serverLevel()).hotels.values())if(h.dimension.equals(l.dimension().location().toString()))for(int r=0;r<2;r++){var door=HotelService.door(h,r);if(p.equals(door)||p.equals(door.above())){HotelService.tick(sp.serverLevel(),h);if(HotelService.key(sp,h,r)){boolean open=!l.getBlockState(door).getValue(OPEN);for(int y=0;y<2;y++)l.setBlock(door.above(y),l.getBlockState(door.above(y)).setValue(OPEN,open),3);if(open)HotelEvents.closeAt.put(new HotelEvents.DoorAddress(l.dimension(),door.immutable()),l.getGameTime()+40);return InteractionResult.CONSUME;}sp.displayClientMessage(net.minecraft.network.chat.Component.literal("Nur mit deinem gültigen Schlüssel für dieses Zimmer."),true);return InteractionResult.FAIL;}}
   return InteractionResult.FAIL;
  }
 }
}
