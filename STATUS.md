# NG-City · aktueller Projektstand

Stand: **31.10.2026**. Letzter belegter ausgelieferter Stand: **dev-0.2.10 vom 21.10.2026**. Minecraft 1.20.2 · Forge 48.1.0 · Java 17. Entwicklungsfassung, kein stabiler GitHub-Release.

## Implementiert und dokumentiert

Begehbarer Verwaltungskomplex mit vier Flügeln, 13 Mitarbeitern und Digitalzentrale; Bank, Konto/PIN, NG-Bargeld/Geldbörse, Handel/Ausrüstung, Hotelbuchungen, Zimmer-/Truhenschlüssel und gemeinsame Spawnkiste. Konkreter Umfang und Platzierungskorrekturen: [DEV-0.2.10.md](DEV-0.2.10.md).

Das aktuelle archivierte Artefakt ist [ng_city-1.20.2-dev-0.2.10.jar](dist/dev-0.2.10/ng_city-1.20.2-dev-0.2.10.jar), 1.381.931 Bytes, SHA-256 c4522ac7916bd5b9934ef6510279e4c97365ce4146de8bf4b44d1f4cb757061f.

## Abnahme und Testgrenzen

- [Eigentümerabnahme vom 21.10.](ABNAHME-2026-10-21.md): bisheriger Umbau im Einzel- und Mehrspieler bestätigt; konkret Konto/PIN, Hotel/Schlüssel/Zimmer/Truhe, Ausrüstung und Ankauf.
- Isolierte Arclight-Tests für Platzierung, Mitarbeiter und gezielte Tier-/Monsterentfernung nach erfolgreichem Bau sind im [Prüfbericht](docs/test-results/dev-0.2.10.json) dokumentiert.
- Bekannter ChunkStorage-Wartezustand beim isolierten Shutdown: kein neuer vollständiger Speicher-/Neustartnachweis aus diesem Lauf. Kein separat protokollierter Zwei-Spieler-Parallellauf aus der allgemeinen Eigentümerbestätigung ableiten.
- Historische Installation/Onlineprüfung ist datiert und keine aktuelle Installationsanweisung.

## Quellen und Reproduzierbarkeit

Der reguläre Gradle-Hauptquellbaum bildet weiterhin den historischen dev-0.0.2-Stand ab. Die [späteren verfügbaren Patchquellen](development/dev-0.2.10/README.md) und die ausgelieferte JAR sind getrennt archiviert. Ein regulärer Gradle-Build des Hauptquellbaums erzeugt daher nicht automatisch dev-0.2.10. Vollständige Quellkonsolidierung bleibt offen.

## Nächste Aufgaben

[Planung vom 21.10.](PLANUNG-2026-10-21.md): Bauamt mit Messgerät, Stromnetz, gemeinsame optionale Mod-Wirtschaft und Straßenmodule. Straßenmodule 24/40 Blöcke breit, Geraden 20 Blöcke; Grundausstattung nach Überblick über die Zusatzpakete festlegen. Diese Funktionen sind noch keine fertig implementierte neue City-Version.

NG-Verkehr und NG-Fahrzeuge bleiben eigenständig. Gemeinsame Konten/Zahlungen brauchen einen tatsächlichen Adapter mit Einheitenumrechnung, Autorisierung, dauerhafter Deduplizierung und Fehler-/Erstattungsfällen. Aktueller gemeinsamer Wirtschaftstest fehlt. Historische Detailberichte bleiben erhalten.

## Herkunft und Prüfgrenzen

Abgleich vom 31.10.2026 mit den vorhandenen Repositorydateien, Issues, Branches und dokumentierten Projektberichten. Tests werden mit ihrem ursprünglichen Datum weitergeführt; keine neuen Geräte-, Zahlungs- oder Spieltests durch diese Dokumentationspflege. Keine neue Programm-/Release-Veröffentlichung.
