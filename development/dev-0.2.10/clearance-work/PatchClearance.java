import java.nio.file.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class PatchClearance implements Opcodes {
 static final String H="de/nevergate/city/PlacementClearance",C="Lnet/minecraft/world/item/context/UseOnContext;",R="Lnet/minecraft/world/InteractionResult;",D="("+C+")"+R;
 static void wrap(ClassNode n,String name){
  MethodNode old=n.methods.stream().filter(m->m.name.equals(name)&&m.desc.equals(D)).findFirst().orElseThrow();boolean stat=(old.access&ACC_STATIC)!=0;old.name="clearance$"+name;int ctx=stat?0:1,tmp=ctx+1;
  MethodNode m=new MethodNode(ASM9,old.access,name,D,null,null);n.methods.add(m);Label start=new Label(),end=new Label(),fail=new Label();
  m.visitVarInsn(ALOAD,ctx);m.visitMethodInsn(INVOKESTATIC,H,"begin","("+C+")V",false);m.visitTryCatchBlock(start,end,fail,"java/lang/Throwable");m.visitLabel(start);
  if(!stat)m.visitVarInsn(ALOAD,0);m.visitVarInsn(ALOAD,ctx);m.visitMethodInsn(stat?INVOKESTATIC:INVOKEVIRTUAL,n.name,old.name,D,false);m.visitVarInsn(ASTORE,tmp);m.visitLabel(end);
  m.visitVarInsn(ALOAD,tmp);m.visitVarInsn(ALOAD,ctx);m.visitMethodInsn(INVOKESTATIC,H,"finish","("+R+C+")"+R,false);m.visitInsn(ARETURN);
  m.visitLabel(fail);m.visitFrame(F_SAME1,0,null,1,new Object[]{"java/lang/Throwable"});m.visitVarInsn(ASTORE,tmp);m.visitVarInsn(ALOAD,ctx);m.visitMethodInsn(INVOKESTATIC,H,"abort","("+C+")V",false);m.visitVarInsn(ALOAD,tmp);m.visitInsn(ATHROW);m.visitMaxs(0,0);m.visitEnd();
 }
 public static void main(String[] a)throws Exception{int config=0,hint=0,wraps=0;try(ZipFile z=new ZipFile(a[0])){for(String key:new String[]{"de/nevergate/city/ComplexPlacement","de/nevergate/city/BuildingItem","de/nevergate/city/digital/OfficeItem","de/nevergate/city/economy/CityConfig","de/nevergate/city/client/BuildingPreview"}){
 ClassNode n=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(key+".class")).readAllBytes()).accept(n,0);
 if(key.endsWith("ComplexPlacement")){wrap(n,"place");wraps++;}else if(key.endsWith("Item")){wrap(n,"m_6225_");wraps++;}
 for(MethodNode m:n.methods)for(AbstractInsnNode i:m.instructions.toArray()){
 if(key.endsWith("CityConfig")&&i instanceof MethodInsnNode c&&c.name.equals("build")&&c.owner.equals("net/minecraftforge/common/ForgeConfigSpec$Builder")){m.instructions.insertBefore(i,new MethodInsnNode(INVOKESTATIC,H,"configure","(Lnet/minecraftforge/common/ForgeConfigSpec$Builder;)Lnet/minecraftforge/common/ForgeConfigSpec$Builder;",false));config++;}
 if(key.endsWith("BuildingPreview")&&i instanceof LdcInsnNode c&&c.cst.equals("Bauplatz frei · Eingang türkis · Rechtsklick zum Setzen")){m.instructions.set(i,new MethodInsnNode(INVOKESTATIC,H,"previewHint","()Ljava/lang/String;",false));hint++;}
 }
 ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);n.accept(w);byte[] b=w.toByteArray();ClassNode check=new ClassNode();new ClassReader(b).accept(check,0);for(MethodNode m:check.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<>(new BasicVerifier()).analyze(check.name,m);
 Path out=Path.of(a[1],key+".class");Files.createDirectories(out.getParent());Files.write(out,b);
 }}if(wraps!=3||config!=1||hint!=1)throw new AssertionError(wraps+"/"+config+"/"+hint);System.out.println("CLEARANCE_PATCH_OK wrappers=3 config=1 preview=1; ASM BasicVerifier passed");}
}
