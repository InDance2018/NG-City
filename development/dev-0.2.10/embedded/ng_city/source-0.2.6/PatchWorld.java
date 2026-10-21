import java.nio.file.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
public final class PatchWorld implements Opcodes {
    public static void main(String[] args)throws Exception {
        byte[] bytes;String key="de/nevergate/city/NgCity.class";
        try(ZipFile z=new ZipFile(args[0])){bytes=z.getInputStream(z.getEntry(key)).readAllBytes();}
        ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);int login=0,command=0;
        for(MethodNode method:node.methods)for(var instruction:method.instructions.toArray()) {
            if(!(instruction instanceof MethodInsnNode call)||!call.owner.equals("de/nevergate/city/SharedStarter")||!call.name.equals("ensure"))continue;
            if(method.name.equals("login")) {
                method.instructions.insertBefore(call,new VarInsnNode(ALOAD,1));
                call.owner="de/nevergate/city/StarterWorldAccess";call.name="login";
                call.desc="(Lnet/minecraft/server/MinecraftServer;Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)Lde/nevergate/city/StarterLedger;";login++;
            } else if(method.desc.equals("(Lcom/mojang/brigadier/context/CommandContext;)I")) {
                method.instructions.insertBefore(call,new VarInsnNode(ALOAD,0));
                call.owner="de/nevergate/city/StarterWorldAccess";call.name="command";
                call.desc="(Lnet/minecraft/server/MinecraftServer;Lcom/mojang/brigadier/context/CommandContext;)Lde/nevergate/city/StarterLedger;";command++;
            }
        }
        if(login!=1||command!=1)throw new AssertionError("Expected login=1 command=1, got "+login+" "+command);
        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(writer);
        Path out=Path.of(args[1],key);Files.createDirectories(out.getParent());Files.write(out,writer.toByteArray());
        System.out.println("World-aware call sites patched: login=1, command=1");
    }
}
