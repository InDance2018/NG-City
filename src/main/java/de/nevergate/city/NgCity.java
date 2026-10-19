package de.nevergate.city;

import net.minecraft.commands.Commands;
import de.nevergate.city.economy.*;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

@Mod(NgCity.ID)
public final class NgCity {
    public static final String ID = "ng_city";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final RegistryObject<Item> STARTER = ITEMS.register("startkiste", () -> new StarterCrateItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BANK = ITEMS.register("bank", () -> new BuildingItem(BuildingKind.BANK));
    public static final RegistryObject<Item> HOTEL = ITEMS.register("hotel", () -> new BuildingItem(BuildingKind.HOTEL));
    public static final RegistryObject<Item> BUYER = ITEMS.register("ankauf", () -> new BuildingItem(BuildingKind.BUYER));
    public static final List<RegistryObject<Item>> CASH=new ArrayList<>();
    static { for(String id:Money.IDS) CASH.add(ITEMS.register(id,()->new Item(new Item.Properties()))); }
    public static final RegistryObject<Item> WALLET=ITEMS.register("geldboerse",BoundItem::new);
    public static final RegistryObject<Item> CARD=ITEMS.register("kreditkarte",BoundItem::new);
    public static final RegistryObject<Item> KEY=ITEMS.register("zimmerschluessel",()->new Item(new Item.Properties().stacksTo(1)));
    static { for(var block:List.of(CityBlocks.ATM,CityBlocks.TERMINAL,CityBlocks.LUGGAGE,CityBlocks.DISPLAY,CityBlocks.ROOM_DOOR))ITEMS.register(block.getId().getPath(),()->new BlockItem(block.get(),new Item.Properties())); }
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public NgCity() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        CityMenus.MENUS.register(bus);
        CityBlocks.BLOCKS.register(bus); CityBlocks.ENTITIES.register(bus);
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER,CityConfig.SPEC);
        TABS.register("city", () -> CreativeModeTab.builder().title(Component.literal("NG-City"))
                .icon(() -> new ItemStack(BANK.get())).displayItems((p, out) -> {
                    out.accept(BANK.get()); out.accept(HOTEL.get()); out.accept(BUYER.get()); out.accept(Guide.create()); CASH.forEach(item->out.accept(item.get())); out.accept(WALLET.get());out.accept(CARD.get());out.accept(KEY.get());for(var block:List.of(CityBlocks.ATM,CityBlocks.TERMINAL,CityBlocks.LUGGAGE,CityBlocks.DISPLAY,CityBlocks.ROOM_DOOR))out.accept(block.get().asItem());
                }).build());
        TABS.register(bus);
        MinecraftForge.EVENT_BUS.addListener(this::login);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
        MinecraftForge.EVENT_BUS.addListener(this::interact);
        MinecraftForge.EVENT_BUS.addListener(BuildingPacking::interact);
        MinecraftForge.EVENT_BUS.addListener(this::protectEmployee);
        MinecraftForge.EVENT_BUS.addListener(this::employeeTick);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
    }
    private void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var ledger=SharedStarter.ensure(player.serverLevel().getServer());
        player.sendSystemMessage(Component.literal(SharedStarter.message(ledger)));
    }
    private void serverStarted(net.minecraftforge.event.server.ServerStartedEvent event){SharedStarter.ensure(event.getServer());}
    private void interact(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Villager npc)) return;
        BuildingKind kind = BuildingKind.fromId(npc.getPersistentData().getString("ng_city_role"));
        if (kind == null) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (event.getEntity() instanceof ServerPlayer player && event.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND)
            ServiceMenu.open(player, npc, kind);
    }
    private void employeeTick(net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Villager npc) || npc.level().isClientSide) return;
        CompoundTag data = npc.getPersistentData();
        if (BuildingKind.fromId(data.getString("ng_city_role")) == null || !data.contains("ng_city_anchor")) return;
        Placement.alignEmployee(npc);
        var anchor = net.minecraft.core.BlockPos.of(data.getLong("ng_city_anchor"));
        if (npc.distanceToSqr(anchor.getX()+.5,anchor.getY(),anchor.getZ()+.5) > .001)
            npc.teleportTo(anchor.getX()+.5,anchor.getY(),anchor.getZ()+.5);
        if(npc.tickCount%20==0){
            if(data.getString("ng_city_role").equals("hotel"))HotelService.ensure(npc);
            if(data.getString("ng_city_role").equals("bank")){
                var dir=Placement.employeeDirection(npc);var origin=anchor.below().relative(dir.getOpposite(),5);
                var at=new BuildingPlan(BuildingKind.BANK).worldPos(origin,dir,new net.minecraft.core.BlockPos(8,1,6));
                if(npc.level().getBlockState(at).isAir())npc.level().setBlock(at,CityBlocks.ATM.get().defaultBlockState().setValue(CityBlocks.FACING,dir.getOpposite()),3);
            }
        }
        npc.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
    }
    private void protectEmployee(LivingAttackEvent event) {
        if (event.getEntity().getPersistentData().contains("ng_city_role")
                && !event.getSource().is(net.minecraft.world.damagesource.DamageTypes.GENERIC_KILL)
                && !event.getSource().is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD)) event.setCanceled(true);
    }
    private void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ngcity")
            .then(Commands.literal("anleitung").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                if (!p.getInventory().add(Guide.create())) return 0;
                return 1;
            }))
            .then(Commands.literal("startkiste").requires(src -> src.hasPermission(2)).executes(ctx -> {
                var ledger=SharedStarter.ensure(ctx.getSource().getServer());
                ctx.getSource().sendSuccess(()->Component.literal(SharedStarter.message(ledger)),false);
                return ledger.distributed()?1:0;
            })));
    }
}
