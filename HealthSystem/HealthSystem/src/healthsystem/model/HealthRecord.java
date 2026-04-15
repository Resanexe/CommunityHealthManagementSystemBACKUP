package healthsystem.model;

import java.time.LocalDate;

/**
 * HealthRecord stores medical readings for a patient.
 * Implements Alertable interface to detect concerning health values.
 */
public class HealthRecord implements Alertable {

    private String recordId;
    private String patientId;
    private LocalDate recordDate;
    private double weight;       // kg
    private double height;       // cm
    private int systolicBP;      // mmHg (upper)
    private int diastolicBP;     // mmHg (lower)
    private double temperature;  // Celsius
    private int heartRate;       // bpm
    private String notes;

    // Constructor
    public HealthRecord(String recordId, String patientId, LocalDate recordDate,
                        double weight, double height, int systolicBP, int diastolicBP,
                        double temperature, int heartRate, String notes) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.recordDate = recordDate;
        this.weight = weight;
        this.height = height;
        this.systolicBP = systolicBP;
        this.diastolicBP = diastolicBP;
        this.temperature = temperature;
        this.heartRate = heartRate;
        this.notes = notes;
    }

    // Calculate BMI (processing logic)
    public double getBMI() {
        double heightM = height / 100.0;
        return Math.round((weight / (heightM * heightM)) * 10.0) / 10.0;
    }

    // BMI category
    public String getBMICategory() {
        double bmi = getBMI();
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25.0) return "Normal";
        else if (bmi < 30.0) return "Overweight";
        else return "Obese";
    }

    // --- Alertable interface implementation ---

    @Override
    public String generateAlert() {
        StringBuilder alerts = new StringBuilder();

        // Blood pressure check
        if (systolicBP >= 140 || diastolicBP >= 90) {
            alerts.append("⚠ HIGH BLOOD PRESSURE detected (").append(systolicBP).append("/").append(diastolicBP).append(" mmHg). ");
        } else if (systolicBP < 90 || diastolicBP < 60) {
            alerts.append("⚠ LOW BLOOD PRESSURE detected (").append(systolicBP).append("/").append(diastolicBP).append(" mmHg). ");
        }

        // Temperature check
        if (temperature >= 38.0) {
            alerts.append("⚠ FEVER detected (").append(temperature).append("°C). ");
        } else if (temperature < 36.0) {
            alerts.append("⚠ LOW TEMPERATURE detected (").append(temperature).append("°C). ");
        }

        // Heart rate check
        if (heartRate > 100) {
            alerts.append("⚠ HIGH HEART RATE (").append(heartRate).append(" bpm). ");
        } else if (heartRate < 60) {
            alerts.append("⚠ LOW HEART RATE (").append(heartRate).append(" bpm). ");
        }

        // BMI check
        String bmiCat = getBMICategory();
        if (bmiCat.equals("Obese")) {
            alerts.append("⚠ BMI indicates OBESITY (").append(getBMI()).append("). ");
        } else if (bmiCat.equals("Underweight")) {
            alerts.append("⚠ BMI indicates UNDERWEIGHT (").append(getBMI()).append("). ");
        }

        return alerts.length() > 0 ? alerts.toString().trim() : null;
    }

    @Override
    public boolean isHealthy() {
        return generateAlert() == null;
    }

    // Getters and Setters
    public String getRecordId() { return recordId; }
    public String getPatientId() { return patientId; }
    public LocalDate getRecordDate() { return recordDate; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public int getSystolicBP() { return systolicBP; }
    public void setSystolicBP(int systolicBP) { this.systolicBP = systolicBP; }
    public int getDiastolicBP() { return diastolicBP; }
    public void setDiastolicBP(int diastolicBP) { this.diastolicBP = diastolicBP; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Serialize to CSV
    public String toCsvLine() {
        return String.join(",",
            recordId, patientId, recordDate.toString(),
            String.valueOf(weight), String.valueOf(height),
            String.valueOf(systolicBP), String.valueOf(diastolicBP),
            String.valueOf(temperature), String.valueOf(heartRate),
            notes.replace(",", ";"));
    }

    // Deserialize from CSV
    public static HealthRecord fromCsvLine(String line) {
        String[] p = line.split(",", 10);
        return new HealthRecord(p[0], p[1], LocalDate.parse(p[2]),
            Double.parseDouble(p[3]), Double.parseDouble(p[4]),
            Integer.parseInt(p[5]), Integer.parseInt(p[6]),
            Double.parseDouble(p[7]), Integer.parseInt(p[8]),
            p[9].replace(";", ","));
    }

    @Override
    public String toString() {
        return recordDate + " | BMI: " + getBMI() + " (" + getBMICategory() + ")" +
               " | BP: " + systolicBP + "/" + diastolicBP +
               " | Temp: " + temperature + "°C | HR: " + heartRate + " bpm";
    }
}
