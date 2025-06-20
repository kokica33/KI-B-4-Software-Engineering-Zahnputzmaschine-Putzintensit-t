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
- [x] Iteration 2: Drucksensor-Integration
- [x] Iteration 3: Timer und erweiterte Features (UI und Statistiken)

## Installation & Ausführung
```bash
# Build
./gradlew build

# Tests ausführen
./gradlew test

# Anwendung starten
./gradlew run
```

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
# Requirements Specification

## Funktionale Requirements

## RF-001: Drei-stufige Putzintensität
- **Priorität**: Hoch
- **Beschreibung**: System bietet drei Intensitätsstufen
- **Akzeptanzkriterien**: 
  - Sanft: 15.000 Schwingungen/min
  - Normal: 25.000 Schwingungen/min  
  - Stark: 35.000 Schwingungen/min
- **Status**: ✅ Implementiert
- **Tests**: TC-001, TC-002, TC-003

## RF-002: Ein-Button-Bedienung
- **Priorität**: Hoch
- **Beschreibung**: Zyklisches Durchschalten der Modi
- **Akzeptanzkriterien**: OFF → Sanft → Normal → Stark → OFF
- **Status**: ✅ Implementiert
- **Tests**: TC-004

- ## NF-001: Reaktionszeit
- **Beschreibung**: Zustandswechsel < 100ms
- **Status**: ⏳ Zu testen

## NF-002: Wartbarkeit
- **Beschreibung**: State Pattern für Erweiterbarkeit
- **Status**: ✅ Erfüllt

# Traceability Matrix - Zahnputzmaschine

## Übersicht
Diese Matrix zeigt die Nachverfolgbarkeit von Requirements über Design, Implementierung bis zu den Tests.

## Legende
- ✅ Vollständig implementiert/getestet
- ⏳ In Bearbeitung
- ❌ Noch nicht begonnen
- 🔄 Teilweise implementiert

## Funktionale Requirements

| Req-ID | Requirement | Priorität | Design Component | Implementation | Test Cases | Status | Iteration |
|--------|-------------|-----------|------------------|----------------|------------|---------|-----------|
| RF-001 | Drei Intensitätsstufen (15k/25k/35k) | Hoch | BrushState Interface, IntensityLevel Enum | GentleState.java, NormalState.java, IntenseState.java | TC-001, TC-002, TC-003 | ✅ | 1 |
| RF-002 | Ein-Button-Bedienung | Hoch | ToothbrushController | buttonPress() Methode | TC-004, TC-005 | ✅ | 1 |
| RF-003 | Zyklisches Durchschalten | Hoch | State Pattern Implementation | handle() in allen States | TC-006 | ✅ | 1 |
| RF-004 | LED-Statusanzeige | Mittel | BrushState.enter() | Konsolen-Output (simuliert) | TC-007 | ✅ | 1 |
| RF-005 | Automatisches Ausschalten | Mittel | IdleState | setState() zu IdleState | TC-008 | ✅ | 1 |
| RF-006 | Drucksensor-Integration | Hoch | PressureSensor (geplant) | - | TC-009, TC-010 | ✅ | 2 |
| RF-007 | Druckwarnung LED | Mittel | WarningSystem (geplant) | - | TC-011 | ✅ | 2 |
| RF-008 | Putzzeit-Timer | Mittel | BrushTimer (geplant) | - | TC-012, TC-013 | ✅ | 3 |
| RF-009 | 30-Sekunden-Intervalle | Niedrig | IntervalNotifier (geplant) | - | TC-014 | ✅ | 3 |
| RF-010 | Statistik-Erfassung | Niedrig | StatisticsCollector (geplant) | - | TC-015 | ✅ | 4 |

## Nicht-funktionale Requirements

| Req-ID | Requirement | Priorität | Design Component | Implementation | Test Cases | Status | Iteration |
|--------|-------------|-----------|------------------|----------------|------------|---------|-----------|
| NF-001 | Reaktionszeit < 100ms | Hoch | Event-Handling | Direkte Zustandswechsel | TC-016 | ⏳ | 1 |
| NF-002 | Wartbarkeit (State Pattern) | Hoch | State Pattern | Alle State-Klassen | Code Review | ✅ | 1 |
| NF-003 | Erweiterbarkeit | Hoch | Interface-Design | BrushState Interface | Code Review | ✅ | 1 |
| NF-004 | Testabdeckung > 80% | Mittel | Unit Tests | JUnit Tests | Coverage Report | ⏳ | 1 |
| NF-005 | Logging-Funktionalität | Niedrig | SimpleLogger | Logger in allen Klassen | TC-017 | ✅ | 1 |

## Rückverfolgbarkeit

### Von Requirements zu Tests
- **RF-001** → TC-001 (Gentle), TC-002 (Normal), TC-003 (Intense)
- **RF-002** → TC-004 (Button funktioniert), TC-005 (Nur ein Button)
- **RF-003** → TC-006 (Kompletter Zyklus)
- **RF-004** → TC-007 (LED-Simulation)
- **RF-005** → TC-008 (Return to OFF)

### Von Tests zu Requirements
- **TC-001 bis TC-003** validieren RF-001
- **TC-004 bis TC-005** validieren RF-002
- **TC-006** validiert RF-003
- **TC-007** validiert RF-004
- **TC-008** validiert RF-005

## Änderungshistorie
| Datum | Version | Änderung | Autor |
|-------|---------|----------|--------|
| 12-06-2025 | 1.0 | Initiale Matrix erstellt | Nikola Valchev |
| 12-06-2025 | 1.1 | Tests hinzugefügt | Nikola Valchev |
