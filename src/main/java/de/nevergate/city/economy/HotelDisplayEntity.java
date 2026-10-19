package de.nevergate.city.economy;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class HotelDisplayEntity extends BlockEntity {
 public UUID hotel; public final String[] lines={"Zimmer 1 – Frei","Zimmer 2 – Frei"};public final boolean[] occupied={false,false};
 public HotelDisplayEntity(BlockPos p,BlockState s){super(CityBlocks.DISPLAY_ENTITY.get(),p,s);}
 public void refresh(){if(!(level instanceof ServerLevel l)||hotel==null)return;var h=EconomyData.get(l).hotels.get(hotel);if(h==null)return;boolean changed=false;for(int i=0;i<2;i++){var r=h.rooms[i];boolean taken=r.owner!=null;String text="Zimmer "+(i+1)+(taken?" – Belegt – "+r.name+" – "+Math.max(0,(r.until-l.getDayTime()+23999)/24000)+" Tage":" – Frei");if(!text.equals(lines[i])||occupied[i]!=taken){lines[i]=text;occupied[i]=taken;changed=true;}}if(changed){setChanged();l.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}}
 @Override public void load(CompoundTag t){super.load(t);hotel=t.hasUUID("Hotel")?t.getUUID("Hotel"):null;for(int i=0;i<2;i++){if(t.contains("Line"+i))lines[i]=t.getString("Line"+i);occupied[i]=t.getBoolean("Taken"+i);}}
 @Override protected void saveAdditional(CompoundTag t){super.saveAdditional(t);if(hotel!=null)t.putUUID("Hotel",hotel);for(int i=0;i<2;i++){t.putString("Line"+i,lines[i]);t.putBoolean("Taken"+i,occupied[i]);}}
 @Override public CompoundTag getUpdateTag(){return saveWithoutMetadata();}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
 @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(){return new net.minecraft.world.phys.AABB(worldPosition).inflate(3);}
}
