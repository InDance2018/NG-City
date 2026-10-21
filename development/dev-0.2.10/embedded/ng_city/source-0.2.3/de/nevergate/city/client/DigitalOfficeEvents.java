package de.nevergate.city.client;
import net.minecraft.client.Minecraft;import net.minecraft.client.gui.screens.inventory.ContainerScreen;import net.minecraftforge.api.distmarker.Dist;import net.minecraftforge.fml.common.Mod;import net.minecraftforge.eventbus.api.SubscribeEvent;import net.minecraftforge.client.event.ScreenEvent;
@Mod.EventBusSubscriber(modid="ng_city",value=Dist.CLIENT)
public final class DigitalOfficeEvents {
 @SubscribeEvent public static void opening(ScreenEvent.Opening event){if(!(event.getNewScreen() instanceof ContainerScreen old))return;String title=old.m_96636_().getString();if(!(title.startsWith("NG · ")||title.equals("Verwaltung einrichten")))return;var mc=Minecraft.m_91087_();if(mc.f_91074_!=null)event.setNewScreen(new DigitalOfficeScreen(old.m_6262_(),mc.f_91074_.m_150109_(),old.m_96636_()));}
}
