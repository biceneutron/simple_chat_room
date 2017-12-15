package ch.makery.address.view;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import ch.makery.address.MainApp;
//import ch.makery.address.controller;
import ch.makery.address.model.Person;
import ch.makery.address.util.DateUtil;

public class PersonOverviewController {
    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> firstNameColumn;
    @FXML
    private TableColumn<Person, String> lastNameColumn;

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label birthdayLabel;

    private Socket socket;
    private PrintWriter writer;
	private BufferedReader reader;
    @FXML
    public TextArea incomingTextArea;
    @FXML
    private TextField outgoingTextArea;
    @FXML
    private Button sendButton;
    // Reference to the main application.
    private MainApp mainApp;	

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PersonOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    		// set up networking
    		//setUpNetworking();
    		
        // Initialize the person table with the two columns.
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        
        // Clear person details.
        showPersonDetails(null);
        
        // Listen for selection changes and show the person details when changed.
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));
        
        // Bind ENTER on outgoingTextArea
        outgoingTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                		handleSend();
                }
            }
        });
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        personTable.setItems(mainApp.getPersonData());
    }
    
    
    public void setStream(PrintWriter w, BufferedReader r) {
    		this.writer = w;
    		this.reader = r;
    }
    
    /*
    public void setUpNetworking() {
		try {
			String serverIP = "192.168.0.8";
			socket = new Socket(InetAddress.getByName(serverIP), 5000);
			System.out.println(socket.toString());
			writer = new PrintWriter(socket.getOutputStream());
			InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(isReader);
			System.out.println("Networking established.");
			incomingTextArea.appendText("Networking established.\n");
			incomingTextArea.appendText("**********************************\n");
			
			Thread thread = new Thread(new ReadBroadCast());
			thread.start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}*/
    
       
    /**
     * Fills all text fields to show details about the person.
     * If the specified person is null, all text fields are cleared.
     * 
     * @param person the person or null
     */
    private void showPersonDetails(Person person) {
        if (person != null) {
            // Fill the labels with info from the person object.
            firstNameLabel.setText(person.getFirstName());
            lastNameLabel.setText(person.getLastName());
            streetLabel.setText(person.getStreet());
            postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
            cityLabel.setText(person.getCity());

            // TODO: We need a way to convert the birthday into a String! 
            // birthdayLabel.setText(...);
            birthdayLabel.setText(DateUtil.format(person.getBirthday()));
        } else {
            // Person is null, remove all the text.
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            streetLabel.setText("");
            postalCodeLabel.setText("");
            cityLabel.setText("");
            birthdayLabel.setText("");
        }
    }

    
    @FXML
    private void handleSend() {
    		try {
    			writer.println(outgoingTextArea.getText());
    			writer.flush();
    		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		outgoingTextArea.setText("");
		outgoingTextArea.requestFocus();
	}
    
    
    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDeletePerson() {
        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            personTable.getItems().remove(selectedIndex);
        }
        else {
            // Nothing selected.
        		Alert alert = new Alert(AlertType.WARNING);
        		alert.setTitle("Warning Dialog");
        		alert.setHeaderText("Look, a Warning Dialog");
        		alert.setContentText("Careful with the next step!");

        		alert.showAndWait();
        }
    }
    
    
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new person.
     */
    @FXML
    private void handleNewPerson() {
        Person tempPerson = new Person();
        boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
        if (okClicked) {
            mainApp.getPersonData().add(tempPerson);
        }
    }

    
    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     */
    @FXML
    private void handleEditPerson() {
        Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
            if (okClicked) {
                showPersonDetails(selectedPerson);
            }

        } else {
        		// Nothing selected.
    			Alert alert = new Alert(AlertType.WARNING);
    			alert.setTitle("Warning Dialog");
    			alert.setHeaderText("Look, a Warning Dialog");
    			alert.setContentText("Careful with the next step!");

    			alert.showAndWait();
        }
    }

    
    public void createListenThread() {
    		Thread thread = new Thread(new ReadBroadCast());
		thread.start();
    }
    
    
    public class ReadBroadCast extends Thread {
		public void run() {
			try {
				String msg;
				while((msg = reader.readLine()) != null) {
					System.out.println("Read: " + msg);
					incomingTextArea.appendText(msg + "\n");
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
