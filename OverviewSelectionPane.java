package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Module;
import model.StudentProfile;

public class OverviewSelectionPane extends VBox {

    private TextArea profileArea, selectedModulesArea, reservedModulesArea;
    private Button saveOverviewBtn;

    public OverviewSelectionPane() {
        profileArea = new TextArea();
        selectedModulesArea = new TextArea();
        reservedModulesArea = new TextArea();
        saveOverviewBtn = new Button("Save Overview");
        
        // Setup text areas
        profileArea.setEditable(false);
        selectedModulesArea.setEditable(false);
        reservedModulesArea.setEditable(false);
        profileArea.setPrefRowCount(7);
        selectedModulesArea.setPrefRowCount(8);
        reservedModulesArea.setPrefRowCount(4);

        VBox profileBox = new VBox(5, new Label("Profile"), profileArea);
        VBox selectedBox = new VBox(5, new Label("Selected Modules"), selectedModulesArea);
        VBox reservedBox = new VBox(5, new Label("Reserved Modules"), reservedModulesArea);
        
        HBox modulesBox = new HBox(20, selectedBox, reservedBox);
        modulesBox.setPadding(new Insets(10, 0, 0, 0));
        
        VBox mainBox = new VBox(10, profileBox, modulesBox, saveOverviewBtn);
        mainBox.setPadding(new Insets(20));
        
        this.getChildren().add(mainBox);
        
        // Set growth priorities for responsive resizing
        VBox.setVgrow(selectedModulesArea, Priority.ALWAYS);
        VBox.setVgrow(reservedModulesArea, Priority.ALWAYS);
        HBox.setHgrow(selectedModulesArea, Priority.ALWAYS);
        HBox.setHgrow(reservedModulesArea, Priority.ALWAYS);
        HBox.setHgrow(modulesBox, Priority.ALWAYS);
    }

    public void displayOverview(StudentProfile profile) {
        // Profile details
        StringBuilder profileText = new StringBuilder();
        if (profile.getStudentName() != null) {
            profileText.append("Name: ").append(profile.getStudentName().getFullName()).append("\n");
        }
        if (profile.getStudentPnumber() != null) {
            profileText.append("P Number: ").append(profile.getStudentPnumber()).append("\n");
        }
        if (profile.getStudentEmail() != null) {
            profileText.append("Email: ").append(profile.getStudentEmail()).append("\n");
        }
        if (profile.getStudentCourse() != null) {
            profileText.append("Course: ").append(profile.getStudentCourse().getCourseName()).append("\n");
        }
        if (profile.getSubmissionDate() != null) {
            profileText.append("Submission Date: ").append(profile.getSubmissionDate()).append("\n");
        }
        profileArea.setText(profileText.toString());

        // Selected modules
        StringBuilder selectedText = new StringBuilder();
        int totalCredits = 0;
        for (Module module : profile.getAllSelectedModules()) {
            selectedText.append(module.getModuleCode())
                       .append(" - ")
                       .append(module.getModuleName())
                       .append(" (")
                       .append(module.getModuleCredits())
                       .append(" credits)\n");
            totalCredits += module.getModuleCredits();
        }
        selectedText.append("\nTotal Credits: ").append(totalCredits);
        selectedModulesArea.setText(selectedText.toString());

        // Reserved modules
        StringBuilder reservedText = new StringBuilder();
        for (Module module : profile.getAllReservedModules()) {
            reservedText.append(module.getModuleCode())
                       .append(" - ")
                       .append(module.getModuleName())
                       .append(" (")
                       .append(module.getModuleCredits())
                       .append(" credits)\n");
        }
        reservedModulesArea.setText(reservedText.toString());
    }

    public void addSaveOverviewHandler(EventHandler<ActionEvent> handler) {
        saveOverviewBtn.setOnAction(handler);
    }
}