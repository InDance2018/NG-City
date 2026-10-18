# NG-City · Grundlagenplanung

Stand: 18.10.2026. Quelle: ausdrückliche Eigentümerentscheidungen im NG-City-Planungsgespräch vom 18.10.2026. Status: Planung; keine Mod-Implementierung, kein Build und keine Installation beauftragt.

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
