package de.nevergate.city;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public final class Guide {
    private Guide() { }
    public static ItemStack create() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = book.getOrCreateTag();
        tag.putString("title", "NG-City · Starthilfe"); tag.putString("author", "NeverGate"); tag.putBoolean("resolved", true);
        ListTag pages = new ListTag();
        String[] texts = {
            "NG-City\nStarthilfe\n\ndev-0.0.1\nMinecraft 1.20.2\nForge 48.1.0\n\nBank, Hotel und Ankauf: fertig gebaut, begehbar und mit Mitarbeitern.",
            "1. Startkiste\n\nSetze deine Startkiste per Rechtsklick auf einen freien Boden. Öffne sie und nimm die drei Gebäudepakete sowie dieses Buch heraus.",
            "2. Gebäude setzen\n\nNimm ein Gebäudepaket in die Hand. Klicke auf die Oberseite eines festen Bodens. Halte dabei einige Blöcke Abstand vom Bauplatz.",
            "3. Ausrichtung\n\nDer Eingang zeigt zu dir. Hinter dem angeklickten Feld entsteht das Gebäude. Mit deiner Blickrichtung bestimmst du seine Ausrichtung.",
            "4. Platzbedarf\n\nBank und Ankauf:\n11 breit, 10 tief, 6 hoch.\n\nHotel:\n13 breit, 14 tief, 11 hoch.\n\nDie Eingangsstufe ist mitgerechnet.",
            "5. Bauplatz\n\nDer ganze Boden muss getragen sein. Der Platz darüber muss frei und geladen sein. Bauten, Wasser und Lebewesen verhindern das Setzen.",
            "6. Mitarbeiter\n\nRechtsklick auf einen Mitarbeiter öffnet die Gebäudeübersicht. Die Mitarbeiter bleiben an ihrem Arbeitsplatz und sind geschützt.",
            "7. Bank\n\nDie Bank hat einen Schalter, einen Sitzbereich und einen Tresorbereich. Geld, Konten und Überweisungen sind noch nicht aktiviert.",
            "8. Hotel\n\nIm Erdgeschoss ist die Rezeption. Über die Innentreppe erreichst du zwei Zimmer mit Betten. Eine kostenpflichtige Vermietung folgt später.",
            "9. Ankauf\n\nDer Ankauf hat eine Annahmetheke und ein Lager. Warenliste, Preise und Warenannahme sind noch nicht aktiviert. Es wird nichts abgebucht.",
            "10. Umbauen\n\nDie gesetzten Gebäude bestehen aus normalen Blöcken. Du kannst sie umbauen. Automatisches Einpacken oder Löschen ist nicht vorgesehen.",
            "11. Neue Anleitung\n\n/ngcity anleitung\n\ngibt dir ein neues Buch.\n\n/ngcity startkiste\n\ngibt Administratoren eine weitere Kiste.",
            "12. Startpaket\n\nJeder Spieler bekommt beim ersten Eintritt eine Kiste. Bei vollem Inventar erfolgt ein neuer Versuch beim nächsten Eintritt. Tod erzeugt keine neue Kiste.",
            "13. Installation\n\nNG-City gehört auf Forge-Server und Clients. Verkehr und Fahrzeuge sind keine Voraussetzung. Wirtschaftsanbindung folgt.\n\nViel Freude beim Stadtbau!"
        };
        for (String text : texts) pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(text))));
        tag.put("pages", pages); return book;
    }
}
