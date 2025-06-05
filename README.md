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

## 2. Wiki Setup für Dokumentation

### 2.1 Wiki-Seiten erstellen
Im GitHub Wiki solltest du folgende Seiten anlegen:

1. **Home** - Projektübersicht
2. **Requirements** - Alle funktionalen und nicht-funktionalen Anforderungen
3. **Architecture** - Architekturentscheidungen und Patterns
4. **Iteration-1** - Dokumentation der ersten Iteration
5. **Test-Strategy** - Teststrategie und Testfälle
6. **Meeting-Notes** - Fortschrittsdokumentation

### 2.2 Requirements Wiki-Seite Template
```markdown
# Requirements Specification

## Funktionale Requirements

### RF-001: Drei-stufige Putzintensität
- **ID**: RF-001
- **Priorität**: Hoch
- **Beschreibung**: Das System muss drei diskrete Intensitätsstufen bereitstellen
- **Akzeptanzkriterien**: 
  - Sanft: 15.000 Schwingungen/min
  - Normal: 25.000 Schwingungen/min
  - Stark: 35.000 Schwingungen/min
- **Status**: ⏳ In Entwicklung
- **Iteration**: 1
- **Tests**: TC-001, TC-002, TC-003

[Weitere Requirements...]

## Traceability Matrix
| Requirement | Design Component | Test Cases | Status |
|-------------|------------------|------------|---------|
| RF-001 | BrushState Classes | TC-001-003 | 🔄 |
