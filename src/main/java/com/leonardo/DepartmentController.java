package com.leonardo;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.leonardo.db.DbException;
import com.leonardo.entities.Department;
import com.leonardo.listeners.DataChangerListener;
import com.leonardo.services.DepartmentService;
import com.leonardo.util.Alerts;
import com.leonardo.util.Utils;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DepartmentController implements Initializable, DataChangerListener {

    private DepartmentService service;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private TableColumn<Department, Department> tableColumnEdit;

    @FXML
    private TableColumn<Department, Department> tableColumnRemove;

    @FXML
    private Button btNew;

    private ObservableList<Department> obsList;

    public void setDepartmentService(DepartmentService service) {
        this.service = service;
    }

    @FXML
    public void onBtNewAction(ActionEvent actionEvent) {
        Stage parentStage = Utils.currentStage(actionEvent);
        Department department = new Department();
        createDialogForm(department, "DepartmentForm", parentStage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { 
        initializeNodes();      
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        Stage stage = (Stage) App.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    } 
    
    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        List<Department> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewDepartment.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Department department, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName + ".fxml"));
            Pane pane = loader.load(); 
            
            DepartmentFormController departmentFormController = loader.getController();
            departmentFormController.setDepartment(department);
            departmentFormController.setDepartmentService(new DepartmentService());
            departmentFormController.subscribeDataChangeListener(this);
            departmentFormController.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter department data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            Alerts.showWarningPopup("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "DepartmentForm", Utils.currentStage(event)));
			}
		});
	}
    private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

    private void removeEntity(Department department) {
        Optional<ButtonType> result =  Alerts.showConfirmation("Confirmation", "Confirm deletion");
        if (result.get() == ButtonType.OK) {
            if (service == null) {
                throw new IllegalStateException("Service was null");
            }
            try {
                service.remove(department);
                updateTableView();
            } catch (DbException e) {
                Alerts.showWarningPopup("Error removing object", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }
}
