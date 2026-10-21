import java.nio.file.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

public final class PatchNpc implements Opcodes {
    public static void main(String[] args)throws Exception {
        for(String name:new String[]{"ComplexPlacement","BuildingItem","digital/OfficeItem"}) {
            String key="de/nevergate/city/"+name+".class";byte[] bytes;
            try(ZipFile z=new ZipFile(args[0])){bytes=z.getInputStream(z.getEntry(key)).readAllBytes();}
            ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);int count=0;
            for(MethodNode method:node.methods)for(var instruction:method.instructions.toArray()) {
                if(instruction instanceof MethodInsnNode call && call.owner.equals("net/minecraft/server/level/ServerLevel")
                    && call.name.equals("m_7967_") && call.desc.equals("(Lnet/minecraft/world/entity/Entity;)Z")) {
                    method.instructions.set(call,new MethodInsnNode(INVOKESTATIC,"de/nevergate/city/EmployeeSpawner","add",
                        "(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)Z",false));count++;
                }
            }
            if(count!=1)throw new AssertionError(name+": expected one employee insertion, got "+count);
            ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(writer);byte[] result=writer.toByteArray();
            ClassNode check=new ClassNode();new ClassReader(result).accept(check,0);
            for(MethodNode method:check.methods)if((method.access&(ACC_NATIVE|ACC_ABSTRACT))==0)new Analyzer<>(new BasicVerifier()).analyze(check.name,method);
            Path out=Path.of(args[1],key);Files.createDirectories(out.getParent());Files.write(out,result);
            System.out.println("NPC insertion patched and verified: "+name);
        }
    }
}
