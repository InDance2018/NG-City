# NG-City dev-0.0.2 · Funktionen und Ausbesserungen

Stand: 19.10.2026. Minecraft 1.20.2 / Forge 48.1.0 / Java 17. Entwicklungsdateiname bleibt `ng_city-1.20.2-dev-0.0.2.jar`.

- Begehbare Bank, Hotel und Ankauf mit Einrichtungen, Mitarbeitern, maßhaltiger Bauvorschau, vier Richtungen und geprüfter Platzierung.
- Einmalige gemeinsame Startkiste am Weltspawn mit drei Gebäudepaketen und 32-seitigem Starthandbuch. Keine persönlichen Starter oder Nachfüllung. Vergabe bleibt gespeichert. Koordinaten im Chat; auf dedizierten Servern außerhalb des Vanilla-Spawnschutzes. Bereits versorgte alte Welten erhalten kein zweites Paket.
- Persönliches Konto (250 Euro Start), UUID-gebundene Kreditkarte und Geldbörse mit 54 Fächern. Ersatzkarten sperren die alte Generation; Ersatzgeldbörsen behalten den Inhalt.
- Originalnah gestaltete NG-Münzen (10/20/50 Cent, 1/2 Euro) und Scheine (10/20/50 Euro), getrennte Vorder-/Rückseiten. Keine kleinen Centmünzen oder 5-Euro-Scheine.
- Geldautomat: Abheben und Einzahlen; vierstellige Karten-PIN bei Erstbesuch setzen, auf der Karte speichern, bei jedem neuen Automatenbesuch und jeder Kartenzahlung erneut abfragen. Führende Nullen und Tastatureingabe möglich.
- Vanilla-Warenankauf mit Material-/Rezeptpreisen, Bündelmengen, maximal passender Menge je Klick und Auszahlung bar oder aufs Konto. Gemeinsames Limit von früher 24 Echtstunden auf **2.000 Euro je rollierender Echtstunde** geändert. Individuell veränderte Items/gefüllte Behälter ausgeschlossen.
- Ausrüstung kaufen: Eisenhelm, Brustpanzer, Hose, Stiefel, Schwert, Spitzhacke, Axt, Schaufel, Hacke und Schild. Je Spieler und Teil einmal 5 Euro; zusammen 50 Euro. Danach 10/16/14/8/8/12/12/6/8/8 Euro. Erstkäufe gespeichert, bar oder Karte mit frischer PIN. Kein Geld-/Erstkaufverlust bei fehlender Zahlung oder fehlendem Platz.
- Hotel vergrößert auf 19 × 19 × 11, inklusive Eingang 20 tief. Zimmerinnenflächen 6 × 10 und 7 × 10 (60/70 Blöcke). Betten versetzt, freie Laufwege hinter den Türen.
- Hotelbuchung für 1-7 Minecraft-Tage, 5 Euro je Tag, Bar-/Karten-PIN-Zahlung. Persönliche zeitlich begrenzte Schlüssel, Ersatzschlüssel, automatische Türschließung und große Belegungsanzeige.
- Ablauf nach Minecraft-Weltzeit einschließlich übersprungener Nächte. Schlüssel ungültig; zurückgelassene Sachen bis sieben Minecraft-Tage im Fundsachenlager. Abholung gegen Tagespauschale, Kartenzahlung mit PIN oder bestätigte Entsorgung.
- Einpacken: leere Haupthand + Umschalt-Rechtsklick aufs Außenschild. Eigentümer, Änderungen, Bewohner, lose Gegenstände, Lager und Buchungen geprüft. Fehler behoben, bei dem der Spieler auf der Eingangsstufe selbst blockierte; verständlichere Ablehnungen und Koordinaten bei Blockänderungen.
- Mitarbeiter-Ausrichtung zur Kundenseite korrigiert, auch beim Laden bestehender Mitarbeiter. Sitzbänke in Bank und Hotelrezeption einen Block nach links versetzt.
- Digitale Bedienoberflächen für Bank, Automat, Verkauf und Hotel; Waren-/Zimmerkarten und PIN-Ziffernfeld. Neues Automatenmodell mit Bildschirm, Kartenleser, Tastatur und Geldausgabe; Hotelterminal mit Bildschirm und Tastatur.
- Handbuch mit Einleitung und Startfolge: Gebäude aufstellen, Konto eröffnen, PIN anlegen, Hotelzimmer buchen. Menübedienung, Ausrüstung und gemeinsame Kiste erklärt. Neue Ausgabe über `/ngcity anleitung`.
- Zweisprachiger Webkatalog und deutsche/englische PDF-Handbücher aktualisiert.

## Abnahme und Grenzen

Einzelspieler-Abnahme durch den Eigentümer am 19.10.2026 bestätigt: alle bisherigen NG-City-Funktionen getestet; gemeinsame Startkiste am Weltspawn gesetzt, alle Inhalte vorhanden und alle drei Gebäude baubar. Der Mehrspieler-Test folgt nach Abschluss des derzeitigen Minecraft-Interface-Umbaus.

79 eindeutige automatische Testfunktionen über Gesamt- und gezielte Suiten abgedeckt, kein einzelner gemeinsamer 79/79-Lauf behauptet. Letzte Läufe: 77/79, anschließend gezielt 15/15; vollständige Fehler-/Fixturegrenzen in `docs/test-results/dev-0.0.2-shared-starter.json`. Zuvor 2.104 Preisbündelprüfungen bestanden. Keine Testklassen/-strukturen in der JAR; 48 Produktionsklassen und Ressourcen geprüft.

Keine MC02-Installation. Verkehr und Fahrzeuge bleiben eigenständige Mods; eine gemeinsame Wirtschaftsanbindung ist noch nicht implementiert oder gemeinsam geprüft. Alte Hotelgrößen werden nicht automatisch über ihre Umgebung vergrößert: nach Räumen/Beenden der Buchungen einpacken und neu setzen.
