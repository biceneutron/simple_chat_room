package ch.makery.address.view;

import ch.makery.address.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ServerIPPortController {
	
	private Stage dialogStage;
	
	@FXML
	TextField ipTextField;
	@FXML
	TextField portTextField;
	
	// Reference to the main application
    private MainApp mainApp;

    
    @FXML
    private void initialize() {
    		portTextField.setText("5000");
    }
    
    
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    
    @FXML
    private void handleIPOk() {
    		this.mainApp.setUpNetworking(ipTextField.getText(), portTextField.getText());
    		dialogStage.close();
    }
    
    @FXML
    public void handleIPCancel() {
    		dialogStage.close();
    } 
    
}
