package healthsystem.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Appointment class represents a scheduled appointment between a Patient and Doctor.
 */
public class Appointment {

    // Enum for appointment status (decision-making logic)
    public enum Status {
        SCHEDULED, COMPLETED, CANCELLED
    }

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private Status status;

    // Constructor
    public Appointment(String appointmentId, String patientId, String doctorId,
                       LocalDate date, LocalTime time, String reason) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = Status.SCHEDULED;
    }

    // Business logic: check if appointment is today
    public boolean isToday() {
        return date.equals(LocalDate.now());
    }

    // Business logic: check if appointment is upcoming
    public boolean isUpcoming() {
        return date.isAfter(LocalDate.now()) || isToday();
    }

    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    // Serialize to CSV
    public String toCsvLine() {
        return String.join(",",
            appointmentId, patientId, doctorId,
            date.toString(), time.toString(),
            reason.replace(",", ";"), status.name());
    }

    // Deserialize from CSV
    public static Appointment fromCsvLine(String line) {
        String[] p = line.split(",", 7);
        Appointment a = new Appointment(p[0], p[1], p[2],
            LocalDate.parse(p[3]), LocalTime.parse(p[4]),
            p[5].replace(";", ","));
        a.setStatus(Status.valueOf(p[6]));
        return a;
    }

    @Override
    public String toString() {
        return "Appointment [" + appointmentId + "] on " + date + " at " + time +
               " | Reason: " + reason + " | Status: " + status;
    }
}
