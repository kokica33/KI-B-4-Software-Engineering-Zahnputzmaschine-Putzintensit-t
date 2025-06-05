# Zahnputzmaschine - Intelligente Putzintensitätssteuerung

## Projektbeschreibung
Dieses Projekt implementiert eine intelligente Steuerung für die Putzintensität einer elektrischen Zahnbürste im Rahmen der Software Engineering Vorlesung.

### Hauptfeatures
- 3-stufige Intensitätsregelung (Sanft/Normal/Stark)
- Ein-Button-Bedienung mit zyklischem Durchschalten
- Intelligente Drucksensorik mit LED-Warnsystem
- Automatische Sicherheitsabschaltung
- Putzzeit-Tracking mit Intervallbenachrichtigungen

## Projektstruktur
- `docs/` - Projektdokumentation
- `src/` - Quellcode
- `uml/` - UML Diagramme

## Entwicklungsumgebung
- Java 17
- Maven/Gradle
- PlantUML für Diagramme

## Iterationen
- [x] Iteration 1: Grundfunktionalität (State Machine)
- [ ] Iteration 2: Drucksensor-Integration
- [ ] Iteration 3: Timer und erweiterte Features
- [ ] Iteration 4: GUI und Statistiken

## Installation & Ausführung
```bash
# Build
./gradlew build

# Tests ausführen
./gradlew test

# Anwendung starten
./gradlew run
