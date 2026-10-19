# NG-City · Grundlagenplanung

Stand: 18.10.2026. Quelle: ausdrückliche Eigentümerentscheidungen im NG-City-Planungsgespräch vom 18.10.2026. Historischer Ausgangsstand: Planung vom 18.10.2026. Neuer Bauauftrag vom 19.10.2026 siehe unten.

## Ziel

NeverGate soll schrittweise eine vollständige Minecraft-Stadt mit Fahrzeugen, Verkehrssystem und weiteren künftigen Projekten ermöglichen. NG-City bildet die gemeinsame Grundlage für das Stadt- und Wirtschaftssystem.

## Verbindliche Architektur

- NG-City, NG-Verkehr und NG-Fahrzeuge bleiben getrennte Mods und JARs.
- NG-Verkehr und NG-Fahrzeuge müssen jeweils ohne NG-City selbständig funktionieren. NG-City darf dort keine Pflichtabhängigkeit werden.
- Wirtschaftsfunktionen dürfen ausschließlich greifen, wenn NG-City tatsächlich mitinstalliert und die Wirtschaftsanbindung verfügbar ist.
- Ohne NG-City gibt es in NG-Verkehr und NG-Fahrzeuge keine aktiven Wirtschaftsfunktionen: keine Geldkonten, wirtschaftlichen Preise, Gebühren, Zahlungen oder Ersatzwährung. Die gewöhnlichen Verkehrs- und Fahrzeugfunktionen bleiben nutzbar.
- Mit NG-City nutzen die beteiligten Mods ein gemeinsames zentrales Wirtschaftssystem. Konten und Geldbewegungen werden nicht parallel in jeder Mod geführt.
- Die bestehenden Mods werden später für diese optionale Anbindung angepasst. Diese Planung beschreibt das Ziel, keine bereits vorhandene oder getestete Integration.
- Weitere NG-Projekte können später nach demselben Prinzip angebunden werden.

## Vorgesehene Zuständigkeiten

| Projekt | Rolle |
|---|---|
| NG-City | Gemeinsame Stadt- und Wirtschaftsgrundlage; zentrale Verwaltung wirtschaftlicher Vorgänge |
| NG-Verkehr | Verkehrstechnik, Straßen und Verkehrsabläufe; spätere optionale Wirtschaftsanbindung |
| NG-Fahrzeuge | Fahrzeuge und Fahrzeugfunktionen; spätere optionale Wirtschaftsanbindung |
| Weitere NG-Projekte | Spätere Erweiterungen der Stadt mit optionaler Nutzung der Grundlage |

Parkgebühren und Fahrzeugkäufe sind Beispiele künftiger Anbindungen, keine bereits implementierten Funktionen.

## Noch gemeinsam festzulegen

Konkreter erster Funktionsumfang, Geld/Konten/Bargeld, Währung und Startguthaben, Stadtverwaltung, Grundstücke, Unternehmen und Berufe sowie technische API, Versionsprüfung, Speicherung und Verhalten bei Ausfall oder Entfernung von NG-City sind noch zu besprechen. Frühere Wirtschaftsvorschläge sind kein automatisch bestätigter Bauumfang.

Bei vorhandener NG-City, aber fehlgeschlagener oder unklarer Zahlung darf kein Vorgang als bezahlt gelten. Die technische Ausgestaltung wird vor der Umsetzung vereinbart.

## Späterer Prüfumfang

NG-Verkehr allein, NG-Fahrzeuge allein, beide ohne NG-City sowie beide mit kompatibler NG-City prüfen. Außerdem fehlende/inkompatible Gegenstelle, Mehrspieler, doppelte Zahlungen, Neustart und persistente Daten gesondert prüfen. Bisher wurde durch diese Planungsablage kein solcher Test durchgeführt.

## Beauftragter erster Bauumfang · 19.10.2026

Quelle: ausdrücklicher Eigentümerauftrag im NG-City-Gespräch am 19.10.2026.
Minecraft 1.20.2 / Forge 48.1.0, Java 17, Version `dev-0.0.1`.

- Startkiste mit drei Gebäudepaketen und lesbarer Bedienungsanleitung.
- Kompakte, fertig platzierbare, begehbare Bank, Hotel und Ankauf.
- Inneneinrichtung und je ein interaktiver Mitarbeiter.
- Platzierung in vier Richtungen mit Prüfung auf freien, geladenen, getragenen Bauplatz, Weltgrenze, Bauhöhe und Baurechte; kein Überschreiben bestehender Gebäude.
- Bank mit Schalter/Tresorbereich, Hotel mit Rezeption, Innentreppe und zwei Zimmern, Ankauf mit Annahmetheke/Lager.

Erste Mitarbeiterinteraktion: Übersicht des jeweiligen Gebäudes. Konten, Währung, Preise, Warenannahme und Zimmervermietung sind noch nicht festgelegt und werden nicht vorgetäuscht. Die Mod ist eine Gebäudegrundlage; sie ist noch kein aktiver Wirtschafts- oder Zahlungsanbieter. NG-Verkehr und NG-Fahrzeuge werden durch diesen Bauauftrag nicht verändert.

Umsetzung, Build, Spieltests, Installation und Veröffentlichung werden in `DEV-0.0.1.md` getrennt dokumentiert. Öffentliche Ankündigung: [Issue 1](https://github.com/InDance2018/NG-City/issues/1). Keine MC02-Installation durch diesen Auftrag.


## dev-0.0.2 · Eigentümerfreigabe vom 19.10.2026, 16:20 CEST

Der gemeinsam besprochene Gesamtumfang ist zur Umsetzung freigegeben: Bankkonto, UUID-gebundene Geldbörse/Karte, Geldautomat und NG-Bargeld (10/20/50 Cent, 1/2 Euro sowie 10/20/50 Euro), Vanilla-Ankauf, Hotelzahlung, Zimmerdisplay, persönliche zeitgebundene Schlüssel und Fundsachenverwaltung sowie Vorschau/Einpacken. Als konfigurierbare Testvorgaben gelten die vorgeschlagenen 250 Euro Start, 500 Euro Ankauf/24 Echtstunden, Hotel 5 Euro/MC-Tag und Aufbewahrung 1 Euro/MC-Tag. Implementierungs- und Prüfnachweise in DEV-0.0.2.md. Nur Einzelspieler-Nutzerprüfung; keine MC02-Installation. GitHub-Fortschreibung dieses Folgeauftrags noch nicht erfolgt.

## Aktueller Entwicklungsstand · 19.10.2026

Der freigegebene dev-0.0.2-Umfang einschließlich der Folgekorrekturen ist umgesetzt und als Entwicklungs-JAR bereitgestellt. Einzelspieler-Abnahme durch den Eigentümer am 19.10.2026 bestätigt: alle bisherigen NG-City-Funktionen getestet; gemeinsame Startkiste am Weltspawn gesetzt, alle Inhalte vorhanden und alle drei Gebäude baubar. Der Mehrspieler-Test folgt nach Abschluss des derzeitigen Minecraft-Interface-Umbaus.

Aktuelle Funktionen, Preise und Korrekturen: [CHANGELOG-dev-0.0.2.md](CHANGELOG-dev-0.0.2.md). Historische Testvorgaben oben sind überholt: aktuelles Ankaufslimit 2.000 Euro je Echtstunde; gemeinsame Spawnkiste und Ausrüstungskauf enthalten. Modübergreifende Wirtschaft bleibt offen.
