import java.nio.file.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
public final class PatchPlacement implements Opcodes {
    static final String P="de/nevergate/city/",D=P+"PlacementDiagnostics";
    static MethodInsnNode call(String n,String d){return new MethodInsnNode(INVOKESTATIC,D,n,d,false);}
    static void stage(MethodNode m,AbstractInsnNode at,String text){InsnList x=new InsnList();x.add(new LdcInsnNode(text));x.add(call("stage","(Ljava/lang/String;)V"));m.instructions.insertBefore(at,x);}
    public static void main(String[] args)throws Exception {
        for(String name:new String[]{"ComplexPlacement","Placement"}){
            byte[] bytes;try(ZipFile z=new ZipFile(args[0])){bytes=z.getInputStream(z.getEntry(P+name+".class")).readAllBytes();}
            ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);
            int diagnostics=0;
            for(MethodNode method:node.methods){
                if(name.equals("ComplexPlacement")&&method.name.equals("place")){
                    method.name="placeInternal";
                    for(var instruction:method.instructions.toArray())if(instruction instanceof MethodInsnNode call){
                        if(call.owner.equals(P+"Placement")&&call.name.equals("validate")){
                            method.instructions.insert(call,call("validation","(Ljava/lang/String;)Ljava/lang/String;"));diagnostics++;
                        }
                        if(call.owner.equals(P+"ComplexPlacement")&&call.name.equals("employees"))stage(method,call,"Mitarbeiter erzeugen");
                        if(call.owner.equals(P+"Placement")&&call.name.equals("apply"))stage(method,call,"Blöcke setzen und Schutzfreigabe prüfen");
                        if(call.owner.equals("net/minecraft/server/level/ServerLevel")&&call.name.equals("m_7967_"))stage(method,call,"Mitarbeiter in die Welt einsetzen");
                        if(call.owner.equals(P+"economy/HotelService")&&call.name.equals("ensure"))stage(method,call,"Hotel registrieren");
                        if(call.owner.equals(P+"digital/NetworkData")&&call.name.equals("register"))stage(method,call,"Verwaltungsstandorte registrieren");
                    }
                    // The original catch rolls everything back, but previously hid its exception.
                    for(var catcher:method.tryCatchBlocks)if("java/lang/RuntimeException".equals(catcher.type)){
                        AbstractInsnNode at=catcher.handler.getNext();while(at!=null&&at.getOpcode()<0)at=at.getNext();
                        if(at==null||at.getOpcode()!=ASTORE)throw new AssertionError("catch shape");
                        InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,((VarInsnNode)at).var));x.add(call("exception","(Ljava/lang/Throwable;)V"));method.instructions.insert(at,x);diagnostics++;
                    }
                }
                if(name.equals("Placement")&&method.name.equals("apply")){
                    for(var instruction:method.instructions.toArray())if(instruction instanceof MethodInsnNode call&&call.name.equals("post")&&call.desc.endsWith(")Z")){
                        method.instructions.insert(call,call("protectedEvent","(Z)Z"));diagnostics++;
                    }
                }
            }
            if(name.equals("ComplexPlacement")){
                if(diagnostics!=2)throw new AssertionError("complex hooks "+diagnostics);
                String ctx="Lnet/minecraft/world/item/context/UseOnContext;",res="Lnet/minecraft/world/InteractionResult;";
                MethodNode wrapper=new MethodNode(ACC_PUBLIC|ACC_STATIC,"place","("+ctx+")"+res,null,null);
                LabelNode start=new LabelNode(),end=new LabelNode(),handler=new LabelNode();
                wrapper.instructions.add(new VarInsnNode(ALOAD,0));wrapper.instructions.add(call("begin","("+ctx+")V"));
                wrapper.instructions.add(start);wrapper.instructions.add(new VarInsnNode(ALOAD,0));
                wrapper.instructions.add(new MethodInsnNode(INVOKESTATIC,node.name,"placeInternal","("+ctx+")"+res,false));
                wrapper.instructions.add(new VarInsnNode(ASTORE,1));wrapper.instructions.add(end);
                wrapper.instructions.add(new VarInsnNode(ALOAD,1));wrapper.instructions.add(new VarInsnNode(ALOAD,0));
                wrapper.instructions.add(call("finish","("+res+ctx+")"+res));wrapper.instructions.add(new InsnNode(ARETURN));
                wrapper.instructions.add(handler);wrapper.instructions.add(new FrameNode(F_SAME1,0,null,1,new Object[]{"java/lang/RuntimeException"}));wrapper.instructions.add(new VarInsnNode(ASTORE,1));
                wrapper.instructions.add(new VarInsnNode(ALOAD,1));wrapper.instructions.add(new VarInsnNode(ALOAD,0));
                wrapper.instructions.add(call("failed","(Ljava/lang/RuntimeException;"+ctx+")"+res));wrapper.instructions.add(new InsnNode(ARETURN));
                wrapper.tryCatchBlocks.add(new TryCatchBlockNode(start,end,handler,"java/lang/RuntimeException"));node.methods.add(wrapper);
            } else if(diagnostics!=1)throw new AssertionError("placement hook "+diagnostics);
            // Only the new wrapper needs stack-map generation; it merges no external hierarchy.
            ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS){protected String getCommonSuperClass(String a,String b){return "java/lang/Object";}};
            node.accept(writer);byte[] out=writer.toByteArray();
            ClassNode check=new ClassNode();new ClassReader(out).accept(check,0);
            for(MethodNode method:check.methods)if((method.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<>(new BasicVerifier()).analyze(check.name,method);
            Path file=Path.of(args[1],P+name+".class");Files.createDirectories(file.getParent());Files.write(file,out);
            System.out.println("Verified diagnostic hooks: "+name+" ("+diagnostics+")");
        }
    }
}
