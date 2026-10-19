# NG-City dev-0.0.2

Aktuelle Korrekturfassung: [FIX-DEV-0.0.2.md](FIX-DEV-0.0.2.md). Jetzt 2.000 Euro je Echtstunde und Karten-PIN. Die folgenden Angaben dokumentieren den historischen Erstbuild; aktuelle Prüfsumme in docs/test-results/dev-0.0.2.json.

Auftrag: ausdrückliche Freigabe vom 19.10.2026, 16:20 CEST. Minecraft 1.20.2 / Forge 48.1.0 / Java 17. Ausgang: ausgelieferte dev-0.0.1 und lokale Entwürfe für Vorschau/Einpacken. Ausschließlich lokaler Einzelspieler-Test vorgesehen, keine MC02-Installation.

## Funktionsumfang

- Maßhaltige Bauvorschau mit Eingang, Abmessungen und Prüfung des sichtbaren Bauplatzes. Endgültige Prüfung erfolgt beim Setzen.
- Ganze unveränderte Gebäude mit leerer Haupthand und Umschalt-Rechtsklick am Außenschild einpacken. Eigentümer/Baurechte, Inventare, Lebewesen, lose Gegenstände und Änderungen werden geprüft. Aktive Hotelbuchungen und Fundsachen verhindern das Einpacken.
- Kontoeröffnung beim Bankpersonal; einmaliges Startguthaben. Kreditkarte und Geldbörse sind an die Spieler-UUID gebunden. Ersatzkarten sperren die alte Kartengeneration. Ersatzgeldbörsen verweisen auf denselben gespeicherten Inhalt.
- Persönliche Geldbörse mit 54 Geldfächern; nur NG-Münzen und Scheine werden angenommen. Inhalt wird unabhängig vom Gegenstandsverlust bei den Weltdaten gespeichert.
- Geldautomat mit Auszahlung einzelner Stückelungen und 10/20/50/100 Euro; Einzahlung von losem Bargeld. Gültige eigene Karte erforderlich.
- Münzen 10/20/50 Cent, 1/2 Euro; Scheine 10/20/50 Euro. Getrennte Vorder-/Rückseiten aus einer generierten detaillierten Texturatlasdatei; Münzen mit Metallprägung, Scheine mit europäischen Farb-/Architekturmotiven und NG-Kennzeichnung. Keine 1/2/5-Cent-Münzen und kein 5-Euro-Schein.
- Vanilla-Ankauf zeigt die akzeptierten unveränderten Waren im Inventar mit Mindestmenge und Preis. Verwaltungsblöcke, Moditems und Items mit individuellen NBT-Daten sind ausgeschlossen, damit insbesondere gefüllte Behälter nicht verschwinden. Bar/Konto teilen ein rollierendes 24-Echtstunden-Limit je UUID. Kein Warenabzug bei fehlender Mindestmenge oder fehlendem Platz für die Auszahlung.
- Preise verwenden exakte ganzzahlige Materialwerte. Crafting-/Steinsäge-Rezepte begrenzen den Wert der Ausgabe auf die billigste bekannte Eingabe. Gebündelte Mengen verhindern künstliche 10-Cent-Aufrundungen.
- Hotel: zwei getrennte Zimmer; gemeinsamer zugänglicher Flur ab Treppenabsatz. Buchung 1–7 Minecraft-Tage, komplette Vorauszahlung bar oder über Kartenterminal. Schlüssel erst nach Zahlung; UUID, Hotel, Zimmer und Buchungsschlüssel müssen übereinstimmen. Redstone öffnet die Zimmertür nicht. Automatisches Schließen nach zwei Sekunden.
- Großes dreiblockbreites Zimmerdisplay: frei grün, belegt mit Spielername und restlichen Minecraft-Tagen. Physisches Zahlungsterminal und Fundsachenlager im Erdgeschoss.
- Welt-Tageszeit zählt einschließlich übersprungener Nächte. Einzelspieler-Pausen und geschlossene Welten lassen die Zeit nicht weiterlaufen. Nach Ablauf werden Gäste aus dem Zimmer zur Lobby versetzt, der Schlüssel ungültig und zurückgelassene Inventarinhalte, lose Gegenstände und hinzugefügte Blöcke eingelagert. Standardmöbel bleiben erhalten.
- Fundsachen pro Spieler/Buchung, maximal sieben Minecraft-Tage ab Buchungsende. Abholung gegen Tagespauschale, ausdrücklich bestätigte Entsorgung oder automatische Vernichtung nach Frist. Abholung prüft Guthaben und Platz einschließlich Wechselgeld, bevor sie belastet oder Gegenstände freigibt.
- Unveränderte dev-0.0.1-Hotels werden beim geladenen Mitarbeiter auf die geschlossenen Zimmer und Geräte aktualisiert. Änderungen an betroffenen Zellen blockieren den Umbau; vorhandene Gegenstände werden nicht überschrieben.

## Konfigurierbare Testvorgaben

Die bisher vorgeschlagenen Werte sind als änderbare Testvorgaben implementiert, keine unveränderliche Preisentscheidung:

| Einstellung | Vorgabe |
|---|---:|
| Einmaliges Startguthaben | 250 Euro |
| Ankauf pro rollierenden 24 Echtstunden | 500 Euro |
| Hotel pro Minecraft-Tag | 5 Euro |
| Aufbewahrung pro angefangenem Minecraft-Tag | 1 Euro |

Datei: `<Welt>/serverconfig/ng_city-server.toml`. Werte sind in 10-Cent-Einheiten angegeben. Geldbeträge werden ohne Fließkomma gespeichert. Die Weltdatei `data/ng_city_economy.dat` enthält Konten, Kartenkennung, Geldbörsen, Hotelbuchungen und Fundsachen; zusammen mit den Welt-/Spielerdaten sichern.

## Prüfstatus

Kompilierung gegen die exakten Forge-48.1.0-Abhängigkeiten erfolgreich. **50 eindeutige erforderliche Forge-GameTest-Fälle bestanden:** 41 Fälle für Grundgebäude, Bank, Zahlungen, Ankauf, Hotel, Schlüssel, Fundsachen und Festplatten-Persistenz im Gesamtlauf; anschließend alle neun Vorschau-/Einpackfälle in einer isolierten Suite. Die 41 Kernfälle hatten keinen Fehler; ein zunächst nicht sichtbarer Ankauf-Mitarbeiter betraf die Test-Chunkaktivierung. Die QA-Flächen werden jetzt ausdrücklich geladen gehalten und Mitarbeiter erst nach Registrierung geprüft. Das ist keine Änderung an der Mitarbeiter- oder Zahlungslogik der Mod.

**2.104 zusätzliche Preisbündel-Prüfungen bestanden**, einschließlich Diamantblock, Nuggets, kleiner Waren und nicht stapelbarer Betten. Große Preisbrüche werden konservativ abgerundet; es gibt keine Aufrundung auf künstlichen Mindestgewinn. Produktions-JAR und Bytecode-/Prüfsummennachweis werden unter `docs/test-results/dev-0.0.2.json` dokumentiert. Die Produktions-JAR enthält keine Testklassen und keine Teststruktur.

Der grafische Clienttest und die reale Einzelspieler-Abnahme durch den Eigentümer bleiben gesondert. Keine NG-Verkehr-/NG-Fahrzeuge-API und keine gemeinsame Gegenmod-Abnahme durch diesen Auftrag. Keine MC02-Installation oder öffentliche Release-Veröffentlichung.

## Buildumgebung

Standard: `./gradlew -PcityQA runGameTestServer`, danach `./gradlew clean build`.

Die lokale Arbeitsumgebung besitzt keinen direkten Zugriff auf die Gradle-Distribution. Für diesen Auftrag wurden ausschließlich bereits vorhandene öffentliche Forge-/Minecraft-/Bibliotheksabhängigkeiten aus der bestehenden Buildumgebung gelesen, gehasht und lokal verwendet. Projektquellen wurden nicht zum externen Buildserver übertragen. `tools/local-build.py` kompiliert offline und verwendet die vorhandene Forge-Reobf-Mappingdatei sowie ForgeAutoRenamingTool. `tools/local-gametest.py` verwendet die exakte ursprüngliche Forge-Userdev-Startkonfiguration mit lokalen Abhängigkeiten. Fremde Abhängigkeiten werden nicht in die Mod-JAR gepackt.

Die automatische Freigabeprüfung hat die versuchte externe Quellarchivübertragung und den GitHub-Statuskommentar abgewiesen. Die lokale Weiterarbeit ist davon unabhängig; eine GitHub-Fortschreibung wird nicht als erfolgt gemeldet.


## Fertige Entwicklungs-JAR

`ng_city-1.20.2-dev-0.0.2.jar` · 1,179,129 Bytes. SHA-256: `e05fe8560d0420831422087df2226f96963e09ee993e785d4ebda8828f935b1f`.

40 Produktionsklassen sind vor der Forge-Umbenennung bytegleich mit den geprüften Klassen. Java-Klassenversion 61 (Java 17), Archiv-CRC, Modversion, Ausschluss sämtlicher Testklassen/-strukturen und 95 Modell-/Texturverweise geprüft. Alle 16 Währungsvorder-/rückseiten sind enthalten. Die ausgelieferte Datei wurde erfolgreich bereitgestellt.

Die Prüfnachweise stehen unter `docs/test-results/dev-0.0.2.json` und den dortigen Kern-, Einpack-, Preisbündel- und Buildprotokollen. Die 50 Fälle wurden in zwei Suiten vollständig abgedeckt (41 Kernfälle plus 9 Einpack-/Vorschaufälle); kein einzelner gemeinsamer 50/50-Lauf wird behauptet.
