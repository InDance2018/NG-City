package de.nevergate.city;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;

/** Reversible kit removal only for a verified, unchanged prefab with its original employee. */
public final class BuildingPacking {
    private BuildingPacking() { }
    public record Target(PreviewGeometry geometry,Villager employee,BlockPos sign) { }
    public static BuildingKind signKind(ServerLevel level,BlockPos pos) {
        if(!(level.getBlockState(pos).getBlock() instanceof WallSignBlock)
            || !(level.getBlockEntity(pos) instanceof SignBlockEntity sign)
            || !sign.getFrontText().getMessage(0,false).getString().equals("NG-City")) return null;
        String title=sign.getFrontText().getMessage(1,false).getString();
        for(BuildingKind kind:BuildingKind.values()) if(kind.title.equals(title)) return kind;
        return null;
    }
    /** Also recognizes unchanged dev-0.0.1 buildings using the already persisted employee anchor. */
    public static Target resolve(ServerLevel level,BlockPos signPos) {
        BuildingKind kind=signKind(level,signPos);if(kind==null)return null;
        Direction into=level.getBlockState(signPos).getValue(WallSignBlock.FACING).getOpposite();
        BlockPos origin=signPos.below(3).relative(into);
        PreviewGeometry geometry=PreviewGeometry.create(kind,origin,into);
        BlockPos anchor=new BuildingPlan(kind).employeePos(origin,into);
        List<Villager> candidates=level.getEntitiesOfClass(Villager.class,new AABB(anchor).inflate(.25),npc ->
            npc.isAlive() && npc.getPersistentData().getString("ng_city_role").equals(kind.id)
            && npc.getPersistentData().contains("ng_city_anchor")
            && npc.getPersistentData().getLong("ng_city_anchor")==anchor.asLong());
        if(candidates.size()!=1)return null;
        if(kind==BuildingKind.BANK) {
            var plan=new BuildingPlan(kind);
            var shiftedSeat=plan.worldPos(origin,into,new BlockPos(kind.width-2,1,2));
            if(!level.getBlockState(shiftedSeat).is(Blocks.OAK_STAIRS))geometry=PreviewGeometry.create(new BuildingPlan(kind,2),origin,into);
        }
        if(kind==BuildingKind.HOTEL){var hotel=de.nevergate.city.economy.HotelService.ensure(candidates.get(0));if(hotel!=null)geometry=PreviewGeometry.create(de.nevergate.city.economy.HotelService.plan(hotel),origin,into);}
        return new Target(geometry,candidates.get(0),signPos.immutable());
    }
    public static String validate(ServerLevel level,Target target,ServerPlayer player) {
        if(target==null)return "Gebäude oder ursprünglicher Mitarbeiter nicht eindeutig gefunden.";
        if(player!=null) {
            var data=target.employee.getPersistentData();
            if(!data.hasUUID("ng_city_owner") || !data.getUUID("ng_city_owner").equals(player.getUUID()) && !player.hasPermissions(2))
                return "Nur der Ersteller oder ein Administrator kann dieses Gebäude einpacken.";
            if(!player.getMainHandItem().isEmpty())return "Zum Einpacken bitte die Haupthand leeren.";
            if(player.distanceToSqr(Vec3Center.x(target.sign),Vec3Center.y(target.sign),Vec3Center.z(target.sign))>64)
                return "Das Gebäudeschild ist zu weit entfernt.";
        }
        if(target.geometry.kind()==BuildingKind.HOTEL){var h=de.nevergate.city.economy.HotelService.ensure(target.employee);if(h!=null&&de.nevergate.city.economy.HotelService.busy(h))return "Zuerst alle Buchungen und aufbewahrten Fundsachen abschließen.";}
        for(var entry:target.geometry.blocks().entrySet()) {
            BlockPos pos=entry.getKey();
            if(!level.hasChunkAt(pos))return "Zum Einpacken muss das ganze Gebäude geladen sein.";
            if(player!=null && (!player.mayBuild() || !level.mayInteract(player,pos)))return "Hier fehlt dir die Abbauberechtigung.";
            BlockState actual=level.getBlockState(pos),expected=entry.getValue();
            // Opening a door is ordinary use, not a structural modification.
            if(actual.getBlock() instanceof DoorBlock && expected.getBlock()==actual.getBlock()) {
                actual=actual.setValue(DoorBlock.OPEN,expected.getValue(DoorBlock.OPEN))
                    .setValue(DoorBlock.POWERED,expected.getValue(DoorBlock.POWERED));
            }
            if(!actual.equals(expected))return "Gebäude verändert bei "+pos.getX()+", "+pos.getY()+", "+pos.getZ()+": "+actual.getBlock().getName().getString()+" statt "+expected.getBlock().getName().getString()+". Erst diesen Block wiederherstellen.";
            if(level.getBlockEntity(pos) instanceof Container inventory && !inventory.isEmpty())
                return "Bitte zuerst alle Kisten, Fässer und sonstigen Lager leeren.";
        }
        AABB interior=interiorBounds(target.geometry);
        if(!level.getEntities((Entity)null,target.geometry.bounds(),entity -> entity.isAlive() && entity!=target.employee
                && (entity!=player || entity.getBoundingBox().intersects(interior))).isEmpty())
            return "Bitte zuerst alle Personen, Tiere und Gegenstände aus dem Gebäude entfernen.";
        return null;
    }
    /** The outline contains an exterior entrance step. The owner may stand there to reach the sign. */
    private static AABB interiorBounds(PreviewGeometry g) {
        AABB b=g.bounds();
        return switch(g.into()) {
            case SOUTH -> new AABB(b.minX,b.minY,b.minZ+1,b.maxX,b.maxY,b.maxZ);
            case NORTH -> new AABB(b.minX,b.minY,b.minZ,b.maxX,b.maxY,b.maxZ-1);
            case EAST -> new AABB(b.minX+1,b.minY,b.minZ,b.maxX,b.maxY,b.maxZ);
            case WEST -> new AABB(b.minX,b.minY,b.minZ,b.maxX-1,b.maxY,b.maxZ);
            default -> throw new IllegalArgumentException("Horizontal building direction required");
        };
    }
    private static void report(ServerPlayer player,String message) {
        player.displayClientMessage(Component.literal(message),true);
        player.sendSystemMessage(Component.literal("NG-City: "+message));
    }
    private static final class Vec3Center {
        static double x(BlockPos p){return p.getX()+.5;}static double y(BlockPos p){return p.getY()+.5;}static double z(BlockPos p){return p.getZ()+.5;}
    }
    private static boolean postBreak(ServerLevel level,BlockPos pos,ServerPlayer player){
        de.nevergate.city.economy.HotelEvents.packing=true;
        try{return MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level,pos,level.getBlockState(pos),player));}
        finally{de.nevergate.city.economy.HotelEvents.packing=false;}
    }
    public static boolean pack(ServerLevel level,Target target,ServerPlayer player) {
        String failure=validate(level,target,player);
        if(failure!=null) { if(player!=null)report(player,failure);return false; }
        // Ask protection integrations before any mutation. No block drops or experience are generated.
        if(player!=null)for(var entry:target.geometry.blocks().entrySet()) {
            if(!entry.getValue().isAir() && postBreak(level,entry.getKey(),player)) {
                report(player,"Abbau durch Gebietsschutz abgebrochen.");return false;
            }
        }
        // Protection handlers may change the world; recheck the complete target before committing.
        String recheck=validate(level,target,player);
        if(recheck!=null){if(player!=null)report(player,recheck);return false;}
        List<BlockSnapshot> snapshots=new ArrayList<>();
        for(var entry:target.geometry.blocks().entrySet())if(!entry.getValue().isAir())snapshots.add(BlockSnapshot.create(level.dimension(),level,entry.getKey()));
        try {
            for(BlockSnapshot snapshot:snapshots) {
                if(!level.setBlock(snapshot.getPos(),Blocks.AIR.defaultBlockState(),2) && !level.getBlockState(snapshot.getPos()).isAir()) {
                    Placement.rollback(snapshots);return false;
                }
            }
        }catch(RuntimeException error){Placement.rollback(snapshots);throw error;}
        if(target.geometry.kind()==BuildingKind.HOTEL){var h=de.nevergate.city.economy.HotelService.ensure(target.employee);if(h!=null){var data=de.nevergate.city.economy.EconomyData.get(level);data.hotels.remove(h.id);data.flush(level);}}
        target.employee.discard();
        if(player!=null) {
            Item item=switch(target.geometry.kind()){case BANK->NgCity.BANK.get();case HOTEL->NgCity.HOTEL.get();case BUYER->NgCity.BUYER.get();};
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(item));
            player.sendSystemMessage(Component.literal(target.geometry.kind().title+" eingepackt. Du kannst das Gebäudepaket erneut setzen."));
        }
        return true;
    }
    public static void interact(PlayerInteractEvent.RightClickBlock event) {
        if(event.getHand()!=InteractionHand.MAIN_HAND || !event.getEntity().isShiftKeyDown()
            || !(event.getEntity() instanceof ServerPlayer player))return;
        ServerLevel level=player.serverLevel();
        if(signKind(level,event.getPos())==null)return;
        event.setCanceled(true);event.setCancellationResult(InteractionResult.SUCCESS);
        pack(level,resolve(level,event.getPos()),player);
    }
}
