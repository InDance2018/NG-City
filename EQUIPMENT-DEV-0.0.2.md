# Ausrüstungskauf im Ankauf · dev-0.0.2

Eigentümerfreigabe vom 19.10.2026: Im Ankauf zusätzliche Eisenrüstung und Werkzeuge kaufen. Jeder Gegenstand einmal je Spieler für 5 Euro; ab dem zweiten Kauf des jeweiligen Gegenstands gelten die bestätigten regulären Preise. Zehn Erstkäufe kosten zusammen 50 Euro.

| Gegenstand | Erstkauf | Ab zweitem Kauf |
|---|---:|---:|
| Eisenhelm | 5 Euro | 10 Euro |
| Eisenbrustpanzer | 5 Euro | 16 Euro |
| Eisenhose | 5 Euro | 14 Euro |
| Eisenstiefel | 5 Euro | 8 Euro |
| Eisenschwert | 5 Euro | 8 Euro |
| Eisenspitzhacke | 5 Euro | 12 Euro |
| Eisenaxt | 5 Euro | 12 Euro |
| Eisenschaufel | 5 Euro | 6 Euro |
| Eisenhacke | 5 Euro | 8 Euro |
| Schild | 5 Euro | 8 Euro |

Im digitalen Ankaufmenü rechts oben „Ausrüstung kaufen“ öffnen. Der angezeigte Preis ist persönlich und gilt für genau ein neues, unverzaubertes Teil mit voller Haltbarkeit. „Weiter“ zeigt den zehnten Artikel. „Zum Warenverkauf“ führt zurück. Zahlungsart unten links auswählen. Jeder Kartenkauf verlangt erneut die PIN; Bargeldzahlung nutzt loses NG-Bargeld oder die vorhandene persönliche Geldbörse.

Erfolgreiche Erstkäufe werden im bestehenden UUID-Konto unter `StarterPurchases` je Artikelkennung gespeichert. Leere/fehlende Daten bei älteren Spielständen bedeuten noch keine Erstkäufe. Verlust der Ausrüstung, Kartenersatz und erneutes Laden setzen den Erstkauf nicht zurück. Ohne Konto, gültige Zahlung oder Inventarplatz gibt es keine Ware, Belastung oder verbrauchten Erstkauf. Vor Abbuchung werden Ware und Wechselgeld gemeinsam auf Platz geprüft. Ändert sich der Preis während einer PIN-Abfrage, wird die Zahlung abgelehnt und der Artikel muss erneut ausgewählt werden. Käufe verbrauchen nicht das stündliche Auszahlungslimit des Warenankaufs.

Das Handbuch enthält Sortiment, Preise, Erstkaufregel und Zahlungsbedienung. Bestehende Bücher mit `/ngcity anleitung` ersetzen. Dateiname bleibt `ng_city-1.20.2-dev-0.0.2.jar`. Aktuelle Prüfnachweise in `docs/test-results/dev-0.0.2-equipment.json`. Grafische Minecraft-Clientabnahme bleibt offen; keine Serverinstallation oder externe Veröffentlichung.
