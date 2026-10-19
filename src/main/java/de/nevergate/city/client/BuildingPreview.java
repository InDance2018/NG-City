package de.nevergate.city.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.nevergate.city.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Client-only subscriber: a dedicated server never loads Minecraft client classes. */
@Mod.EventBusSubscriber(modid=NgCity.ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class BuildingPreview {
    private static PreviewGeometry preview;
    private static Object world;
    private static String reason;
    private static long checked;
    private BuildingPreview() { }
    private static BuildingItem held(Minecraft mc) {
        if(mc.player==null || mc.level==null || mc.screen!=null || mc.options.hideGui) return null;
        ItemStack main=mc.player.getMainHandItem(),off=mc.player.getOffhandItem();
        return main.getItem() instanceof BuildingItem b ? b : off.getItem() instanceof BuildingItem b ? b : null;
    }
    private static PreviewGeometry update(Minecraft mc,BuildingItem item) {
        if(!(mc.hitResult instanceof BlockHitResult hit) || hit.getType()!=HitResult.Type.BLOCK) { preview=null;return null; }
        BlockPos origin=hit.getBlockPos().above();Direction into=mc.player.getDirection();
        boolean changed=preview==null || world!=mc.level || preview.kind()!=item.kind || !preview.origin().equals(origin) || preview.into()!=into;
        long now=System.nanoTime();
        if(changed) { preview=PreviewGeometry.create(item.kind,origin,into);world=mc.level;checked=0; }
        if(changed || now-checked>100_000_000L) {
            reason=hit.getDirection()!=Direction.UP ? "Auf die Oberseite des Bodens zielen" : Placement.validate(mc.level,preview.blocks(),mc.player,null);
            checked=now;
        }
        // A face change on the same block must immediately invalidate a former green preview.
        if(hit.getDirection()!=Direction.UP) reason="Auf die Oberseite des Bodens zielen";
        return preview;
    }
    @SubscribeEvent public static void render(RenderLevelStageEvent event) {
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc=Minecraft.getInstance();BuildingItem item=held(mc);
        if(item==null) { preview=null;world=null;return; }
        PreviewGeometry p=update(mc,item);if(p==null)return;
        PoseStack pose=event.getPoseStack();Vec3 camera=event.getCamera().getPosition();
        pose.pushPose();pose.translate(-camera.x,-camera.y,-camera.z);
        var buffers=mc.renderBuffers().bufferSource();var lines=buffers.getBuffer(RenderType.lines());
        float r=reason==null ? .2f : 1f,g=reason==null ? 1f : .2f;
        LevelRenderer.renderLineBox(pose,lines,p.bounds().inflate(.01),r,g,.2f,1f);
        AABB b=p.bounds();
        LevelRenderer.renderLineBox(pose,lines,new AABB(b.minX,b.minY+.025,b.minZ,b.maxX,b.minY+.065,b.maxZ),r,g,.2f,1f);
        LevelRenderer.renderLineBox(pose,lines,p.entrance().inflate(.025),.15f,.8f,1f,1f);
        buffers.endBatch(RenderType.lines());pose.popPose();
    }
    @SubscribeEvent public static void hud(RenderGuiEvent.Post event) {
        Minecraft mc=Minecraft.getInstance();BuildingItem item=held(mc);if(item==null)return;
        PreviewGeometry p=update(mc,item);
        String size=item.kind.title+" · "+item.kind.width+" × "+(item.kind.depth+1)+" × "+item.kind.height+" Blöcke (B × T × H)";
        String status=p==null ? "Auf den Boden zielen – Rechtsklick zum Setzen" : reason==null ? "Bauplatz frei · Eingang türkis · Rechtsklick zum Setzen" : reason;
        var gui=event.getGuiGraphics();int center=mc.getWindow().getGuiScaledWidth()/2;
        int y=mc.getWindow().getGuiScaledHeight()-76;
        gui.fill(center-Math.min(center-4,mc.font.width(size)/2+8),y-4,center+Math.min(center-4,mc.font.width(size)/2+8),y+32,0xAA000000);
        gui.drawCenteredString(mc.font,size,center,y,0xFFFFFF);
        gui.drawCenteredString(mc.font,status,center,y+11,p!=null && reason!=null ? 0xFF7777 : 0x88FF88);
        gui.drawCenteredString(mc.font,"Endgültige Platzprüfung beim Setzen",center,y+22,0xBBBBBB);
    }
}
