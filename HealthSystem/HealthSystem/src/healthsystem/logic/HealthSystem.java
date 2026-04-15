package healthsystem.logic;

import healthsystem.model.*;
import healthsystem.util.FileHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


public class HealthSystem {

    // store all data in memory
    private HashMap<String, Patient>     patients;
    private HashMap<String, Doctor>      doctors;
    private ArrayList<Appointment>       appointments;
    private ArrayList<HealthRecord>      healthRecords;

    // Counters for ID generation
    private int patientCounter = 1;
    private int doctorCounter  = 1;
    private int appointmentCounter = 1;
    private int recordCounter  = 1;

    // Singleton-style constructor — load data on startup
    public HealthSystem() {
        patients     = new HashMap<>();
        doctors      = new HashMap<>();
        appointments = new ArrayList<>();
        healthRecords = new ArrayList<>();
        loadAllData();
        syncCounters();
    }

    //  PATIENTS

    public Patient registerPatient(String name, int age, String phone, String email,
                                   String bloodType, String medicalHistory) {
        String id = "P" + String.format("%03d", patientCounter++);
        Patient patient = new Patient(id, name, age, phone, email, bloodType, medicalHistory);
        patients.put(id, patient);
        saveAllData();
        return patient;
    }

    public boolean removePatient(String id) {
        if (patients.containsKey(id)) {
            patients.remove(id);
            saveAllData();
            return true;
        }
        return false;
    }

    public Patient getPatient(String id) { return patients.get(id); }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients.values());
    }

    public List<Patient> searchPatients(String query) {
        String q = query.toLowerCase();
        return patients.values().stream()
            .filter(p -> p.getName().toLowerCase().contains(q) ||
                         p.getId().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    //  DOCTORS

    public Doctor registerDoctor(String name, int age, String phone, String email,
                                 String specialization, String licenseNumber) {
        String id = "D" + String.format("%03d", doctorCounter++);
        Doctor doctor = new Doctor(id, name, age, phone, email, specialization, licenseNumber);
        doctors.put(id, doctor);
        saveAllData();
        return doctor;
    }

    public boolean removeDoctor(String id) {
        if (doctors.containsKey(id)) {
            doctors.remove(id);
            saveAllData();
            return true;
        }
        return false;
    }

    public Doctor getDoctor(String id) { return doctors.get(id); }

    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors.values());
    }

    public List<Doctor> getAvailableDoctors() {
        return doctors.values().stream()
            .filter(Doctor::isAvailable)
            .collect(Collectors.toList());
    }

    //  APPOINTMENTS

    public Appointment bookAppointment(String patientId, String doctorId,
                                       LocalDate date, LocalTime time, String reason) {
        // Validate both exist
        if (!patients.containsKey(patientId) || !doctors.containsKey(doctorId)) return null;
        //Check if doctor is available
        Doctor doctor = doctors.get(doctorId);
        if (!doctor.isAvailable()) {
            return null;
        }

        //Check for double booking
        for (Appointment a : appointments) {
            if (a.getDoctorId().equals(doctorId) &&
                    a.getDate().equals(date) &&
                    a.getTime().equals(time) &&
                    a.getStatus() == Appointment.Status.SCHEDULED) {
                return null; // already booked
            }
        }



        String id = "A" + String.format("%03d", appointmentCounter++);
        Appointment appointment = new Appointment(id, patientId, doctorId, date, time, reason);
        appointments.add(appointment);
        saveAllData();
        return appointment;
    }

    public boolean cancelAppointment(String appointmentId) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(appointmentId)) {
                a.setStatus(Appointment.Status.CANCELLED);
                saveAllData();
                return true;
            }
        }
        return false;
    }

    public boolean completeAppointment(String appointmentId) {
        for (Appointment a : appointments) {
            if (a.getAppointmentId().equals(appointmentId)) {
                a.setStatus(Appointment.Status.COMPLETED);
                saveAllData();
                return true;
            }
        }
        return false;
    }

    public List<Appointment> getAllAppointments() { return new ArrayList<>(appointments); }

    public List<Appointment> getAppointmentsForPatient(String patientId) {
        return appointments.stream()
            .filter(a -> a.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }

    public List<Appointment> getUpcomingAppointments() {
        return appointments.stream()
            .filter(a -> a.isUpcoming() && a.getStatus() == Appointment.Status.SCHEDULED)
            .collect(Collectors.toList());
    }

    // HEALTH RECORDS

    public HealthRecord addHealthRecord(String patientId, double weight, double height,
                                        int systolicBP, int diastolicBP, double temperature,
                                        int heartRate, String notes) {
        if (!patients.containsKey(patientId)) return null;

        String id = "R" + String.format("%03d", recordCounter++);
        HealthRecord record = new HealthRecord(id, patientId, LocalDate.now(),
            weight, height, systolicBP, diastolicBP, temperature, heartRate, notes);
        healthRecords.add(record);
        saveAllData();
        return record;
    }

    public List<HealthRecord> getRecordsForPatient(String patientId) {
        return healthRecords.stream()
            .filter(r -> r.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }

    public List<HealthRecord> getAllHealthRecords() { return new ArrayList<>(healthRecords); }

    // Get all records with active alerts (polymorphism via Alertable interface)
    public List<HealthRecord> getAlertRecords() {
        return healthRecords.stream()
            .filter(r -> !r.isHealthy())
            .collect(Collectors.toList());
    }

    //  STATISTICS

    public int getTotalPatients()     { return patients.size(); }
    public int getTotalDoctors()      { return doctors.size(); }
    public int getTotalAppointments() { return appointments.size(); }
    public int getTotalAlerts()       { return getAlertRecords().size(); }

    //PERSISTENCE

    public void saveAllData() {
        FileHandler.savePatients(new ArrayList<>(patients.values()));
        FileHandler.saveDoctors(new ArrayList<>(doctors.values()));
        FileHandler.saveAppointments(appointments);
        FileHandler.saveHealthRecords(healthRecords);
    }

    private void loadAllData() {
        for (Patient p : FileHandler.loadPatients())     patients.put(p.getId(), p);
        for (Doctor d  : FileHandler.loadDoctors())      doctors.put(d.getId(), d);
        appointments  = new ArrayList<>(FileHandler.loadAppointments());
        healthRecords = new ArrayList<>(FileHandler.loadHealthRecords());
    }

    private void syncCounters() {
        // Set counters based on loaded data to avoid ID collisions
        for (String id : patients.keySet()) {
            try { patientCounter = Math.max(patientCounter, Integer.parseInt(id.substring(1)) + 1); }
            catch (NumberFormatException ignored) {}
        }
        for (String id : doctors.keySet()) {
            try { doctorCounter = Math.max(doctorCounter, Integer.parseInt(id.substring(1)) + 1); }
            catch (NumberFormatException ignored) {}
        }
        for (Appointment a : appointments) {
            try { appointmentCounter = Math.max(appointmentCounter, Integer.parseInt(a.getAppointmentId().substring(1)) + 1); }
            catch (NumberFormatException ignored) {}
        }
        for (HealthRecord r : healthRecords) {
            try { recordCounter = Math.max(recordCounter, Integer.parseInt(r.getRecordId().substring(1)) + 1); }
            catch (NumberFormatException ignored) {}
        }
    }
}
