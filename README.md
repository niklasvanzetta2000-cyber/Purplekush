# PurpleKush-EliteMobs

Paper-Plugin für Minecraft Java **26.3** (Java 25).

Nachts werden einige Monster zu **Elite-Monstern**: Sie haben eine höhere Chance auf bessere Rüstung und Waffen
und sind von leuchtenden lila Partikeln umgeben. Sie bekommen **keinen Namen** und **keine Bossbar**.

## Kompilieren

Voraussetzung: **JDK 25** und Internetverbindung (Paper-API wird geladen).

```
./gradlew build        # Linux / macOS
gradlew.bat build      # Windows
```

Die fertige Datei liegt danach in `build/libs/PurpleKush-EliteMobs-1.0.0.jar`.

## Installieren

1. JAR in den Ordner `plugins/` des Paper-26.3-Servers legen.
2. Server neu starten (kein `/reload`).
3. Einstellungen in `plugins/PurpleKush-EliteMobs/config.yml` anpassen, dann `/elitemobs reload`.

## Befehle (Permission: `purplekush.elitemobs.admin`, Standard: OP)

| Befehl | Wirkung |
| --- | --- |
| `/elitemobs reload` | Config neu laden |
| `/elitemobs spawn [Typ]` | Elite-Monster zum Testen an deiner Position spawnen (z. B. `zombie`, `skeleton`) |

## Wichtige Einstellungen

- `elite.chance`: Chance pro Monster (Standard 0.15 = 15 %)
- `elite.night-only`: nur nachts
- `equipment.armor-tiers` / `weapon-tiers`: Gewichtung der Materialien
- `equipment.drop-chance`: Chance, dass die Ausrüstung beim Tod droppt
- `particles.*`: Partikelart, Farbe, Menge und Intervall
