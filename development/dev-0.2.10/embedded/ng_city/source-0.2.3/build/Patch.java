import java.util.*;import java.util.jar.*;import java.nio.file.*;import java.io.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
public class Patch implements Opcodes {
 static String P="de/nevergate/city/", A=P+"Appearance";static MethodInsnNode call(String n,String d){return new MethodInsnNode(INVOKESTATIC,A,n,d,false);}static final String B="L"+P+"BuildingPlan;",V="Lnet/minecraft/world/entity/npc/Villager;",D="Lnet/minecraft/core/Direction;",S="Lnet/minecraft/server/level/ServerLevel;",POS="Lnet/minecraft/core/BlockPos;",T="L"+P+"BuildingPacking$Target;";
 static void retHook(MethodNode m,String name,String desc){for(var i:m.instructions.toArray())if(i.getOpcode()==ARETURN){InsnList x=new InsnList();x.add(new InsnNode(DUP));x.add(new VarInsnNode(ALOAD,3));x.add(call(name,desc));m.instructions.insertBefore(i,x);}}
 static void prefix(MethodNode m,String name,String desc,String target,int...args){InsnList x=new InsnList();for(int a:args)x.add(new VarInsnNode(ALOAD,a));x.add(call(name,desc));x.add(new InsnNode(DUP));LabelNode no=new LabelNode();x.add(new JumpInsnNode(IFNULL,no));x.add(new InsnNode(ARETURN));x.add(no);x.add(new FrameNode(F_SAME1,0,null,1,new Object[]{target}));x.add(new InsnNode(POP));m.instructions.insert(x);}
 public static void main(String[] args)throws Exception{
 Map<String,byte[]> files=new LinkedHashMap<>();try(JarFile j=new JarFile(args[0])){for(var e:Collections.list(j.entries()))files.put(e.getName(),j.getInputStream(e).readAllBytes());}
 for(String name:List.of("BuildingPlan","ComplexPlan","ComplexPlacement","BuildingItem","BuildingPacking","economy/HotelService","digital/DigitalBlocks$Panel","client/PanelRenderer")){
  String key=P+name+".class";ClassNode n=new ClassNode();new ClassReader(files.get(key)).accept(n,0);
  if(name.equals("BuildingPlan"))n.fields.add(new FieldNode(ACC_PUBLIC|ACC_FINAL,"layoutVersion","I",null,null));
  List<MethodNode> added=new ArrayList<>();
  for(MethodNode m:n.methods){
   if(name.equals("BuildingPlan")&&m.name.equals("<init>")){
    if(m.desc.equals("("+B.replace("BuildingPlan","BuildingKind")+"I)V")){for(var i:m.instructions.toArray())if(i instanceof MethodInsnNode q&&q.name.equals("<init>")&&q.owner.equals("java/lang/Object")){InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,0));x.add(new VarInsnNode(ILOAD,2));x.add(new FieldInsnNode(PUTFIELD,n.name,"layoutVersion","I"));m.instructions.insert(i,x);break;}}
    if(m.desc.equals("(L"+P+"BuildingKind;)V"))for(var i:m.instructions.toArray())if(i.getOpcode()==ICONST_4)m.instructions.set(i,new InsnNode(ICONST_5));
   }
   if(name.equals("ComplexPlan")&&m.name.equals("build")){
    m.name="buildLegacy";
    for(var i:m.instructions.toArray())if(i instanceof MethodInsnNode q&&q.owner.equals(P+"BuildingPlan")&&q.name.equals("<init>")&&q.desc.equals("(L"+P+"BuildingKind;)V")){InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,0));x.add(new FieldInsnNode(GETFIELD,P+"BuildingPlan","layoutVersion","I"));m.instructions.insertBefore(i,x);q.desc="(L"+P+"BuildingKind;I)V";}
    MethodNode fresh=new MethodNode(ACC_PUBLIC|ACC_STATIC,"build","("+B+")V",null,null);fresh.instructions.add(new VarInsnNode(ALOAD,0));fresh.instructions.add(new MethodInsnNode(INVOKESTATIC,n.name,"buildLegacy","("+B+")V",false));fresh.instructions.add(new VarInsnNode(ALOAD,0));fresh.instructions.add(call("finishComplex","("+B+")V"));fresh.instructions.add(new InsnNode(RETURN));added.add(fresh);
   }
   if(name.equals("ComplexPlacement")){
    if(m.name.equals("tag"))for(var i:m.instructions.toArray())if(i.getOpcode()==ICONST_4)m.instructions.set(i,new InsnNode(ICONST_5));
    if(m.name.equals("employees")){retHook(m,"alignAll","(Ljava/util/List;"+D+")V");for(var i:m.instructions.toArray())if(i instanceof LdcInsnNode l&&"ng_city_layout".equals(l.cst)){var z=i.getNext();while(z!=null&&z.getOpcode()<0)z=z.getNext();if(z!=null&&z.getOpcode()==ICONST_4)m.instructions.set(z,new InsnNode(ICONST_5));}}
    if(m.name.equals("resolve"))prefix(m,"resolve5","("+S+POS+D+POS+")"+T,P+"BuildingPacking$Target",0,1,2,3);
   }
   if(name.equals("BuildingItem")&&m.name.equals("m_6225_"))for(var i:m.instructions.toArray())if(i instanceof LdcInsnNode l&&"ng_city_layout".equals(l.cst)){var z=i.getNext();while(z!=null&&z.getOpcode()<0)z=z.getNext();if(z!=null&&z.getOpcode()==ICONST_4)m.instructions.set(z,new InsnNode(ICONST_5));}
   if(name.equals("BuildingPacking")&&m.name.equals("signKind"))prefix(m,"panelKind","("+S+POS+")L"+P+"BuildingKind;",P+"BuildingKind",0,1);
   if(name.equals("economy/HotelService")&&m.name.equals("ensure"))for(var i:m.instructions.toArray())if(i instanceof MethodInsnNode q&&q.owner.equals(n.name)&&q.name.equals("install")){InsnList x=new InsnList();x.add(new InsnNode(DUP));x.add(new VarInsnNode(ALOAD,0));x.add(call("hotelLayout","(L"+P+"economy/EconomyData$Hotel;"+V+")V"));m.instructions.insertBefore(i,x);}
   if(name.equals("client/PanelRenderer"))for(var i:m.instructions.toArray())if(i instanceof LdcInsnNode l&&l.cst instanceof Double d&&Math.abs(d-.505)<.00001)l.cst=-.4315;
  }
  if(name.equals("digital/DigitalBlocks$Panel"))for(String method:List.of("m_5940_","m_5939_")){MethodNode m=new MethodNode(ACC_PUBLIC,method,"(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;"+POS+"Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",null,null);m.instructions.add(new VarInsnNode(ALOAD,1));m.instructions.add(call("panelShape","(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/shapes/VoxelShape;"));m.instructions.add(new InsnNode(ARETURN));added.add(m);}
  n.methods.addAll(added);ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);n.accept(w);files.put(key,w.toByteArray());System.out.println("Patched "+name);
 }
 try(JarOutputStream out=new JarOutputStream(Files.newOutputStream(Path.of(args[1])))){for(var e:files.entrySet()){out.putNextEntry(new JarEntry(e.getKey()));out.write(e.getValue());out.closeEntry();}}
 // Compile-only vanilla Entity extension matching Forge's existing public API. Never distributed.
 ClassNode entity;try(JarFile j=new JarFile(args[2])){entity=new ClassNode();new ClassReader(j.getInputStream(j.getJarEntry("net/minecraft/world/entity/Entity.class"))).accept(entity,0);}
 MethodNode ext=new MethodNode(ACC_PUBLIC,"getPersistentData","()Lnet/minecraft/nbt/CompoundTag;",null,null);ext.instructions.add(new InsnNode(ACONST_NULL));ext.instructions.add(new InsnNode(ARETURN));entity.methods.add(ext);ClassWriter ew=new ClassWriter(ClassWriter.COMPUTE_MAXS);entity.accept(ew);Path dest=Path.of(args[3],"net/minecraft/world/entity/Entity.class");Files.createDirectories(dest.getParent());Files.write(dest,ew.toByteArray());
 }
}
