package de.nevergate.city;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public final class SharedStarter {
   public static StarterLedger ensure(MinecraftServer var0) {
      return ensureWorld(var0,var0.m_129783_());
   }
   public static StarterLedger ensureWorld(MinecraftServer var0,ServerLevel var1) {
      StarterLedger var2 = StarterLedger.get(var1);
      var2.bindWorld(var1.m_46472_().m_135782_().toString());
      if (var2.distributed()) {
         upgradeUntouched(var1, var2);
         SpawnStarter.reconcile(var0, var1, var2);
         return var2;
      } else {
         BlockPos var3 = var1.m_220360_();
         int var4 = var0.m_6982_() ? var0.m_6396_() : 0;
         int var5 = var4 >= 2147483645 ? Integer.MAX_VALUE : Math.max(2, var4 + 2);

         for (int var6 = 0; var6 <= 8; var6++) {
            for (int var7 = -var6; var7 <= var6; var7++) {
               for (int var8 = -var6; var8 <= var6; var8++) {
                  if (Math.max(Math.abs(var7), Math.abs(var8)) == var6) {
                     long var9 = (long)var3.m_123341_() + var5 + var7;
                     long var11 = (long)var3.m_123343_() + var8;
                     if (Math.abs(var9) <= 29999984L
                        && Math.abs(var11) <= 29999984L
                        && Math.max(Math.abs(var9 - var3.m_123341_()), Math.abs(var11 - var3.m_123343_())) > var4) {
                        BlockPos var13 = new BlockPos((int)var9, var3.m_123342_(), (int)var11);
                        if (var1.m_6857_().m_61937_(var13)) {
                           BlockPos var14 = var1.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, var13);
                           if (place(var1, var2, var14)) {
                              var2.observeSpawn(var3);
                              var1.m_8895_().m_78151_();
                              return var2;
                           }
                        }
                     }
                  }
               }
            }
         }

         return var2;
      }
   }

   public static boolean place(ServerLevel var0, StarterLedger var1, BlockPos var2) {
      if (var1.distributed() || var0.m_151570_(var2) || var0.m_151570_(var2.m_7494_()) || !var0.m_6857_().m_61937_(var2)) {
         return false;
      } else if (var0.m_8055_(var2).m_60795_() && var0.m_8055_(var2.m_7494_()).m_60795_() && var0.m_7702_(var2) == null) {
         BlockState var3 = var0.m_8055_(var2.m_7495_());
         if (var3.m_60783_(var0, var2.m_7495_(), Direction.UP) && var3.m_60819_().m_76178_() && !var3.m_204336_(BlockTags.f_13035_)) {
            for (Direction var5 : Plane.HORIZONTAL) {
               BlockState var6 = var0.m_8055_(var2.m_121945_(var5));
               if (var6.m_60713_(Blocks.f_50087_) || var6.m_60713_(Blocks.f_50325_)) {
                  return false;
               }
            }

            if (!var0.m_7731_(var2, (BlockState)Blocks.f_50087_.m_49966_().m_61124_(ChestBlock.f_51478_, Direction.WEST), 3)) {
               return false;
            } else if (var0.m_7702_(var2) instanceof ChestBlockEntity var7) {
               var7.m_58638_(Component.m_237113_("NG-City · Gemeinsame Startkiste"));
               var7.m_6836_(13, new ItemStack((ItemLike)NgCity.COMPLEX.get()));
               var7.m_6836_(22, Guide.create());
               var7.m_6596_();
               var1.markCommunal(var2);
               return true;
            } else {
               var0.m_7471_(var2, false);
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean upgradeUntouched(ServerLevel var0, StarterLedger var1) {
      BlockPos var2 = var1.location();
      if (var2 != null && var0.m_46805_(var2) && var0.m_7702_(var2) instanceof ChestBlockEntity var3) {
         ItemStack var8 = var3.m_8020_(10);
         ItemStack var5 = var3.m_8020_(13);
         ItemStack var6 = var3.m_8020_(16);
         ItemStack var7 = var3.m_8020_(22);
         if (!var8.m_150930_((Item)NgCity.BANK.get())
            || !var5.m_150930_((Item)NgCity.HOTEL.get())
            || !var6.m_150930_((Item)NgCity.BUYER.get())
            || var8.m_41613_() != 1
            || var5.m_41613_() != 1
            || var6.m_41613_() != 1
            || var8.m_41782_()
            || var5.m_41782_()
            || var6.m_41782_()) {
            return false;
         } else if (var7.m_150930_(Items.f_42615_)
            && var7.m_41782_()
            && var7.m_41783_().m_128461_("author").equals("NeverGate")
            && var7.m_41783_().m_128461_("title").equals("NG-City · Starthilfe")) {
            var3.m_6836_(10, ItemStack.f_41583_);
            var3.m_6836_(13, new ItemStack((ItemLike)NgCity.COMPLEX.get()));
            var3.m_6836_(16, ItemStack.f_41583_);
            var3.m_6836_(22, Guide.create());
            var3.m_6596_();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static String message(StarterLedger var0) {
      BlockPos var1 = var0.location();
      if (var1 != null) {
         return "Gemeinsame NG-City-Startkiste in " + var0.worldIdentity() + " beim Weltspawn: "
            + var1.m_123341_()
            + " / "
            + var1.m_123342_()
            + " / "
            + var1.m_123343_()
            + ". Sie wird nicht aufgefüllt.";
      } else {
         return var0.distributed()
            ? "Die NG-City-Startausstattung wurde in dieser Welt bereits vergeben. Nutzt die vorhandenen Gebäude gemeinsam."
            : "Beim Weltspawn ist noch kein sicherer freier Platz für die gemeinsame NG-City-Startkiste vorhanden.";
      }
   }

   private SharedStarter() {
   }
}
