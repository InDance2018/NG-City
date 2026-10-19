package de.nevergate.city.economy;
import net.minecraftforge.common.ForgeConfigSpec;
public final class CityConfig {
 public static final ForgeConfigSpec SPEC;
 public static final ForgeConfigSpec.IntValue START, LIMIT, HOTEL, STORAGE;
 static { var b=new ForgeConfigSpec.Builder();
 START=b.comment("Einmaliges Startguthaben bei Kontoeröffnung, in 10-Cent-Einheiten.").defineInRange("startUnits",2500,0,1000000);
 LIMIT=b.comment("Gemeinsames Ankaufslimit pro Spieler und rollierender Echtstunde, in 10-Cent-Einheiten.").defineInRange("hourlyPurchaseUnits",20000,1,10000000);
 HOTEL=b.comment("Zimmerpreis pro Minecraft-Tag, in 10-Cent-Einheiten.").defineInRange("hotelDayUnits",50,1,1000000);
 STORAGE=b.comment("Aufbewahrung pro angefangenen Minecraft-Tag, in 10-Cent-Einheiten.").defineInRange("storageDayUnits",10,0,1000000); SPEC=b.build(); }
 public static String money(long units){return String.format(java.util.Locale.GERMANY,"%,.2f € NG",units/10.0);}
}
