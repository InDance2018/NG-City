package de.nevergate.city;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.*;
public final class CityMenus {
 public static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(ForgeRegistries.MENU_TYPES,NgCity.ID);
 public static final RegistryObject<MenuType<ServiceMenu>> SERVICE=MENUS.register("digital_service",()->IForgeMenuType.create(ServiceMenu::new));
 private CityMenus(){}
}
