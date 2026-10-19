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
            "Willkommen!\n\nBaue deine moderne NG-City auf: mit Bank, Hotel und Ankauf.\n\nDieses Buch führt dich durch deinen Start und erklärt die digitalen Menüs.",
            "Dein Start\n\n1. Alle drei Gebäude aufstellen.\n\n2. Zur Bank gehen und Konto eröffnen.\n\n3. Am Geldautomaten PIN anlegen.\n\n4. Im Hotel ein Zimmer mieten.",
            "Schritt 1: Gebäude\n\nGehe zur gemeinsamen Startkiste beim Weltspawn. Nimm Bank, Hotel, Ankauf und Handbuch heraus.\n\nStelle zuerst alle drei Gebäude auf. Suche dafür freie, feste Bauplätze.",
            "Gebäude setzen\n\nPaket in die Hand nehmen und oben auf festen Boden rechtsklicken.\n\nDie Vorschau zeigt Größe und Eingang. Der Eingang zeigt zu dir. Halte beim Setzen Abstand.",
            "Platzbedarf\n\nBank und Ankauf:\n11 × 10 × 6 Blöcke.\n\nHotel:\n19 × 20 × 11 Blöcke.\n\nMit Eingangsstufe. Der Boden muss tragen; darüber muss alles frei sein.",
            "Schritt 2: Bank\n\nGehe in die Bank. Rechtsklicke auf den Mitarbeiter. Wähle im digitalen Menü „Konto eröffnen“.\n\nDu erhältst dein Startguthaben, deine Geldbörse und Kreditkarte.",
            "Schritt 3: PIN\n\nRechtsklicke auf den Geldautomaten. Die Karte muss im Inventar sein.\n\nWähle vier Ziffern. „Bestätigen“ speichert die PIN auf deiner Karte.",
            "PIN bedienen\n\nZiffern anklicken oder auf der Tastatur eingeben. Rücktaste löscht; Eingabe bestätigt.\n\nAuch führende Nullen sind möglich. Merke dir deine vierstellige PIN!",
            "Schritt 4: Hotel\n\nGehe ins Hotel. Rechtsklicke auf das Personal oder Terminal.\n\nWähle ein freies Zimmer und 1–7 Minecraft-Tage. Unten links wählst du Karte oder Bargeld.",
            "Zimmer bezahlen\n\nKlicke „Jetzt bezahlen“. Bei Karte gib deine PIN ein und bestätige.\n\nNach der Zahlung erhältst du den Schlüssel. Nimm ihn in die Hand und rechtsklicke die Zimmertür.",
            "Digitale Menüs\n\nLinksklick auf ein beschriftetes Feld führt die Aktion aus. Fahre mit der Maus darüber für den ganzen Text.\n\nTab wählt Felder; Eingabe aktiviert. ESC schließt.",
            "Karte und PIN\n\nJeder neue Besuch am Geldautomaten und jede Kartenzahlung fragt deine PIN erneut ab.\n\nDas gilt auch im Hotel und bei Fundsachen. Bargeld braucht keine PIN.",
            "Geldautomat\n\nNach deiner PIN siehst du Kontostand und Auszahlung. Wähle einen Betrag oder Münzen und Scheine.\n\nMit „Loses Bargeld vollständig einzahlen“ zahlst du Geld aus dem Inventar ein.",
            "Geldbörse\n\nRechtsklick auf deine Geldbörse öffnet sie. Nur NG-Münzen und Scheine passen hinein.\n\nKarte und Geldbörse gehören dir. Ersatz gibt es beim Bankpersonal.",
            "Karte verloren?\n\nLass sie beim Bankpersonal ersetzen. Die alte Karte wird dadurch ungültig.\n\nLege für die Ersatzkarte am Geldautomaten eine neue PIN fest. Dein Konto bleibt erhalten.",
            "Waren verkaufen\n\nGehe zum Ankauf und rechtsklicke auf den Mitarbeiter.\n\nDas digitale Menü zeigt passende Vanilla-Waren aus deinem Inventar mit Menge und Preis. Wähle unten bar oder Konto.",
            "Verkauf bedienen\n\nKlicke die Warenkarte an. Die passende Gesamtmenge wird verkauft.\n\n„Weiter“ und „Zurück“ blättern. Bar und Konto teilen 2.000 Euro Limit je echter Stunde.",
            "Dein Hotelzimmer\n\nDie Zimmerkarten zeigen, was frei oder belegt ist. Die große Anzeige in der Lobby zeigt den Zimmerstatus.\n\nÜber die Innentreppe erreichst du die zwei großen Mietzimmer.",
            "Buchung läuft ab\n\nEs zählt die Minecraft-Weltzeit, auch übersprungene Nächte.\n\nNach Ablauf ist der Schlüssel ungültig. Du kommst in die Lobby. Deine Sachen werden eingelagert.",
            "Fundsachen\n\nHole deine Sachen beim Hotelpersonal oder Fundsachenlager gegen Tagespauschale ab.\n\nBei Karte ist erneut die PIN nötig. Sorge für Platz im Inventar.",
            "Fundsachenfrist\n\nDeine Sachen bleiben höchstens sieben Minecraft-Tage im Lager. Danach werden sie vernichtet.\n\nDu kannst sie auch vorher entsorgen. Diese Auswahl musst du ausdrücklich bestätigen.",
            "Schlüssel verloren?\n\nWähle beim Hotel dein eigenes gebuchtes Zimmer. Dort kannst du den verlorenen Schlüssel ersetzen.\n\nNach Buchungsende hilft kein Ersatzschlüssel mehr.",
            "Gebäude einpacken\n\nLeere Haupthand, Umschalt halten und auf das Außenschild rechtsklicken.\n\nRäume vorher alles aus. Umbauten, Inventare, andere Bewohner und aktive Hotelbuchungen verhindern das Einpacken.",
            "NG-Spielwährung\n\nMünzen: 10, 20 und 50 Cent sowie 1 und 2 Euro.\n\nScheine: 10, 20 und 50 Euro.\n\nEs gibt keine 1-, 2- oder 5-Cent-Münzen und keinen 5-Euro-Schein.",
            "Grundeinstellungen\n\nStart: 250 Euro.\nAnkauf: 2.000 Euro je echter Stunde.\nHotel: 5 Euro je Minecraft-Tag.\nLager: 1 Euro je angefangenem Minecraft-Tag.\n\nIn der Welt konfigurierbar.",
            "Neues Handbuch\n\n/ngcity anleitung\n\nDamit erhältst du die aktuelle Ausgabe. Alte Bücher ändern ihren Inhalt beim JAR-Wechsel nicht.\n\nViel Freude beim Stadtbau!",
            "Startkiste\n\nDie Kiste am Weltspawn ist für alle Spieler. Sie entsteht nur einmal und wird nie ersetzt oder aufgefüllt.\n\nKoordinaten stehen beim Eintritt im Chat.",
            "Über NG-City\n\ndev-0.0.2\nMinecraft 1.20.2\nForge 48.1.0\n\nNG-City gehört auf Server und Clients. NG-Verkehr und NG-Fahrzeuge sind für deinen Start nicht nötig."
,
            "Ausrüstung kaufen\n\nBeim Ankauf öffnest du „Ausrüstung kaufen“. Jedes Teil kostet beim ersten Kauf 5 Euro, einmal je Spieler.\n\nDanach gilt der reguläre Preis. Das Menü zeigt deinen Preis an.",
            "Das Startsortiment\n\nEisenhelm, Brustpanzer, Hose, Stiefel, Schwert, Spitzhacke, Axt, Schaufel, Hacke und Schild.\n\nAlle zehn Teile erstmals zusammen: 50 Euro.",
            "Ausrüstung bezahlen\n\nWähle unten Bargeld oder Karte. Klicke dann auf das gewünschte Teil. Bei Karte ist für jeden Kauf die PIN nötig.\n\nOhne Platz oder Geld wird der Erstkauf nicht verbraucht.",
            "Reguläre Preise\n\nHelm: 10 Euro\nBrustpanzer: 16 Euro\nHose: 14 Euro\nStiefel: 8 Euro\nSchwert: 8 Euro\nSpitzhacke: 12 Euro\nAxt: 12 Euro\nSchaufel: 6 Euro\nHacke: 8 Euro\nSchild: 8 Euro"
        };
        for (String text : texts) pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(text))));
        tag.put("pages", pages); return book;
    }
}
