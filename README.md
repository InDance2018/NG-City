# NG-City

Stadt- und Wirtschaftsmod für **Minecraft 1.20.2 / Forge 48.1.0 / Java 17**. Aktuelle Entwicklung: **dev-0.0.2** (intern `0.0.2-dev`).

## Enthalten

- Startkiste und Anleitung; begehbare Bank, Hotel und Ankauf mit Mitarbeitern.
- Maßhaltige Bauvorschau und vollständiges Einpacken unveränderter Gebäude per leerer Haupthand, Umschalt + Rechtsklick aufs Außenschild.
- Bankkonto, persönliche Geldbörse und Kreditkarte, Geldautomat, NG-Münzen und Scheine mit getrennten Vorder-/Rückseiten.
- Vanilla-Ankauf mit materialbezogenen Preisen, gemeinsamen Auszahlungslimit und Wahl zwischen Bargeld und Kontogutschrift.
- Hotel mit zwei abschließbaren Zimmern, Zahlungsterminal, großer Zimmerübersicht, maximal sieben Minecraft-Tagen Buchungsdauer und persönlichem Schlüssel.
- Fundsachenlager: höchstens sieben Minecraft-Tage, kostenpflichtige Abholung oder bestätigte Entsorgung.

## Einstieg

1. Die alte NG-City-JAR durch dev-0.0.2 ersetzen, keine zwei Versionen gleichzeitig laden.
2. Eine Welt mit Forge 48.1.0 öffnen; am Weltspawn steht eine gemeinsame Startkiste mit den drei Gebäudepaketen und dem Buch. Die Koordinaten stehen beim Eintritt im Chat. Bestehende unveränderte dev-0.0.1-Gebäude können weiterverwendet werden.
3. Beim Bankpersonal ein Konto eröffnen. Geldbörse und Karte werden an deine Spieler-UUID gebunden.
4. Am Ankauf Waren aus dem Inventar verkaufen, am Geldautomaten Geld abheben und im Hotel bar oder mit Karte buchen. Den Schlüssel zum Öffnen in die Hand nehmen.

Testwerte: 250 Euro einmaliges Startguthaben; 2.000 Euro Ankaufserlös pro rollierender Echtstunde; Hotel 5 Euro pro Minecraft-Tag; Aufbewahrung 1 Euro pro angefangenem Minecraft-Tag. Konfigurierbar in `<Welt>/serverconfig/ng_city-server.toml`, jeweils in Zehn-Cent-Einheiten. Alle Preise sind NG-Spielwährung.

Die Hotelzeit folgt der Minecraft-Tageszeit einschließlich übersprungener Nächte. Nach Ablauf wird der Schlüssel ungültig. Beim Hotelpersonal oder Fundsachenlager kannst du zurückgelassene Gegenstände abholen; nach sieben Minecraft-Tagen werden sie vernichtet.

## Nachweise und Grenzen

[DEV-0.0.2.md](DEV-0.0.2.md) beschreibt den Umfang, die Testvorgaben und die Prüfnachweise. 79 eindeutige Forge-GameTest-Fälle über Gesamt- und gezielte Suiten abgedeckt; genaue Laufgrenzen in docs/test-results/dev-0.0.2-shared-starter.json. Zuvor 2.104 Preisbündel-Prüfungen bestanden. Einzelspieler-Abnahme durch den Eigentümer am 19.10.2026 bestätigt: alle bisherigen NG-City-Funktionen getestet; gemeinsame Startkiste am Weltspawn gesetzt, alle Inhalte vorhanden und alle drei Gebäude baubar. Der Mehrspieler-Test folgt nach Abschluss des derzeitigen Minecraft-Interface-Umbaus. Keine MC02-Installation. Entwicklungsfassung, kein stabiler Release.

NG-Verkehr und NG-Fahrzeuge bleiben eigenständige Mods. Eine gemeinsame Zahlungsanbindung ist durch dev-0.0.2 nicht implementiert. [Planung](PLANUNG.md), [historischer Grundaufbau dev-0.0.1](DEV-0.0.1.md).

## Bauen

```sh
./gradlew -PcityQA runGameTestServer
./gradlew clean build
```

`priceBundleTest` gehört zu `check`. QA-Quellen und die leere Teststruktur werden nicht in der Produktions-JAR ausgeliefert. Die Offline-Hilfen unter `tools/` wurden für die vorhandene eingeschränkte lokale Buildumgebung eingesetzt; sie ersetzen keine Entwicklungsabhängigkeiten im Repository.

## Vergrößerte Hotelzimmer

Siehe [DEV-0.0.2-HOTEL1.md](DEV-0.0.2-HOTEL1.md). Neue Mietzimmer mit 60 bzw. 70 Blöcken Innenfläche. Alte Hotels können leer eingepackt und mit dem größeren Paket neu gesetzt werden. [Webkatalog und Bedienung](https://nevergate.de/ng-city/).

## Aktuelle dev-0.0.2-Korrekturen

[FIX-DEV-0.0.2.md](FIX-DEV-0.0.2.md): Einpacken auf der Eingangsstufe, Mitarbeiter-Ausrichtung, nach links versetzte Sitzbänke, 2.000 Euro Ankauf je Echtstunde und vierstellige PIN bei jeder Kartenzahlung. Die PIN erstmals am Geldautomaten festlegen. Dateiname bleibt ng_city-1.20.2-dev-0.0.2.jar.

## Digitale Oberflächen

[DIGITAL-DEV-0.0.2.md](DIGITAL-DEV-0.0.2.md): digitale Bedienfelder für Bank, Geldautomat, Verkauf und Hotel, PIN-Tastatureingabe sowie neue Geldautomaten- und Terminalmodelle. Bestehende Geräte verwenden die neue Darstellung nach dem JAR-Wechsel.

## Ausrüstung im Ankauf

[EQUIPMENT-DEV-0.0.2.md](EQUIPMENT-DEV-0.0.2.md): zehn Rüstungs-/Werkzeugangebote mit persönlichem Erstkaufpreis von je 5 Euro, danach freigegebenem regulärem Preis. Bar oder Karte mit PIN, Erstkäufe werden gespeichert.

## Gemeinsame Startkiste

[SHARED-STARTER-DEV-0.0.2.md](SHARED-STARTER-DEV-0.0.2.md): einmalige gemeinsame Kiste am Weltspawn statt persönlicher Starter. Keine erneute Vergabe bei neuen Spielern, leerer/zerstörter Kiste oder Neustart. Bereits nach altem Verfahren versorgte Welten erhalten kein zusätzliches Paket.

## Download und aktuelle Abnahme

[Entwicklungs-JAR dev-0.0.2](https://nevergate.de/ng-city/downloads/ng_city-1.20.2-dev-0.0.2.jar) · [Alle Funktionen und Ausbesserungen](CHANGELOG-dev-0.0.2.md) · [Einzelspieler-Abnahme](ABNAHME-2026-10-19.md)

[Webkatalog DE/EN](https://nevergate.de/ng-city/) · [PDF Deutsch](https://nevergate.de/ng-city/downloads/NG-City-Katalog-DE.pdf) · [PDF English](https://nevergate.de/ng-city/downloads/NG-City-Katalog-EN.pdf)
