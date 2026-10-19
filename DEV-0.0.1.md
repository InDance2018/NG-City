# NG-City · dev-0.0.1

Eigentümerauftrag: 19.10.2026. Ausgang: Planungsrepository ohne Modquellen. Ziel: Minecraft 1.20.2 / Forge 48.1.0 / Java 17. Die sichtbare Dateiversion heißt `dev-0.0.1`; Forge benötigt intern die numerisch beginnende Version `0.0.1-dev`.

## Implementierter Grundumfang

- Startkisten-Item beim ersten Eintritt; weltweit gespeicherte Vergabe je Spieler-UUID. Die Datei `data/ng_city_starters.dat` gehört zur normalen Weltsicherung. Tod/Dimensionswechsel erzeugen keine neue Kiste. Bei vollem Inventar wird beim nächsten Login erneut versucht.
- Platzierte Startkiste mit Bank-, Hotel- und Ankaufpaket sowie einer schriftlichen Anleitung mit 14 kurzen Seiten.
- Begehbare Fertiggebäude aus normalen Minecraft-Blöcken, Außenbeschriftung und Innenausstattung. Bank und Ankauf benötigen inklusive Eingangsstufe 11 × 10 × 6 Blöcke; Hotel 13 × 14 × 11.
- Hotel mit Rezeption, Innentreppe und zwei Zimmern mit Betten. Bank mit Schalter, Sitzbereich und Tresor-/Lagerbereich; Ankauf mit Annahmetheke/Lager.
- Je ein benannter, stationärer Villager als Mitarbeiter. Rolle, Eigentümer und Arbeitsplatz werden im nativen Entity-NBT gespeichert. Mitarbeiter öffnen per Rechtsklick eine schreibgeschützte Übersicht; deren Symbole können nicht entnommen werden. Administratorisches Entfernen per `/kill` bleibt möglich.
- Vier Gebäuderichtungen. Prüfung des Bauvolumens auf geladene Chunks, Weltgrenze, Höhe, feste Unterstützung, Baurechte, bestehende Blöcke, Blockentities, Flüssigkeiten und Lebewesen. Forge-MultiPlace-Ereignis mit Wiederherstellung bei Abbruch. Itemverbrauch erst nach erfolgreicher Platzierung und Mitarbeitererzeugung.
- Eigenes Kreativmenü; `/ngcity anleitung`, `/ngcity startkiste` (Admin, Stufe 2).

## Bewusste Funktionsgrenze

Konten/Währung, Startguthaben, Warenliste/Preise, tatsächlicher Warenankauf, Zimmervermietung und gemeinsame Zahlungsadapter wurden noch nicht festgelegt und sind nicht aktiviert. Die Mitarbeiterübersichten benennen das. Kein Dummy-Geld, keine scheinbare Zahlung, keine Belastung oder Warenabgabe. NG-Verkehr und NG-Fahrzeuge bleiben unverändert und werden nicht benötigt.

Gebäude lassen sich nach dem Platzieren normal umbauen; automatisches Einpacken/Abreißen ist nicht vorhanden. Ein nachträglich abgebauter Mitarbeiter-Arbeitsplatz wird nicht automatisch als Gebäude neu erzeugt.

## Prüfung

**Build erfolgreich; alle 21 erforderlichen Forge-GameTests bestanden.** Der abschließende Nachweis steht unter [dev-0.0.1.json](docs/test-results/dev-0.0.1.json), die bereinigten Protokolle unter [Spieltests](docs/test-results/dev-0.0.1-gametest.txt) und [Produktionsbuild](docs/test-results/dev-0.0.1-build.txt). Produktions-JAR: 31.068 Bytes; SHA-256 `0f869f8f8066c7893bf1f0cfd218cf5f0db4015c4014a220e44e8dd51d37953e`. Testklassen und Testvorlage sind ausgeschlossen. Alle lokalen Quell- und Builddatei-Hashes stimmen mit dem geprüften Build überein. QA-Quellen und Strukturvorlage liegen in `src/qa`; ohne `-PcityQA` sind sie nicht Teil der Mod.

Reproduzieren:

```sh
./gradlew -PcityQA runGameTestServer
./gradlew clean build
```

Offen bleiben echter grafischer Forge-Clienttest, tatsächliches Betreten und Bedienung durch mehrere Spieler, Arclight-/Schutzplugin-Kompatibilität, vollständiger Serverneustart mit Clientbeitritt und spätere Wirtschafts-/Gegenmod-Integration. Ein NBT- oder Festplatten-Ladetest ersetzt keine vollständige Server-/Clientabnahme.

## Bereitstellung

Quellcode im Repository und Entwicklungs-JAR für den Eigentümer. Keine MC02-Installation und keine öffentliche Release-Veröffentlichung durch diesen Auftrag. Die Web-/PDF-Kataloge vom 18.10.2026 bleiben historischer Planungsstand.
