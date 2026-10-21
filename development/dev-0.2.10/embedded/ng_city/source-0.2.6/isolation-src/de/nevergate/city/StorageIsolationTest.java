package de.nevergate.city;
import java.nio.file.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
public class StorageIsolationTest {
 static int checks;static void ok(boolean b){checks++;if(!b)throw new AssertionError("storage isolation "+checks);}
 public static void main(String[] args)throws Exception {
  net.minecraft.SharedConstants.m_142977_();
  Path root=Files.createTempDirectory("ng-city-world-isolation-");
  ServerLevel a=new ServerLevel(Files.createDirectories(root.resolve("server-a/any-name-1")).toFile());
  ServerLevel b=new ServerLevel(Files.createDirectories(root.resolve("server-a/any-name-2")).toFile());
  ServerLevel c=new ServerLevel(Files.createDirectories(root.resolve("server-b/any-name-1")).toFile());
  StarterLedger la=StarterLedger.get(a),lb=StarterLedger.get(b),lc=StarterLedger.get(c);
  ok(la!=lb);ok(la!=lc);ok(lb!=lc);
  la.markCommunal(new BlockPos(10,64,20));la.observeSpawn(new BlockPos(0,64,0));la.bindWorld("arbitrary:alpha");
  ok(la.distributed());ok(!lb.distributed());ok(!lc.distributed());
  lb.markCommunal(new BlockPos(-5000,80,9000));lb.bindWorld("arbitrary:beta");
  ok(la.location().equals(new BlockPos(10,64,20)));ok(lb.location().equals(new BlockPos(-5000,80,9000)));ok(lc.location()==null);
  ok(StarterLedger.get(a)==la);ok(StarterLedger.get(b)==lb);ok(StarterLedger.get(c)==lc);
  a.m_8895_().m_78151_();b.m_8895_().m_78151_();
  ok(Files.exists(root.resolve("server-a/any-name-1/ng_city_starters.dat")));
  ok(Files.exists(root.resolve("server-a/any-name-2/ng_city_starters.dat")));
  System.out.println("WORLD/SERVER STORAGE ISOLATION PASSED: "+checks);
 }
}
