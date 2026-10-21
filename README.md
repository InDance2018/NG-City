# NG-City

**Aktueller ausgelieferter Entwicklungsstand: dev-0.2.10 · 21.10.2026**
Minecraft 1.20.2 · Forge 48.1.0 · Java 17

NG-City ist die Stadt- und Wirtschaftsgrundlage für NeverGate. Der begehbare Verwaltungskomplex besitzt vier Gebäudeflügel und 13 Mitarbeiter einschließlich Digitalzentrale. Bank, Handel/Ausrüstung und Hotel sind nutzbar; weitere Fachverwaltungen sind vorbereitet.

## Aktueller Stand und Download

- [Getestete Entwicklungs-JAR dev-0.2.10](dist/dev-0.2.10/ng_city-1.20.2-dev-0.2.10.jar)
- [Umfang, Korrekturen und Arclight-Prüfung](DEV-0.2.10.md)
- [Eigentümerabnahme Einzelspieler und Multiplayer](ABNAHME-2026-10-21.md)
- [Neue Planung: Bauamt, Strom, Wirtschaft und Straßenmodule](PLANUNG-2026-10-21.md)
- [Vorhandene Hotfix-/Patchquellen](development/dev-0.2.10/README.md)

SHA-256: `c4522ac7916bd5b9934ef6510279e4c97365ce4146de8bf4b44d1f4cb757061f` · 1.381.931 Bytes.

Der Eigentümer hat den bisherigen Umbau am 21.10.2026 im Singleplayer und Multiplayer bestätigt. dev-0.2.10 ist auf MC02 aktiviert und der Server online. Tiere/Monster im Bauplatz werden standardmäßig erst nach erfolgreichem Setzen entfernt; Spieler und Mitarbeiter bleiben geschützt. Es handelt sich um eine Entwicklungsfassung, keinen stabilen Release.

## Einstieg

Client und Server benötigen die passende City-JAR; nur eine City-Version laden. Die gemeinsame Startkiste wird anhand der tatsächlichen Welt und ihres Weltspawns verwaltet. Vollständige unbenutzte Kisten können dem geänderten Spawn folgen; benutzte Kisten werden nicht aufgefüllt. Keine festen Weltnamen nötig.

Beim Bankpersonal Konto eröffnen und PIN festlegen. Bankkarte/Geldbörse sind spielergebunden. Handel und Ausrüstung nutzen das bestehende Geldsystem; Hotelbuchungen liefern persönliche Schlüssel für Zimmer und Truhe. Weitere Verwaltungsfunktionen sind erst zu entwickeln.

Konfiguration: `<Welt>/serverconfig/ng_city-server.toml`. `clearAnimalsAndMonstersOnPlacement = true` aktiviert die Bauplatzräumung; `false` stellt die bisherige Mob-Blockade wieder her. Die bisherigen Preiswerte verwenden intern Zehn-Cent-Einheiten.

## Modfamilie

NG-Verkehr und NG-Fahrzeuge bleiben eigenständig nutzbar. Gemeinsame City-Wirtschaft und Verwaltungsanbindungen sind Zielarchitektur und noch keine fertig geprüfte Gegenmod-Integration. Die allein spielbare City-Grundausstattung wird nach dem Überblick über die Dev-Zusatzpakete festgelegt. Bestehende Mods werden vorerst nicht auseinandergerissen.

## Quellstand und bisherige Dokumentation

Die aktuelle ausgelieferte JAR und vorhandenen Patchquellen sind oben gesichert. Der reguläre Gradle-Quellbaum `src/main` bildet noch den historischen dev-0.0.2-Stand ab; seine vollständige Zusammenführung mit allen späteren lokalen Umbauten bleibt offen. Ein normaler Gradle-Build dieses Baums erzeugt daher nicht die hier getestete dev-0.2.10. Die Version des historischen Buildskripts wird nicht nur kosmetisch hochgesetzt.

Frühere Entwicklungs-/Testberichte bleiben als Historie erhalten: [dev-0.0.2](DEV-0.0.2.md), [Änderungen](CHANGELOG-dev-0.0.2.md), [Abnahme 19.10.](ABNAHME-2026-10-19.md). Deren damalige Aussage „Mehrspieler offen/keine MC02-Installation“ ist durch die heutige Fortschreibung überholt. Der Webkatalog auf nevergate.de wurde mit diesem GitHub-/Discord-Auftrag nicht aktualisiert.
