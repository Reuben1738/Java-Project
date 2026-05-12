package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Block;
import model.Course;
import model.Module;
import model.Name;
import model.StudentProfile;
import view.ModuleSelectorRootPane;
import view.CreateStudentProfilePane;
import view.ModuleSelectorMenuBar;
import view.ReserveModulesPane;
import view.OverviewSelectionPane;
import view.SelectModulesPane; 

public class ModuleSelectorController {

	//fields to be used throughout class
	private ModuleSelectorRootPane view;
	private StudentProfile model;
	
	private CreateStudentProfilePane cspp;
	private ModuleSelectorMenuBar msmb;
	private SelectModulesPane smp;
	private ReserveModulesPane rmp;
	private OverviewSelectionPane osp;
	
	 public ModuleSelectorController(ModuleSelectorRootPane view, StudentProfile model) {
		//initialise view and model fields
		this.view = view;
		this.model = model;
		
		//initialise view subcontainer fields
		cspp = view.getCreateStudentProfilePane();
		msmb = view.getModuleSelectorMenuBar();
        smp = view.getSelectModulesPane();
        rmp = view.getReserveModulesPane();
        osp = view.getOverviewSelectionPane();

		//add courses to combobox in create student profile pane using the createModulesAndCourses helper method below
		cspp.addCourseDataToComboBox(createModulesAndCourses());

		//attach event handlers to view using private helper method
		this.attachEventHandlers();
	}

	//helper method - used to attach event handlers
	private void attachEventHandlers() {
		//attach an event handler to the create student profile pane
		cspp.addCreateStudentProfileHandler(new CreateStudentProfileHandler());
		
		//attach event handlers to menu bar
				msmb.addAboutHandler(new AboutHandler());
				msmb.addExitHandler(e -> System.exit(0));
        
				//attach event handlers to select modules pane
				smp.addAddHandler(new AddModuleHandler());
				smp.addRemoveHandler(new RemoveModuleHandler());
				smp.addResetHandler(new ResetHandler());
				smp.addSubmitHandler(new SubmitHandler());

				//attach event handlers to reserve modules pane
				rmp.addAddHandler(new AddReserveHandler());
				rmp.addRemoveHandler(new RemoveReserveHandler());
				rmp.addConfirmHandler(new ConfirmHandler());
		
		//attach an event handler to the menu bar that closes the application
		msmb.addExitHandler(e -> System.exit(0));
        msmb.addAboutHandler(new AboutHandler());
        
     // Event handlers for Save Overview, Save and Load Profile
        osp.addSaveOverviewHandler(new SaveOverviewHandler());    
        msmb.addSaveHandler(new SaveProfileHandler());     
        msmb.addLoadHandler(new LoadProfileHandler());
	}
	
	//event handler (currently empty), which can be used for creating a profile
	private class CreateStudentProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			try {
				Course selectedCourse = cspp.getSelectedCourse();
				String pNumber = cspp.getStudentPnumber().trim();
				Name studentName = cspp.getStudentName();
				String email = cspp.getStudentEmail().trim();
				
				// Validation
				if (selectedCourse == null || pNumber.isEmpty() || 
					studentName.getFirstName().isEmpty() || email.isEmpty() || studentName.getFamilyName().isEmpty()) {
					showAlert(AlertType.ERROR, "Error", "Please fill in all required fields");
					return;
				}
				
				// Set student profile data
				model.setStudentCourse(selectedCourse);
				model.setStudentPnumber(pNumber);
				model.setStudentName(studentName);
				model.setStudentEmail(email);
				if (cspp.getStudentDate() != null) {
					model.setSubmissionDate(cspp.getStudentDate());
				}
				
				// Clear previous selections
				model.clearSelectedModules();
				model.clearReservedModules();
				
				// Add compulsory modules
				for (Module module : selectedCourse.getAllModulesOnCourse()) {
					if (module.isMandatory()) {
						model.addSelectedModule(module);
					}
				}
							 
				// Switch to select modules tab
				view.changeTab(1);
				smp.populateModules(selectedCourse, model);
				
			} catch (Exception ex) {
				showAlert(AlertType.ERROR, "Error", "Error = can not create profile: " + ex.getMessage());
			}
		}
	}
	
	// For AddModuleHandler
	private class AddModuleHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			Module selected = smp.getSelectedUnselectedModule();
			if (selected != null) {
			    int currentCredits = smp.getCurrentCredits();
			    if (currentCredits + selected.getModuleCredits() <= 120) {

			        smp.addSelectedModule(selected);
			        model.addSelectedModule(selected);

			    } else {
			        showAlert(AlertType.WARNING, "Credit Limit", "Cannot exceed 120 credits");
			    }
			}

		}
	}
	
	// For RemoveModuleHandler
	private class RemoveModuleHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			Module selected = smp.getSelectedSelectedModule();
			if (selected != null && !selected.isMandatory()) {
			    smp.removeSelectedModule(selected);
			    model.removeSelectedModule(selected);
			}

		}
	}
	
	//  For ResetModuleHandler
	private class ResetHandler implements EventHandler<ActionEvent> {
	    public void handle(ActionEvent e) {

	        model.clearSelectedModules();
	        model.clearReservedModules();

	        // Re-add mandatory modules
	        for (Module module : model.getStudentCourse().getAllModulesOnCourse()) {
	            if (module.isMandatory()) {
	                model.addSelectedModule(module);
	            }
	        }
	        smp.populateModules(model.getStudentCourse(), model);
	    }
	}

	// For submitModuleHandler
	private class SubmitHandler implements EventHandler<ActionEvent> {
	    public void handle(ActionEvent e) {
	        int totalCredits = smp.getCurrentCredits();

	        if (totalCredits != 120) {
	            showAlert(AlertType.WARNING, "Selection Invalid", "Select 120 credits. Current: " + totalCredits);
	            return;
	        }

	        // Clear previous selections to avoid duplicates
	        model.clearSelectedModules();

	        // Add block 1
	        for (Module m : smp.getBlock1Modules()) {
	            model.addSelectedModule(m);
	        }

	        // Add block 2
	        for (Module m : smp.getBlock2Modules()) {
	            model.addSelectedModule(m);
	        }

	        // Add selected block 3/4
	        for (Module m : smp.getSelectedBlock34Modules()) {
	            model.addSelectedModule(m);
	        }

	        rmp.populateRemainingModules(model);
	        view.changeTab(2);
	    }
	}

	// For AddReserveModuleHandler
	private class AddReserveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getReservedModulesCount() == 0) {
				Module selected = rmp.getSelectedUnselectedModule();
				if (selected != null) {
					rmp.addReservedModule(selected);
				}
			} else {
				showAlert(AlertType.WARNING, "Reserve Limit", "Can only reserve one module");
			}
		}
	}
	
	// For RemoveReserveModuleHandler
	private class RemoveReserveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			Module selected = rmp.getSelectedReservedModule();
			if (selected != null) {
				rmp.removeReservedModule(selected);
			}
		}
	}
	
	// For ConfirmModuleHandler
	private class ConfirmHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getReservedModulesCount() == 1) {
				Module reservedModule = rmp.getReservedModules().get(0);
				model.addReservedModule(reservedModule);
				
				// Display overview 
				osp.displayOverview(model);
				view.changeTab(3);
			} else {
				showAlert(AlertType.WARNING, "Reserve Required", "Please select one reserve module");
			}
		}
	}
	
	private class SaveOverviewHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Overview");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
			File file = fileChooser.showSaveDialog(new Stage());
			
			if (file != null) {
				try (PrintWriter writer = new PrintWriter(file)) {
					writer.println("STUDENT PROFILE OVERVIEW");
					writer.println("========================");
					writer.println("Name: " + model.getStudentName().getFullName());
					writer.println("P Number: " + model.getStudentPnumber());
					writer.println("Email: " + model.getStudentEmail());
					writer.println("Course: " + model.getStudentCourse().getCourseName());
					if (model.getSubmissionDate() != null) {
						writer.println("Date: " + model.getSubmissionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
					}
					writer.println();
					
					writer.println("SELECTED MODULES:");
					writer.println("-----------------");
					int totalCredits = 0;
					for (Module module : model.getAllSelectedModules()) {
						writer.println(module.getModuleCode() + " - " + module.getModuleName() + " (" + module.getModuleCredits() + " credits)");
						totalCredits += module.getModuleCredits();
					}
					writer.println("Total Credits: " + totalCredits);
					writer.println();
					
					writer.println("RESERVED MODULE:");
					writer.println("----------------");
					for (Module module : model.getAllReservedModules()) {
						writer.println(module.getModuleCode() + " - " + module.getModuleName() + " (" + module.getModuleCredits() + " credits)");
					}
					
					showAlert(AlertType.INFORMATION, "Success", "Overview saved successfully");
				} catch (IOException ex) {
					showAlert(AlertType.ERROR, "Error", "Failed to save overview: " + ex.getMessage());
				}
			}
		}
	}
	
	private class SaveProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Profile");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data Files", "*.dat"));
			File file = fileChooser.showSaveDialog(new Stage());
			
			if (file != null) {
				try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
					oos.writeObject(model);
					showAlert(AlertType.INFORMATION, "Success", "Profile saved successfully");
				} catch (IOException ex) {
					showAlert(AlertType.ERROR, "Error", "Failed to save profile: " + ex.getMessage());
				}
			}
		}
	}
	
	private class LoadProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Load Profile");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data Files", "*.dat"));
			File file = fileChooser.showOpenDialog(new Stage());
			
			if (file != null) {
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
					StudentProfile loadedProfile = (StudentProfile) ois.readObject();
					
					// Update current model
					model.setStudentCourse(loadedProfile.getStudentCourse());
					model.setStudentPnumber(loadedProfile.getStudentPnumber());
					model.setStudentName(loadedProfile.getStudentName());
					model.setStudentEmail(loadedProfile.getStudentEmail());
					model.setSubmissionDate(loadedProfile.getSubmissionDate());
					
					// Clear and reload modules
					model.clearSelectedModules();
					model.clearReservedModules();
					for (Module module : loadedProfile.getAllSelectedModules()) {
						model.addSelectedModule(module);
					}
					for (Module module : loadedProfile.getAllReservedModules()) {
						model.addReservedModule(module);
					}
					
					// Update UI
					cspp.populateWithLoadedProfile(model);
					smp.populateModules(model.getStudentCourse(), model);
					rmp.populateRemainingModules(model);
					osp.displayOverview(model);
					
					view.changeTab(3);
					showAlert(AlertType.INFORMATION, "Success", "Profile loaded successfully");
				} catch (IOException | ClassNotFoundException ex) {
					showAlert(AlertType.ERROR, "Error", "Failed to load profile: " + ex.getMessage());
				}
			}
		}
	}

	
	private class AboutHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText("Final Year Module Selection Tool");
			alert.setContentText("CTEC2710 OO Design and Development\nJavaFX Module Selector GUI\n\nThis application allows students to select their final year modules.");
			alert.showAndWait();
		}
	}
	
	private void showAlert(AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	//helper method - creates modules and course data and returns courses within an array
	private Course[] createModulesAndCourses() {
		Module ctec3701 = new Module("CTEC3701", "Software Development: Methods & Standards", 30, true, Block.BLOCK_1);

		Module ctec3702 = new Module("CTEC3702", "Big Data and Machine Learning", 30, true, Block.BLOCK_2);
		Module ctec3703 = new Module("CTEC3703", "Mobile App Development and Big Data", 30, true, Block.BLOCK_2);

		Module ctec3451 = new Module("CTEC3451", "Development Project", 30, true, Block.BLOCK_3_4);

		Module ctec3704 = new Module("CTEC3704", "Functional Programming", 30, false, Block.BLOCK_3_4);
		Module ctec3705 = new Module("CTEC3705", "Advanced Web Development", 30, false, Block.BLOCK_3_4);

		Module imat3711 = new Module("IMAT3711", "Privacy and Data Protection", 30, false, Block.BLOCK_3_4);
		Module imat3722 = new Module("IMAT3722", "Fuzzy Logic and Inference Systems", 30, false, Block.BLOCK_3_4);

		Module ctec3706 = new Module("CTEC3706", "Embedded Systems and IoT", 30, false, Block.BLOCK_3_4);


		Course compSci = new Course("Computer Science");
		compSci.addModule(ctec3701);
		compSci.addModule(ctec3702);
		compSci.addModule(ctec3451);
		compSci.addModule(ctec3704);
		compSci.addModule(ctec3705);
		compSci.addModule(imat3711);
		compSci.addModule(imat3722);

		Course softEng = new Course("Software Engineering");
		softEng.addModule(ctec3701);
		softEng.addModule(ctec3703);
		softEng.addModule(ctec3451);
		softEng.addModule(ctec3704);
		softEng.addModule(ctec3705);
		softEng.addModule(ctec3706);

		Course[] courses = new Course[2];
		courses[0] = compSci;
		courses[1] = softEng;

		return courses;
	}

}
