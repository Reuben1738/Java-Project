package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Course;
import model.Module;
import model.StudentProfile;

import java.util.ArrayList;
import java.util.List;

public class ReserveModulesPane extends VBox {

    private ListView<Module> unselectedListView, reservedListView;
    private Button addBtn, removeBtn, confirmBtn;
    private ObservableList<Module> unselectedModules;
    private ObservableList<Module> reservedModules;

    public ReserveModulesPane() {
        unselectedModules = FXCollections.observableArrayList();
        reservedModules = FXCollections.observableArrayList();
        
        unselectedListView = new ListView<>();
        reservedListView = new ListView<>();
        addBtn = new Button("Add");
        removeBtn = new Button("Remove");
        confirmBtn = new Button("Confirm");
        
        unselectedListView.setItems(unselectedModules);
        reservedListView.setItems(reservedModules);
        unselectedListView.setPrefHeight(350);
        reservedListView.setPrefHeight(350);

        VBox leftPanel = new VBox(5, new Label("Unselected Block 3/4 modules"), unselectedListView);
        VBox rightPanel = new VBox(5, new Label("Reserved Block 3/4 modules"), reservedListView);
        
        VBox buttonPanel = new VBox(10, addBtn, removeBtn);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(20, 0, 0, 0));
        
        HBox modulesPanel = new HBox(20, leftPanel, buttonPanel, rightPanel);
        modulesPanel.setPadding(new Insets(10));
        modulesPanel.setAlignment(Pos.CENTER);
        
        VBox centerPanel = new VBox(10, 
            new Label("Reserve one optional module"), 
            modulesPanel, 
            confirmBtn);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(20));
        
        this.getChildren().add(centerPanel);
    }

    public void populateRemainingModules(StudentProfile profile) {
        unselectedModules.clear();
        reservedModules.clear();
        
        // Get all optional modules from the course that weren't selected
        if (profile.getStudentCourse() != null) {
            for (Module module : profile.getStudentCourse().getAllModulesOnCourse()) {
                if (!module.isMandatory() && 
                    !profile.getAllSelectedModules().contains(module) &&
                    module.getRunPlan() == model.Block.BLOCK_3_4) {
                    unselectedModules.add(module);
                }
            }
        }
    }

    public void addReservedModule(Module module) {
        if (unselectedModules.remove(module)) {
            reservedModules.add(module);
        }
    }

    public void removeReservedModule(Module module) {
        if (reservedModules.remove(module)) {
            unselectedModules.add(module);
        }
    }

    public Module getSelectedUnselectedModule() {
        return unselectedListView.getSelectionModel().getSelectedItem();
    }

    public Module getSelectedReservedModule() {
        return reservedListView.getSelectionModel().getSelectedItem();
    }

    public List<Module> getReservedModules() {
        return new ArrayList<>(reservedModules);
    }

    public int getReservedModulesCount() {
        return reservedModules.size();
    }

    public void addAddHandler(EventHandler<ActionEvent> handler) {
        addBtn.setOnAction(handler);
    }

    public void addRemoveHandler(EventHandler<ActionEvent> handler) {
        removeBtn.setOnAction(handler);
    }

    public void addConfirmHandler(EventHandler<ActionEvent> handler) {
        confirmBtn.setOnAction(handler);
    }

	public void populateModules(Course selectedCourse, StudentProfile model) {
		// TODO Auto-generated method stub
		
	}

	public int getCurrentCredits() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Module[] getSelectedOptionalModules() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSelectedModule(Module selected) {
		// TODO Auto-generated method stub
		
	}
}