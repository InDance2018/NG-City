# Digitale Bedienoberflächen · dev-0.0.2

Eigentümerauftrag 19.10.2026: Bank, Geldautomat, Verkauf und Hotel modern/digital gestalten; Geldautomat an realen Geräten orientieren. Dateiname bleibt `ng_city-1.20.2-dev-0.0.2.jar`.

- Eigener registrierter Menütyp und ausschließlich clientseitiger Bildschirm, mit dunklem Bedienfeld, türkisfarbenen Akzenten und sichtbaren Beschriftungen. Keine Truhengrafik oder Spielerinventar im Servicemenü.
- Bank mit Konto-/Kartenaktionen, Geldautomat mit Auszahlungsfeldern und Einzahlung, Verkauf mit neun Warenkarten je Ansichtsseite (bis zu 27 Waren pro Serverseite), Hotel mit Zimmerkarten, Buchungstagen, Zahlung und Fundsachen.
- Vorhandene serverseitige Aktionen und PIN-Prüfungen bleiben zuständig. Der Client sendet gewöhnliche geprüfte Containerklicks. Kontostand, Meldungen und Waren stammen aus synchronisierten Menüfeldern.
- PIN-Ziffernfeld mit Maus oder Zifferntasten einschließlich Nummernblock; Rücktaste löscht, Eingabe bestätigt. Tab und Eingabe bedienen die übrigen Schaltflächen. ESC schließt.
- Eigenes NG-Bank-Automatenmodell mit Gehäuse, Bildschirm, Kartenleser, Tastatur und Geldausgabe. Hotelterminal mit Bildschirm, Standfuß und Tastatur. Eigene prozedural gezeichnete Texturen, keine Übernahme von Bankmarken oder fremden Modellen.
- Bestehende Automaten und Terminals verwenden die neuen Modelle nach Laden der neuen JAR; kein Gebäude-Neubau für diese Darstellung nötig.

Prüfung: Java-17-Kompilierung gegen Minecraft 1.20.2/Forge 48.1.0. Automatisierte Forge-Spieltests und Archivprüfung siehe `docs/test-results/dev-0.0.2-digital.json`. Eine grafische Minecraft-Clientabnahme steht aus; Kompilierung und Servertests belegen keine tatsächliche Bildschirmdarstellung. Keine Serverinstallation oder externe Veröffentlichung durch diesen Auftrag.

67 eindeutige Testfälle über zwei Läufe abgedeckt (65/67 und 66/67). Der erste Lauf benötigte eine Korrektur der Mock-Spieler-Verbindungsattribute für Forge-Menüpakete; danach sind beide Menü-/PIN-Fälle bestanden. Im zweiten Lauf verzögerte sich die Registrierung eines Ankauf-Mitarbeiters; derselbe Fall bestand im ersten Lauf. Kein gemeinsamer 67/67-Lauf wird behauptet. 45 Produktionsklassen vor Reobf bytegleich mit den getesteten Klassen; Ressourcen bytegleich mit Quellen, Archiv-CRC und 72 Modellflächen/Texturverweise geprüft.

Handbuch-Folgeauftrag 19.10.2026: 28 kurze Buchseiten mit Einleitung und Startfolge: alle drei Gebäude setzen, Bankkonto eröffnen, am Automaten vierstellige PIN anlegen, Hotelzimmer buchen. Anleitung für digitale Menüs aktualisiert. Neue Startkisten enthalten die neue Ausgabe; vorhandene Bücher mit `/ngcity anleitung` ersetzen. Nur Guide.class geändert, alle übrigen JAR-Inhalte bytegleich; Java-17-Build und Archivprüfung bestanden. Keine erneuten Spieltests für die reine Textkorrektur. Aktuelle JAR-Prüfsumme in `docs/test-results/dev-0.0.2-guide.json`.
