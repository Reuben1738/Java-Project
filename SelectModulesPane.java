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

public class SelectModulesPane extends VBox {

    private ListView<Module> block1ListView, block2ListView, unselectedBlock34ListView, selectedBlock34ListView;
    private Button addBtn, removeBtn, resetBtn, submitBtn;
    private Label creditsLabel;
    private ObservableList<Module> unselectedBlock34Modules;
    private ObservableList<Module> selectedBlock34Modules;

    public SelectModulesPane() {
        // Initialise lists
        unselectedBlock34Modules = FXCollections.observableArrayList();
        selectedBlock34Modules = FXCollections.observableArrayList();
        
        // Create components
        block1ListView = new ListView<>();
        block2ListView = new ListView<>();
        unselectedBlock34ListView = new ListView<>();
        selectedBlock34ListView = new ListView<>();
        
        addBtn = new Button("Add");
        removeBtn = new Button("Remove");
        resetBtn = new Button("Reset");
        submitBtn = new Button("Submit");
        creditsLabel = new Label("Current credits: 0");

        // Setup list views
        block1ListView.setPrefHeight(250);
        block2ListView.setPrefHeight(250);
        unselectedBlock34ListView.setPrefHeight(250);
        selectedBlock34ListView.setPrefHeight(250);
        
        unselectedBlock34ListView.setItems(unselectedBlock34Modules);
        selectedBlock34ListView.setItems(selectedBlock34Modules);

        // Create layout
        HBox topContainer = new HBox(20);
        topContainer.setPadding(new Insets(10));

        // Left side: Block 1 + Block 2
        VBox leftBox = new VBox(15,
            new VBox(5, new Label("Selected Block 1 modules"), block1ListView),
            new VBox(5, new Label("Selected Block 2 modules"), block2ListView)
        );

        // Middle: Add/Remove buttons
        VBox buttonBox = new VBox(10, addBtn, removeBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(40, 0, 0, 0));

        // Right side: Block 3/4 unselected + selected
        VBox rightBox = new VBox(15,
            new VBox(5, new Label("Unselected Block 3/4 modules"), unselectedBlock34ListView),
            new VBox(5, new Label("Selected Block 3/4 modules"), selectedBlock34ListView)
        );

        // Put left, middle, right together
        topContainer.getChildren().addAll(leftBox, buttonBox, rightBox);

        // Bottom bar: credits + reset + submit
        HBox bottomBox = new HBox(20, creditsLabel, resetBtn, submitBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        // Add everything to root
        this.getChildren().addAll(topContainer, bottomBox);
    }

    public void populateModules(Course course, StudentProfile profile) {

        block1ListView.getItems().clear();
        block2ListView.getItems().clear();
        unselectedBlock34Modules.clear();
        selectedBlock34Modules.clear();

        if (course != null) {

            for (Module module : course.getAllModulesOnCourse()) {

                boolean alreadySelected = profile.getAllSelectedModules().contains(module);

                switch (module.getRunPlan()) {

                    case BLOCK_1:
                        block1ListView.getItems().add(module);
                        break;

                    case BLOCK_2:
                        block2ListView.getItems().add(module);
                        break;

                    case BLOCK_3_4:
                        if (module.isMandatory() || alreadySelected) {
                            selectedBlock34Modules.add(module);
                        } else {
                            unselectedBlock34Modules.add(module);
                        }
                        break;
                }
            }
        }

        updateCredits();
    }


    public void addSelectedModule(Module module) {
        if (unselectedBlock34Modules.remove(module)) {
            selectedBlock34Modules.add(module);
            updateCredits();
        }
    }

    public void removeSelectedModule(Module module) {
        if (selectedBlock34Modules.remove(module)) {
            unselectedBlock34Modules.add(module);
            updateCredits();
        }
    }
    
    public ObservableList<Module> getBlock1Modules() {
        return block1ListView.getItems();
    }
    public ObservableList<Module> getBlock2Modules() {
        return block2ListView.getItems();
    }
    public ObservableList<Module> getSelectedBlock34Modules() {
        return selectedBlock34Modules;
    }


    public Module getSelectedUnselectedModule() {
        return unselectedBlock34ListView.getSelectionModel().getSelectedItem();
    }

    public Module getSelectedSelectedModule() {
        return selectedBlock34ListView.getSelectionModel().getSelectedItem();
    }

    public ObservableList<Module> getSelectedOptionalModules() {
        ObservableList<Module> list = FXCollections.observableArrayList();
        for (Module m : selectedBlock34Modules) {
            if (!m.isMandatory()) {
                list.add(m);
            }
        }
        return list;
    }


    public int getCurrentCredits() {
        int total = 0;
        for (Module module : block1ListView.getItems()) {
            total += module.getModuleCredits();
        }
        for (Module module : block2ListView.getItems()) {
            total += module.getModuleCredits();
        }
        for (Module module : selectedBlock34Modules) {
            total += module.getModuleCredits();
        }
        return total;
    }

    private void updateCredits() {
        creditsLabel.setText("Current credits: " + getCurrentCredits());
    }

    public void addAddHandler(EventHandler<ActionEvent> handler) {
        addBtn.setOnAction(handler);
    }

    public void addRemoveHandler(EventHandler<ActionEvent> handler) {
        removeBtn.setOnAction(handler);
    }

    public void addResetHandler(EventHandler<ActionEvent> handler) {
        resetBtn.setOnAction(handler);
    }

    public void addSubmitHandler(EventHandler<ActionEvent> handler) {
        submitBtn.setOnAction(handler);
    }
}