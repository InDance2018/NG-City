# NG-City

Minecraft **1.20.2**, **Forge 48.1.0**, **Java 17** · **dev-0.0.1**

Die erste NG-City-Entwicklungsversion liefert drei kompakte, fertig platzierbare und begehbare Gebäude mit Inneneinrichtung und interaktiven Mitarbeitern: **Bank**, **Hotel** und **Ankauf**.

## Einstieg

Beim ersten Eintritt erhält jeder Spieler eine Startkiste als Item. Auf einen freien Boden setzen und öffnen: Darin liegen die drei Gebäudepakete und eine Bedienungsanleitung als Buch. Bei vollem Inventar wird die Vergabe beim nächsten Eintritt erneut versucht.

Gebäudepaket in die Hand nehmen und auf die Oberseite eines festen Bodens klicken. Der Eingang zeigt zum Spieler; das Gebäude entsteht dahinter. Einige Blöcke Abstand halten. Bank und Ankauf benötigen 11 × 10 × 6 freie Blöcke inklusive Eingangsstufe, das Hotel 13 × 14 × 11. Alle Grundflächen müssen getragen sein. Platzierung funktioniert in vier Richtungen und prüft geladene Chunks, Bauhöhe, Weltgrenze, Baurechte, vorhandene Blöcke, Flüssigkeiten und Lebewesen. Bestehende Bauten werden nicht überschrieben. Forge-Platzierungsereignisse können den Bau abbrechen; die erfassten Blöcke werden dann wiederhergestellt.

Die Gebäude bestehen nach dem Setzen aus normalen Minecraft-Blöcken. Ein automatisches Einpacken oder Entfernen gibt es nicht. Rechtsklick auf einen Mitarbeiter öffnet eine schreibgeschützte Gebäudeübersicht. Die Mitarbeiter bleiben an ihrem Arbeitsplatz.

- **Bank:** Schalter, Sitzbereich und Tresor-/Lagerbereich.
- **Hotel:** Rezeption, Innentreppe und zwei Zimmer mit Betten im Obergeschoss.
- **Ankauf:** Annahmetheke und Lager.
- `/ngcity anleitung`: neues Handbuch.
- `/ngcity startkiste`: weitere Kiste für Administratoren (Berechtigungsstufe 2).
- Kreativmenü: eigener Tab **NG-City**.

## Installation und Entwicklung

Die Mod gehört auf Forge-Server **und** Clients. NG-Verkehr und NG-Fahrzeuge sind für diese Version nicht erforderlich. Nicht in einem laufenden Server austauschen. Diese erste Entwicklungsversion zunächst in einer gesicherten Testwelt prüfen.

Build: `./gradlew clean build` mit Java 17. Dateiversion: `dev-0.0.1`; Forge-interne Versionsnummer: `0.0.1-dev` (Forge verlangt eine führende Zahl).
Server-Spieltests: `./gradlew -PcityQA runGameTestServer`.
Produktions-JAR anschließend erneut ohne `cityQA` bauen: `./gradlew clean build`.
Testquellen und Teststrukturen befinden sich ausschließlich in `src/qa` und werden ohne `cityQA` nicht gepackt.

[Entwicklungs- und Prüfnachweis](DEV-0.0.1.md) · [Planung](PLANUNG.md) · [Ankündigung](https://github.com/InDance2018/NG-City/issues/1)

## Wirtschaft und weitere Mods

Konten, Geld, Ankaufspreise, Warenannahme und Zimmervermietung sind in dev-0.0.1 noch nicht aktiviert. Die Mitarbeiter zeigen den Entwicklungsstand; es wird keine Bezahlung oder erledigte Dienstleistung vorgetäuscht.

NG-City bildet künftig die gemeinsame Stadt- und Wirtschaftsgrundlage. NG-Verkehr und NG-Fahrzeuge bleiben getrennte, eigenständig nutzbare Mods. Ihre spätere Wirtschaftsanbindung ist noch nicht implementiert oder gemeinsam geprüft. Weitere Projekte können später angebunden werden. [Grundlagenplanung](PLANUNG.md)

## Webkatalog und PDF

Die Kataloge vom 18.10.2026 beschreiben die damalige Planung und wurden durch diesen Modbau nicht neu veröffentlicht.

- [Zweisprachiger Webkatalog](https://nevergate.de/ng-city/)
- [PDF-Katalog Deutsch](https://nevergate.de/ng-city/downloads/NG-City-Katalog-DE.pdf)
- [PDF catalogue English](https://nevergate.de/ng-city/downloads/NG-City-Katalog-EN.pdf)
- [Katalogquellen und PDF-Kopien](docs/catalog/)
