package healthsystem.ui;

import healthsystem.logic.HealthSystem;
import healthsystem.model.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainApp extends Application {

    private HealthSystem system;

    // Colours
    private static final String PRIMARY    = "#1a6b3c";
    private static final String SECONDARY  = "#2ecc71";
    private static final String ACCENT     = "#f39c12";
    private static final String DANGER     = "#e74c3c";
    private static final String LIGHT_BG   = "#f0f7f4";
    private static final String WHITE      = "#ffffff";
    private static final String DARK_TEXT  = "#1c2e27";

    @Override
    public void start(Stage primaryStage) {
        system = new HealthSystem();

        primaryStage.setTitle("Community Health Management System — SDG 3");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(720);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BG + ";");

        // Header
        root.setTop(buildHeader());

        // Tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: " + LIGHT_BG + ";");

        tabPane.getTabs().addAll(
            buildDashboardTab(),
            buildPatientsTab(),
            buildDoctorsTab(),
            buildAppointmentsTab(),
            buildHealthRecordsTab(),
            buildAlertsTab()
        );

        root.setCenter(tabPane);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // HEADER

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + PRIMARY + ";");
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);

        Label icon  = new Label("🏥");
        icon.setFont(Font.font(26));

        Label title = new Label("Community Health Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Label sdg = new Label("SDG 3: Good Health & Well-being");
        sdg.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        sdg.setTextFill(Color.web("#a8e6c3"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(icon, title, spacer, sdg);
        return header;
    }

    // DASHBOARD

    private Tab buildDashboardTab() {
        Tab tab = new Tab("📊 Dashboard");
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));

        Label heading = new Label("System Overview");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        heading.setTextFill(Color.web(DARK_TEXT));

        HBox cards = new HBox(15);
        cards.getChildren().addAll(
            buildStatCard("👤 Patients",     String.valueOf(system.getTotalPatients()),     PRIMARY),
            buildStatCard("\uD83D\uDC89 Doctors",      String.valueOf(system.getTotalDoctors()),      "#2980b9"),
            buildStatCard("📅 Appointments", String.valueOf(system.getTotalAppointments()), "#8e44ad"),
            buildStatCard("⚠ Alerts",        String.valueOf(system.getTotalAlerts()),       DANGER)
        );

        // Upcoming appointments list
        Label upcomingLabel = new Label("Upcoming Appointments");
        upcomingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        upcomingLabel.setTextFill(Color.web(DARK_TEXT));

        ListView<String> upcomingList = new ListView<>();
        List<Appointment> upcoming = system.getUpcomingAppointments();
        if (upcoming.isEmpty()) {
            upcomingList.getItems().add("No upcoming appointments.");
        } else {
            for (Appointment a : upcoming) {
                Patient p = system.getPatient(a.getPatientId());
                Doctor  d = system.getDoctor(a.getDoctorId());
                String pName = p != null ? p.getName() : a.getPatientId();
                String dName = d != null ? "Dr. " + d.getName() : a.getDoctorId();
                upcomingList.getItems().add(
                    a.getDate() + " " + a.getTime() + " — " + pName + " with " + dName + " | " + a.getReason()
                );
            }
        }
        upcomingList.setMaxHeight(180);

        // Refresh button
        Button refreshBtn = styledButton("🔄 Refresh Dashboard", PRIMARY);
        refreshBtn.setOnAction(e -> {
            // Rebuild the tab content by reloading
            rebuildDashboard(cards, upcomingList, upcomingLabel);
        });

        content.getChildren().addAll(heading, cards, upcomingLabel, upcomingList, refreshBtn);
        tab.setContent(new ScrollPane(content));
        return tab;
    }

    private void rebuildDashboard(HBox cards, ListView<String> upcomingList, Label upcomingLabel) {
        cards.getChildren().clear();
        cards.getChildren().addAll(
            buildStatCard("👤 Patients",     String.valueOf(system.getTotalPatients()),     PRIMARY),
            buildStatCard("\uD83D\uDC89 Doctors",      String.valueOf(system.getTotalDoctors()),      "#2980b9"),
            buildStatCard("📅 Appointments", String.valueOf(system.getTotalAppointments()), "#8e44ad"),
            buildStatCard("⚠ Alerts",        String.valueOf(system.getTotalAlerts()),       DANGER)
        );
        upcomingList.getItems().clear();
        List<Appointment> upcoming = system.getUpcomingAppointments();
        if (upcoming.isEmpty()) {
            upcomingList.getItems().add("No upcoming appointments.");
        } else {
            for (Appointment a : upcoming) {
                Patient p = system.getPatient(a.getPatientId());
                Doctor  d = system.getDoctor(a.getDoctorId());
                String pName = p != null ? p.getName() : a.getPatientId();
                String dName = d != null ? "Dr. " + d.getName() : a.getDoctorId();
                upcomingList.getItems().add(
                    a.getDate() + " " + a.getTime() + " — " + pName + " with " + dName + " | " + a.getReason()
                );
            }
        }
    }

    private VBox buildStatCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18, 30, 18, 30));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12;");

        Label valLabel = new Label(value);
        valLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        valLabel.setTextFill(Color.WHITE);

        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Arial", 13));
        nameLabel.setTextFill(Color.web("#dff5eb"));

        card.getChildren().addAll(valLabel, nameLabel);
        return card;
    }

    //  PATIENTS TAB

    private Tab buildPatientsTab() {
        Tab tab = new Tab("👤 Patients");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Form
        TitledPane formPane = new TitledPane("Register New Patient", buildPatientForm());
        formPane.setCollapsible(true);
        formPane.setStyle("-fx-background-color: " + WHITE + ";");

        // Table
        TableView<Patient> table = buildPatientTable();
        refreshPatientTable(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Search
        HBox searchBar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or ID...");
        searchField.setPrefWidth(280);
        Button searchBtn = styledButton("🔍 Search", "#2980b9");
        Button showAllBtn = styledButton("Show All", "#7f8c8d");
        searchBtn.setOnAction(e -> {
            List<Patient> results = system.searchPatients(searchField.getText().trim());
            table.setItems(FXCollections.observableArrayList(results));
        });
        showAllBtn.setOnAction(e -> refreshPatientTable(table));
        searchBar.getChildren().addAll(searchField, searchBtn, showAllBtn);

        // Remove button
        Button removeBtn = styledButton("🗑 Remove Selected", DANGER);
        removeBtn.setOnAction(e -> {
            Patient selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                system.removePatient(selected.getId());
                refreshPatientTable(table);
                showInfo("Patient " + selected.getName() + " removed.");
            }
        });

        // Wire form save
        Button saveBtn = (Button) ((GridPane) formPane.getContent()).lookup("#savePatientBtn");
        if (saveBtn != null) {
            saveBtn.setOnAction(e -> {
                // handled in buildPatientForm with table reference
            });
        }

        // Rebuild form with table reference
        GridPane patientForm = buildPatientFormWithSave(table);
        formPane.setContent(patientForm);

        content.getChildren().addAll(formPane, searchBar, table, removeBtn);
        tab.setContent(content);
        return tab;
    }

    private GridPane buildPatientForm() {
        return buildPatientFormWithSave(null);
    }

    private GridPane buildPatientFormWithSave(TableView<Patient> table) {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField nameF   = new TextField(); nameF.setPromptText("Full Name");
        TextField ageF    = new TextField(); ageF.setPromptText("Age");
        TextField phoneF  = new TextField(); phoneF.setPromptText("Phone");
        TextField emailF  = new TextField(); emailF.setPromptText("Email");
        ComboBox<String> bloodCB = new ComboBox<>();
        bloodCB.getItems().addAll("A+","A-","B+","B-","AB+","AB-","O+","O-");
        bloodCB.setPromptText("Blood Type");
        TextArea historyTA = new TextArea(); historyTA.setPromptText("Medical History"); historyTA.setPrefRowCount(2);

        grid.addRow(0, label("Name:"), nameF, label("Age:"), ageF);
        grid.addRow(1, label("Phone:"), phoneF, label("Email:"), emailF);
        grid.addRow(2, label("Blood Type:"), bloodCB, label("Medical History:"), historyTA);

        Button saveBtn = styledButton("✅ Register Patient", PRIMARY);
        saveBtn.setId("savePatientBtn");
        saveBtn.setOnAction(e -> {
            try {
                String name    = nameF.getText().trim();
                int age        = Integer.parseInt(ageF.getText().trim());
                String phone   = phoneF.getText().trim();
                String email   = emailF.getText().trim();
                String blood   = bloodCB.getValue() != null ? bloodCB.getValue() : "Unknown";
                String history = historyTA.getText().trim();
                if (name.isEmpty()) { showError("Name is required."); return; }
                Patient p = system.registerPatient(name, age, phone, email, blood, history);
                showInfo("Patient registered: " + p.getId() + " — " + p.getName());
                nameF.clear(); ageF.clear(); phoneF.clear(); emailF.clear();
                bloodCB.setValue(null); historyTA.clear();
                if (table != null) refreshPatientTable(table);
            } catch (NumberFormatException ex) {
                showError("Age must be a valid number.");
            }
        });

        grid.add(saveBtn, 0, 3, 4, 1);
        return grid;
    }

    private TableView<Patient> buildPatientTable() {
        TableView<Patient> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Patient, String> idCol   = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Patient, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Patient, String> bloodCol = new TableColumn<>("Blood Type");
        bloodCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
        TableColumn<Patient, String> histCol  = new TableColumn<>("Medical History");
        histCol.setCellValueFactory(new PropertyValueFactory<>("medicalHistory"));

        table.getColumns().addAll(idCol, nameCol, ageCol, phoneCol, bloodCol, histCol);
        table.setPlaceholder(new Label("No patients registered yet."));
        return table;
    }

    private void refreshPatientTable(TableView<Patient> table) {
        table.setItems(FXCollections.observableArrayList(system.getAllPatients()));
    }

    //  DOCTORS TAB

    private Tab buildDoctorsTab() {
        Tab tab = new Tab("\uD83D\uDC89 Doctors");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        TableView<Doctor> table = buildDoctorTable();
        refreshDoctorTable(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        TitledPane formPane = new TitledPane("Register New Doctor", new GridPane());
        GridPane doctorForm = buildDoctorFormWithSave(table);
        formPane.setContent(doctorForm);
        formPane.setCollapsible(true);

        Button removeBtn = styledButton("🗑 Remove Selected", DANGER);
        removeBtn.setOnAction(e -> {
            Doctor selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                system.removeDoctor(selected.getId());
                refreshDoctorTable(table);
                showInfo("Doctor " + selected.getName() + " removed.");
            }
        });

        content.getChildren().addAll(formPane, table, removeBtn);
        tab.setContent(content);
        return tab;
    }

    private GridPane buildDoctorFormWithSave(TableView<Doctor> table) {
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField nameF    = new TextField(); nameF.setPromptText("Full Name");
        TextField ageF     = new TextField(); ageF.setPromptText("Age");
        TextField phoneF   = new TextField(); phoneF.setPromptText("Phone");
        TextField emailF   = new TextField(); emailF.setPromptText("Email");
        TextField specF    = new TextField(); specF.setPromptText("e.g. Cardiology");
        TextField licenseF = new TextField(); licenseF.setPromptText("License Number");

        grid.addRow(0, label("Name:"), nameF, label("Age:"), ageF);
        grid.addRow(1, label("Phone:"), phoneF, label("Email:"), emailF);
        grid.addRow(2, label("Specialization:"), specF, label("License No:"), licenseF);

        Button saveBtn = styledButton("✅ Register Doctor", "#2980b9");
        saveBtn.setOnAction(e -> {
            try {
                String name    = nameF.getText().trim();
                int age        = Integer.parseInt(ageF.getText().trim());
                String phone   = phoneF.getText().trim();
                String email   = emailF.getText().trim();
                String spec    = specF.getText().trim();
                String license = licenseF.getText().trim();
                if (name.isEmpty() || spec.isEmpty()) { showError("Name and Specialization required."); return; }
                Doctor d = system.registerDoctor(name, age, phone, email, spec, license);
                showInfo("Doctor registered: " + d.getId() + " — Dr. " + d.getName());
                nameF.clear(); ageF.clear(); phoneF.clear(); emailF.clear(); specF.clear(); licenseF.clear();
                if (table != null) refreshDoctorTable(table);
            } catch (NumberFormatException ex) {
                showError("Age must be a valid number.");
            }
        });
        grid.add(saveBtn, 0, 3, 4, 1);
        return grid;
    }

    private TableView<Doctor> buildDoctorTable() {
        TableView<Doctor> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Doctor, String> idCol    = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Doctor, String> nameCol  = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Doctor, Integer> ageCol  = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        TableColumn<Doctor, String> specCol  = new TableColumn<>("Specialization");
        specCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        TableColumn<Doctor, String> licCol   = new TableColumn<>("License");
        licCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        TableColumn<Doctor, Boolean> availCol = new TableColumn<>("Available");

        availCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isAvailable())
        );

        availCol.setCellFactory(col -> new TableCell<Doctor, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    doctor.setAvailable(checkBox.isSelected());
                    system.saveAllData();
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, ageCol, specCol, licCol, availCol);
        table.setPlaceholder(new Label("No doctors registered yet."));
        return table;
    }

    private void refreshDoctorTable(TableView<Doctor> table) {
        table.setItems(FXCollections.observableArrayList(system.getAllDoctors()));
    }

    // APPOINTMENTS TAB

    private Tab buildAppointmentsTab() {
        Tab tab = new Tab("📅 Appointments");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        TableView<Appointment> table = buildAppointmentTable();
        refreshAppointmentTable(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Booking form
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(15));

        ComboBox<String> patientCB = new ComboBox<>();
        ComboBox<String> doctorCB  = new ComboBox<>();
        refreshPersonCombos(patientCB, doctorCB);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField timeField   = new TextField("09:00"); timeField.setPromptText("HH:MM");
        TextField reasonField = new TextField(); reasonField.setPromptText("Reason for visit");

        grid.addRow(0, label("Patient:"), patientCB, label("Doctor:"), doctorCB);
        grid.addRow(1, label("Date:"), datePicker, label("Time (HH:MM):"), timeField);
        grid.addRow(2, label("Reason:"), reasonField);

        Button bookBtn = styledButton("📅 Book Appointment", "#8e44ad");
        bookBtn.setOnAction(e -> {
            try {
                String patientSel = patientCB.getValue();
                String doctorSel  = doctorCB.getValue();
                if (patientSel == null || doctorSel == null) { showError("Select patient and doctor."); return; }
                String patientId = patientSel.split(" ")[0];
                String doctorId  = doctorSel.split(" ")[0];
                LocalDate date   = datePicker.getValue();
                LocalTime time   = LocalTime.parse(timeField.getText().trim());
                String reason    = reasonField.getText().trim();
                //Prevent past date
                if (date.isBefore(LocalDate.now())) {
                    showError("Cannot book appointments in the past.");
                    return;
                }

                //Prevent past time today
                if (date.equals(LocalDate.now()) && time.isBefore(LocalTime.now())) {
                    showError("This time has already passed.");
                    return;
                }
                if (reason.isEmpty()) { showError("Reason required."); return; }
                Appointment a = system.bookAppointment(patientId, doctorId, date, time, reason);
                if (a != null) {
                    showInfo("Appointment booked: " + a.getAppointmentId());
                    refreshAppointmentTable(table);
                } else {
                    showError("Doctor is already booked at that time or is not available.");
                }
            } catch (DateTimeParseException ex) {
                showError("Time format must be HH:MM (e.g. 14:30).");
            }
        });
        grid.add(bookBtn, 0, 3, 4, 1);

        TitledPane formPane = new TitledPane("Book New Appointment", grid);
        formPane.setCollapsible(true);

        // Action buttons
        HBox actionRow = new HBox(10);
        Button completeBtn = styledButton("✅ Mark Complete", PRIMARY);
        Button cancelBtn   = styledButton("❌ Cancel Appointment", DANGER);
        Button refreshBtn  = styledButton("🔄 Refresh", "#7f8c8d");

        completeBtn.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) { system.completeAppointment(sel.getAppointmentId()); refreshAppointmentTable(table); }
            table.refresh();
        });
        cancelBtn.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) { system.cancelAppointment(sel.getAppointmentId()); refreshAppointmentTable(table); }
            table.refresh();
        });
        refreshBtn.setOnAction(e -> {
            refreshPersonCombos(patientCB, doctorCB);
            refreshAppointmentTable(table);
            table.refresh();
        });

        actionRow.getChildren().addAll(completeBtn, cancelBtn, refreshBtn);
        content.getChildren().addAll(formPane, actionRow, table);
        tab.setContent(content);
        return tab;
    }

    private void refreshPersonCombos(ComboBox<String> patientCB, ComboBox<String> doctorCB) {
        patientCB.getItems().clear();
        for (Patient p : system.getAllPatients()) patientCB.getItems().add(p.getId() + " " + p.getName());
        doctorCB.getItems().clear();
        for (Doctor d : system.getAllDoctors()) doctorCB.getItems().add(d.getId() + " Dr. " + d.getName());
    }

    private TableView<Appointment> buildAppointmentTable() {
        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Appointment, String> idCol      = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> patCol     = new TableColumn<>("Patient Name");
        patCol.setCellValueFactory(a -> {
            Patient p = system.getPatient(a.getValue().getPatientId());
            String name = (p != null) ? p.getName() : a.getValue().getPatientId();
            return new SimpleStringProperty(name);
        });
        TableColumn<Appointment, String> docCol     = new TableColumn<>("Doctor Name");
        docCol.setCellValueFactory(a -> {
            Doctor d = system.getDoctor(a.getValue().getDoctorId());
            String name = (d != null) ? "Dr. " + d.getName() : a.getValue().getDoctorId();
            return new SimpleStringProperty(name);
        });
        TableColumn<Appointment, String> dateCol    = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Appointment, String> timeCol    = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        TableColumn<Appointment, String> reasonCol  = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        TableColumn<Appointment, String> statusCol  = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, patCol, docCol, dateCol, timeCol, reasonCol, statusCol);
        table.setPlaceholder(new Label("No appointments booked yet."));
        return table;
    }

    private void refreshAppointmentTable(TableView<Appointment> table) {
        table.setItems(FXCollections.observableArrayList(system.getAllAppointments()));
    }

    //HEALTH RECORDS TAB

    private Tab buildHealthRecordsTab() {
        Tab tab = new Tab("📋 Health Records");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        TableView<HealthRecord> table = buildHealthRecordTable();
        refreshRecordTable(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Form
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(15));

        ComboBox<String> patientCB = new ComboBox<>();
        for (Patient p : system.getAllPatients()) patientCB.getItems().add(p.getId() + " " + p.getName());
        patientCB.setPromptText("Select Patient");

        TextField weightF  = new TextField(); weightF.setPromptText("Weight (kg)");
        TextField heightF  = new TextField(); heightF.setPromptText("Height (cm)");
        TextField sysBPF   = new TextField(); sysBPF.setPromptText("Systolic BP");
        TextField diaBPF   = new TextField(); diaBPF.setPromptText("Diastolic BP");
        TextField tempF    = new TextField(); tempF.setPromptText("Temperature (°C)");
        TextField hrF      = new TextField(); hrF.setPromptText("Heart Rate (bpm)");
        TextField notesF   = new TextField(); notesF.setPromptText("Notes");

        grid.addRow(0, label("Patient:"), patientCB, label("Weight (kg):"), weightF);
        grid.addRow(1, label("Height (cm):"), heightF, label("Systolic BP:"), sysBPF);
        grid.addRow(2, label("Diastolic BP:"), diaBPF, label("Temperature °C:"), tempF);
        grid.addRow(3, label("Heart Rate:"), hrF, label("Notes:"), notesF);

        Button saveBtn = styledButton("💾 Save Health Record", PRIMARY);
        saveBtn.setOnAction(e -> {
            try {
                String sel = patientCB.getValue();
                if (sel == null) { showError("Select a patient."); return; }
                String patientId = sel.split(" ")[0];
                double weight    = Double.parseDouble(weightF.getText().trim());
                double height    = Double.parseDouble(heightF.getText().trim());
                int sysBP        = Integer.parseInt(sysBPF.getText().trim());
                int diaBP        = Integer.parseInt(diaBPF.getText().trim());
                double temp      = Double.parseDouble(tempF.getText().trim());
                int hr           = Integer.parseInt(hrF.getText().trim());
                String notes     = notesF.getText().trim();

                HealthRecord r = system.addHealthRecord(patientId, weight, height, sysBP, diaBP, temp, hr, notes);
                if (r != null) {
                    String alert = r.generateAlert();
                    if (alert != null) {
                        showWarning("Record saved!\n\n" + alert);
                    } else {
                        showInfo("Record saved. All readings are within normal range. ✅");
                    }
                    refreshRecordTable(table);
                    weightF.clear(); heightF.clear(); sysBPF.clear(); diaBPF.clear();
                    tempF.clear(); hrF.clear(); notesF.clear();
                }
            } catch (NumberFormatException ex) {
                showError("All numeric fields must be valid numbers.");
            }
        });
        grid.add(saveBtn, 0, 4, 4, 1);

        Button refreshComboBtn = styledButton("🔄 Refresh Patient List", "#7f8c8d");
        refreshComboBtn.setOnAction(e -> {
            patientCB.getItems().clear();
            for (Patient p : system.getAllPatients()) patientCB.getItems().add(p.getId() + " " + p.getName());
            refreshRecordTable(table);
        });

        TitledPane formPane = new TitledPane("Add Health Record", grid);
        formPane.setCollapsible(true);
        content.getChildren().addAll(formPane, refreshComboBtn, table);
        tab.setContent(content);
        return tab;
    }

    private TableView<HealthRecord> buildHealthRecordTable() {
        TableView<HealthRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HealthRecord, String> idCol     = new TableColumn<>("Record ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        TableColumn<HealthRecord, String> patCol    = new TableColumn<>("Patient ID");
        patCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<HealthRecord, String> dateCol   = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("recordDate"));
        TableColumn<HealthRecord, Double> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        TableColumn<HealthRecord, String> bmiCol    = new TableColumn<>("BMI");
        bmiCol.setCellValueFactory(r -> new SimpleStringProperty(
            r.getValue().getBMI() + " (" + r.getValue().getBMICategory() + ")"));
        TableColumn<HealthRecord, String> bpCol     = new TableColumn<>("BP");
        bpCol.setCellValueFactory(r -> new SimpleStringProperty(
            r.getValue().getSystolicBP() + "/" + r.getValue().getDiastolicBP()));
        TableColumn<HealthRecord, Double> tempCol   = new TableColumn<>("Temp °C");
        tempCol.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        TableColumn<HealthRecord, Integer> hrCol    = new TableColumn<>("Heart Rate");
        hrCol.setCellValueFactory(new PropertyValueFactory<>("heartRate"));
        TableColumn<HealthRecord, String> alertCol  = new TableColumn<>("Alert");
        alertCol.setCellValueFactory(r -> new SimpleStringProperty(
            r.getValue().isHealthy() ? "✅ Normal" : "⚠ Alert"));

        table.getColumns().addAll(idCol, patCol, dateCol, weightCol, bmiCol, bpCol, tempCol, hrCol, alertCol);
        table.setPlaceholder(new Label("No health records yet."));
        return table;
    }

    private void refreshRecordTable(TableView<HealthRecord> table) {
        table.setItems(FXCollections.observableArrayList(system.getAllHealthRecords()));
    }

    // ALERTS TAB

    private Tab buildAlertsTab() {
        Tab tab = new Tab("⚠ Health Alerts");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label heading = new Label("Active Health Alerts");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        heading.setTextFill(Color.web(DANGER));

        ListView<String> alertList = new ListView<>();
        refreshAlertList(alertList);
        VBox.setVgrow(alertList, Priority.ALWAYS);

        Button refreshBtn = styledButton("🔄 Refresh Alerts", DANGER);
        refreshBtn.setOnAction(e -> refreshAlertList(alertList));

        content.getChildren().addAll(heading, alertList, refreshBtn);
        tab.setContent(content);
        return tab;
    }

    private void refreshAlertList(ListView<String> alertList) {
        alertList.getItems().clear();
        List<HealthRecord> alertRecords = system.getAlertRecords();
        if (alertRecords.isEmpty()) {
            alertList.getItems().add("✅ No active health alerts. All patients are within normal ranges.");
        } else {
            for (HealthRecord r : alertRecords) {
                Patient p = system.getPatient(r.getPatientId());
                String name = p != null ? p.getName() : r.getPatientId();
                alertList.getItems().add("[" + r.getRecordDate() + "] " + name + " — " + r.generateAlert());
            }
        }
    }

    //  HELPERS

    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                     "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 7 14;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + color + ", -15%); -fx-text-fill: white; " +
                                                 "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 7 14;"));
        btn.setOnMouseExited(e  -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                                                 "-fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 7 14;"));
        return btn;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        return l;
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Health Alert Detected");
        alert.setHeaderText("⚠ Health Warning");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
