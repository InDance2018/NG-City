import java.nio.file.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
public final class PatchOccupancy implements Opcodes {
 public static void main(String[] args)throws Exception{
  String key="de/nevergate/city/Placement.class";byte[] b;try(ZipFile z=new ZipFile(args[0])){b=z.getInputStream(z.getEntry(key)).readAllBytes();}
  ClassNode n=new ClassNode();new ClassReader(b).accept(n,0);int queries=0,messages=0;
  for(MethodNode m:n.methods)if(m.name.equals("validate"))for(AbstractInsnNode a:m.instructions.toArray()){
   if(a instanceof MethodInsnNode c && c.name.equals("m_6249_") && c.desc.equals("(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;")){
    m.instructions.set(c,new MethodInsnNode(INVOKESTATIC,"de/nevergate/city/PlacementOccupancy","query","(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",false));queries++;
   }
   if(a instanceof LdcInsnNode c && c.cst.equals("Eine Person oder ein Lebewesen steht im Bauplatz.")){
    m.instructions.set(c,new MethodInsnNode(INVOKESTATIC,"de/nevergate/city/PlacementOccupancy","message","()Ljava/lang/String;",false));messages++;
   }
  }
  if(queries!=1||messages!=1)throw new AssertionError(queries+" queries, "+messages+" messages");
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);n.accept(w);b=w.toByteArray();ClassNode check=new ClassNode();new ClassReader(b).accept(check,0);
  for(MethodNode m:check.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<>(new BasicVerifier()).analyze(check.name,m);
  Path out=Path.of(args[1],key);Files.createDirectories(out.getParent());Files.write(out,b);System.out.println("Query and reason replaced; existing predicate and bounds unchanged; bytecode verified");
 }
}
