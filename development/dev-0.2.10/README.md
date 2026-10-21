# Entwicklungssnapshot dev-0.2.10

Enthält die mit der ausgelieferten JAR archivierten vorhandenen Java-Quellen der Hotfixstufen und die aktuellen Räumungs-/Diagnosepatcher. `embedded/` erhält die ursprünglichen Versionspfade, damit ältere Zwischenstufen nicht als aktuelle Implementierung verwechselt werden.

Die getestete JAR liegt unter `dist/dev-0.2.10/`. Der reguläre Gradle-Hauptquellbaum des Repositories bleibt ein historischer dev-0.0.2-Stand. Dieser Snapshot ist kein vollständig zusammengeführter, aus diesem Quellbaum reproduzierbar geprüfter dev-0.2.10-Build. Keine aktuelle Neubauprüfung durch die Veröffentlichung behauptet.

Aktuelle Räumung: `clearance-work/src/`, ASM-Patcher `clearance-work/PatchClearance.java`. Serverintegrationstests und Grenzen: `../../docs/test-results/dev-0.2.10.json`. Testquellen gehören nicht in die Produktions-JAR. Die fokussierte Zusatzsuite für Bank/Theke ist separat abgelegt; ihre Serverprotokolle dokumentieren den tatsächlichen Lauf.
