package healthsystem.util;

import healthsystem.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler utility class — handles all file I/O operations.
 * Demonstrates file handling and data persistence.
 */
public class FileHandler {

    private static final String DATA_DIR = "data/";
    private static final String PATIENTS_FILE  = DATA_DIR + "patients.csv";
    private static final String DOCTORS_FILE   = DATA_DIR + "doctors.csv";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.csv";
    private static final String RECORDS_FILE   = DATA_DIR + "health_records.csv";

    // Ensure data directory exists
    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    // --- Generic write method ---
    private static void writeLines(String filepath, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + filepath + ": " + e.getMessage());
        }
    }

    // --- Generic read method ---
    private static List<String> readLines(String filepath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading " + filepath + ": " + e.getMessage());
        }
        return lines;
    }

    // --- Patients ---
    public static void savePatients(List<Patient> patients) {
        List<String> lines = new ArrayList<>();
        for (Patient p : patients) lines.add(p.toCsvLine());
        writeLines(PATIENTS_FILE, lines);
    }

    public static List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();
        for (String line : readLines(PATIENTS_FILE)) {
            try { patients.add(Patient.fromCsvLine(line)); }
            catch (Exception e) { System.err.println("Skipping bad patient line: " + line); }
        }
        return patients;
    }

    // --- Doctors ---
    public static void saveDoctors(List<Doctor> doctors) {
        List<String> lines = new ArrayList<>();
        for (Doctor d : doctors) lines.add(d.toCsvLine());
        writeLines(DOCTORS_FILE, lines);
    }

    public static List<Doctor> loadDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        for (String line : readLines(DOCTORS_FILE)) {
            try { doctors.add(Doctor.fromCsvLine(line)); }
            catch (Exception e) { System.err.println("Skipping bad doctor line: " + line); }
        }
        return doctors;
    }

    // --- Appointments ---
    public static void saveAppointments(List<Appointment> appointments) {
        List<String> lines = new ArrayList<>();
        for (Appointment a : appointments) lines.add(a.toCsvLine());
        writeLines(APPOINTMENTS_FILE, lines);
    }

    public static List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        for (String line : readLines(APPOINTMENTS_FILE)) {
            try { appointments.add(Appointment.fromCsvLine(line)); }
            catch (Exception e) { System.err.println("Skipping bad appointment line: " + line); }
        }
        return appointments;
    }

    // --- Health Records ---
    public static void saveHealthRecords(List<HealthRecord> records) {
        List<String> lines = new ArrayList<>();
        for (HealthRecord r : records) lines.add(r.toCsvLine());
        writeLines(RECORDS_FILE, lines);
    }

    public static List<HealthRecord> loadHealthRecords() {
        List<HealthRecord> records = new ArrayList<>();
        for (String line : readLines(RECORDS_FILE)) {
            try { records.add(HealthRecord.fromCsvLine(line)); }
            catch (Exception e) { System.err.println("Skipping bad record line: " + line); }
        }
        return records;
    }
}
