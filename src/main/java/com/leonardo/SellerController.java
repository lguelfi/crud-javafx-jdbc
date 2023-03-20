package com.leonardo;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.leonardo.db.DbException;
import com.leonardo.entities.Seller;
import com.leonardo.listeners.DataChangerListener;
import com.leonardo.services.DepartmentService;
import com.leonardo.services.SellerService;
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

public class SellerController implements Initializable, DataChangerListener {

    private SellerService service;

    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller, Integer> tableColumnId;

    @FXML
    private TableColumn<Seller, String> tableColumnName;

    @FXML
    private TableColumn<Seller, String> tableColumnEmail;

    @FXML
    private TableColumn<Seller, Date> tableColumnBirthDate;

    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;

    @FXML
    private TableColumn<Seller, Seller> tableColumnEdit;

    @FXML
    private TableColumn<Seller, Seller> tableColumnRemove;

    @FXML
    private Button btNew;

    private ObservableList<Seller> obsList;

    public void setSellerService(SellerService service) {
        this.service = service;
    }

    @FXML
    public void onBtNewAction(ActionEvent actionEvent) {
        Stage parentStage = Utils.currentStage(actionEvent);
        Seller seller = new Seller();
        createDialogForm(seller, "SellerForm", parentStage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { 
        initializeNodes();      
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
        
        Stage stage = (Stage) App.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    } 
    
    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        List<Seller> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewSeller.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Seller seller, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName + ".fxml"));
            Pane pane = loader.load(); 
            
            SellerFormController sellerFormController = loader.getController();
            sellerFormController.setSeller(seller);
            sellerFormController.setServices(new SellerService(), new DepartmentService());
            sellerFormController.loadAssociatedObjects();
            sellerFormController.subscribeDataChangeListener(this);
            sellerFormController.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter seller data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showWarningPopup("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "SellerForm", Utils.currentStage(event)));
			}
		});
	}
    private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
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

    private void removeEntity(Seller seller) {
        Optional<ButtonType> result =  Alerts.showConfirmation("Confirmation", "Confirm deletion");
        if (result.get() == ButtonType.OK) {
            if (service == null) {
                throw new IllegalStateException("Service was null");
            }
            try {
                service.remove(seller);
                updateTableView();
            } catch (DbException e) {
                Alerts.showWarningPopup("Error removing object", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }
}
