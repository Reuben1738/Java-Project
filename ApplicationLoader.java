package main;

import controller.ModuleSelectorController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.StudentProfile;
import view.ModuleSelectorRootPane;

public class ApplicationLoader extends Application {

	private ModuleSelectorRootPane view;
	
	@Override
	public void init() {
		//create view and model and pass their references to the controller
		view = new ModuleSelectorRootPane();
		StudentProfile model = new StudentProfile();		
		new ModuleSelectorController(view, model);	
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		//sets min width and height for the stage window
		stage.setMinWidth(530); 
		stage.setMinHeight(500);
		
		stage.setTitle("Final Year Module Selection Tool");
		stage.setScene(new Scene(view));
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
