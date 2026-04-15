# Community Health Management System
## SDG 3: Good Health and Well-being
### BIT1123 Object Oriented Programming — Final Project

---

## How to Run in IntelliJ IDEA

### Prerequisites
- IntelliJ IDEA (Community or Ultimate)
- Java 17 or above
- JavaFX SDK (download from https://gluonhq.com/products/javafx/)

### Setup Steps

1. **Open Project in IntelliJ**
   - Open IntelliJ → File → Open → Select the `HealthSystem` folder

2. **Add JavaFX as a Library**
   - File → Project Structure → Libraries → Click `+` → Java
   - Browse to your JavaFX SDK `lib` folder (e.g. `javafx-sdk-21/lib`)
   - Click OK and Apply

3. **Set Run Configuration**
   - Run → Edit Configurations → `+` → Application
   - Main class: `healthsystem.ui.MainApp`
   - VM Options: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`
   - Replace `/path/to/javafx-sdk` with your actual path (e.g. `C:\javafx-sdk-21\lib` on Windows)

4. **Mark Source Root**
   - Right-click `src` folder → Mark Directory As → Sources Root

5. **Run the Application**
   - Click the green Run button ▶

---

## Project Structure

```
HealthSystem/
├── src/
│   └── healthsystem/
│       ├── model/
│       │   ├── Person.java          (Abstract class — Abstraction)
│       │   ├── Patient.java         (Extends Person — Inheritance)
│       │   ├── Doctor.java          (Extends Person — Inheritance)
│       │   ├── Appointment.java     (Encapsulation + Enum)
│       │   ├── HealthRecord.java    (Implements Alertable — Interface)
│       │   └── Alertable.java       (Interface — Abstraction)
│       ├── logic/
│       │   └── HealthSystem.java    (Collections — HashMap, ArrayList)
│       ├── util/
│       │   └── FileHandler.java     (File I/O — Persistence)
│       └── ui/
│           └── MainApp.java         (JavaFX GUI)
└── data/                            (Auto-created at runtime)
    ├── patients.csv
    ├── doctors.csv
    ├── appointments.csv
    └── health_records.csv
```

---

## OOP Concepts Demonstrated

| Concept         | Where Used |
|-----------------|------------|
| Abstraction     | `Person` abstract class, `Alertable` interface |
| Inheritance     | `Patient` and `Doctor` extend `Person` |
| Polymorphism    | `getRole()`, `getSummary()` overridden; `Alertable` references |
| Encapsulation   | All fields private, accessed via getters/setters |
| Collections     | `HashMap<String, Patient>`, `HashMap<String, Doctor>`, `ArrayList<Appointment>` |
| File Handling   | `FileHandler.java` — CSV read/write for all entities |

---

## Features

- **Dashboard** — live stats (patients, doctors, appointments, alerts)
- **Patient Management** — register, search, remove patients
- **Doctor Management** — register, remove doctors
- **Appointment Booking** — book, complete, cancel appointments
- **Health Records** — record vitals (weight, height, BP, temperature, heart rate)
- **Automatic Health Alerts** — flags abnormal BP, fever, BMI, heart rate
- **Data Persistence** — all data saved to CSV files automatically

---

## SDG 3 Alignment

This system directly supports **SDG 3: Good Health and Well-being** by:
- Enabling community health monitoring at low cost
- Providing real-time health alerts to catch early warning signs
- Streamlining appointment management to improve healthcare access
- Maintaining persistent patient health records for continuity of care
