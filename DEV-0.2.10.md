# NG-City · dev-0.2.10 · Stand 21.10.2026

Minecraft 1.20.2 · Forge 48.1.0 · Java 17. Entwicklungsfassung; kein stabiler Release.

## Aktuelles Artefakt und Installation

- Datei: `ng_city-1.20.2-dev-0.2.10.jar`
- Größe: 1.381.931 Bytes
- SHA-256: `c4522ac7916bd5b9934ef6510279e4c97365ce4146de8bf4b44d1f4cb757061f`
- Auf MC02 aktiviert und nach Neustart mit frischem Laufzeitstatus als online verifiziert. Vorversion dev-0.2.9 deaktiviert aufbewahrt.
- Kein Plugin wurde für die Modänderung verändert.
- Client und Server benötigen die passende City-Fassung. Die heutigen Tests belegen keine neue gemeinsame Wirtschaftsanbindung mit NG-Verkehr/NG-Fahrzeuge.

## Umbau und Korrekturen

- Begehbarer Verwaltungskomplex mit vier Gebäudeflügeln, 13 Mitarbeitern und Digitalzentrale. Bank, Handel/Ausrüstung und Hotel enthalten; weitere Fachverwaltungen zunächst vorbereitet.
- Digitale Bedienoberflächen, Türmonitore, mobile Terminals, Theken-/Mitarbeiterausrichtung und Geldautomaten-Kollisionsform überarbeitet.
- Gemeinsame Startkiste folgt bei vollständigem, unverändertem Inhalt dem tatsächlichen Weltspawn; Zuordnung zur tatsächlich verwendeten Welt ohne feste Weltnamen. Benutzte oder veränderte Kisten werden nicht einfach aufgefüllt.
- Platzierungsdiagnose meldet konkrete Serverablehnungen und blockierende Entitäten.
- Mitarbeiter werden auf Arclight mit geeignetem CUSTOM-Spawn eingefügt; reguläre Schutz-/Spawnereignisse bleiben wirksam. Ein ablehnendes Plugin wird nicht umgangen.
- dev-0.2.10: Tiere und Monster im tatsächlichen Bauplatz blockieren den Bau bei aktivierter Option nicht mehr und werden **erst nach erfolgreicher Platzierung** entfernt. Slimes teilen sich dabei nicht; keine Mob-Drops. Spieler und Dorfbewohner einschließlich City-Mitarbeitern bleiben geschützt. Tiere außerhalb bleiben erhalten.
- Ablehnung wegen Gelände, Berechtigung/Schutz oder Mitarbeiter-Spawn führt nicht zur Tierentfernung.
- Gilt für Komplex, Einzelgebäude und Außenstellen-Theken direkt in der Mod.
- Serverkonfiguration: `clearAnimalsAndMonstersOnPlacement = true` (Standard). `false` stellt die bisherige Blockade durch Tiere/Monster wieder her.

## Eigentümerabnahme

Der Eigentümer bestätigte am 21.10.2026 den bisherigen kompletten Umbau im Singleplayer und Multiplayer. Konkret im Gespräch geprüft: Setzen des Komplexes; Konto eröffnen und PIN setzen; Hotelzimmer buchen und Schlüssel erhalten; Zimmer und Truhe nur mit Schlüssel öffnen; Ausrüstung kaufen und Erde verkaufen. Der gemeldete Schaufelverdacht wurde als Verwechslung von Rechtsklick-Wegbildung und Linksklick-Abbau geklärt, nicht als behobener Modfehler geführt.

Die allgemeine Bestätigung enthält keinen separat protokollierten Zwei-Spieler-Parallellauf. Neue Bauamt-, Strom- und Straßenmodul-Funktionen wurden damit nicht abgenommen: sie sind Planung.

## Arclight-Funktionsprüfung dev-0.2.10

Isolierter Arclight-Server 1.20.2 / 1.0.3-5abaeaf / Forge 48.1.0 / Java 17; keine Tests am produktiven Bauplatz.

Bestanden: vollständiger Komplex über den tatsächlichen Item-Einstieg; Kuh, Zombie und großer Slime entfernt; Tier außerhalb erhalten; keine Slime-Kinder/Beute; 13 Mitarbeiter erhalten; deaktivierte Option blockiert wieder; Dorfbewohner geschützt; abgelehnte Gelände-/Blockereignis-/Mitarbeiterplatzierung erhält Tiere; erfolgreicher Wiederholungsversuch; zwei Komplexe mit je 13 Mitarbeitern; Einzelbank und Außenstellen-Theke mit erhaltenem Mitarbeiter.

Begrenzung: Der isolierte Server zeigte den bereits vorher bestehenden ChunkStorage-Wartezustand beim Herunterfahren. Dieser Lauf ist daher **kein bestandener Speicher-/Neustartnachweis**. Die Einsetz-Funktionstests sind davon getrennt. Keine neue grafische Clientprüfung durch den automatisierten Serverlauf behauptet.

## Quellstand und Historie

Der bisherige Gradle-Hauptquellstand im Repository lag vor dieser Fortschreibung bei dev-0.0.2. Der ausgelieferte aktuelle Hotfix-Build beruht auf späteren lokalen Artefakten und gezielten Bytecode-Patches. Zugehörige vorhandene Java-Patchquellen und Testnachweise werden als Entwicklungssnapshot abgelegt; dies ist kein vorgetäuschter vollständiger Gradle-Neubau der aktuellen JAR aus dem historischen `src/main`.

Historische Berichte dev-0.0.1/dev-0.0.2 bleiben als damalige Nachweise erhalten. Ihr „Mehrspieler offen/keine MC02-Installation“ beschreibt den damaligen Stand, nicht die heutige Abnahme. Die vollständige Zusammenführung der späteren Änderungen in den regulären Gradle-Quellbaum ist separat offen.

## Nächste Planung

Bauverwaltung/Messgerät, Stromnetz, gemeinsame modübergreifende Wirtschaft und Straßenmodule sind in [PLANUNG-2026-10-21.md](PLANUNG-2026-10-21.md) festgehalten. Keine dieser neuen Funktionen ist in dev-0.2.10 implementiert.
