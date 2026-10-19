package de.nevergate.city.client;
import de.nevergate.city.*;
import de.nevergate.city.economy.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=NgCity.ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.MOD)
public final class HotelDisplayRenderer implements BlockEntityRenderer<HotelDisplayEntity> {
 private final Font font;
 public HotelDisplayRenderer(BlockEntityRendererProvider.Context ctx){font=ctx.getFont();}
 @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(CityBlocks.DISPLAY_ENTITY.get(),HotelDisplayRenderer::new);}
 @Override public void render(HotelDisplayEntity be,float tick,PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.pushPose();pose.translate(.5,.5,.5);pose.mulPose(Axis.YP.rotationDegrees(-be.getBlockState().getValue(CityBlocks.FACING).toYRot()));pose.translate(0,0,.515);pose.scale(.009f,-.009f,.009f);
  draw("NG HOTEL · ZIMMERÜBERSICHT",-48,0xFFD66B,pose,buffers);draw(be.lines[0],-12,be.occupied[0]?0xFFB464:0x62F292,pose,buffers);draw(be.lines[1],12,be.occupied[1]?0xFFB464:0x62F292,pose,buffers);draw("Buchung: 1–7 Minecraft-Tage",44,0xB8CCDA,pose,buffers);pose.popPose();}
 private void draw(String text,float y,int color,PoseStack p,MultiBufferSource b){font.drawInBatch(text,-font.width(text)/2f,y,color,false,p.last().pose(),b,Font.DisplayMode.NORMAL,0,15728880);}
 @Override public int getViewDistance(){return 48;}
}
