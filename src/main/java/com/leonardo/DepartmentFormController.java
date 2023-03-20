package com.leonardo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.leonardo.db.DbException;
import com.leonardo.entities.Department;
import com.leonardo.exceptions.ValidationException;
import com.leonardo.listeners.DataChangerListener;
import com.leonardo.services.DepartmentService;
import com.leonardo.util.Alerts;
import com.leonardo.util.Constraints;
import com.leonardo.util.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class DepartmentFormController implements Initializable {

    private Department department;

    private DepartmentService departmentService;

    private List<DataChangerListener> dataChangerListeners = new ArrayList<>();

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private Label labelError;

    @FXML
    private Button btSave;

    @FXML
    private Button btCancel;

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void subscribeDataChangeListener(DataChangerListener dataChangerListener) {
        dataChangerListeners.add(dataChangerListener);
    }

    @FXML
    public void onBtSaveAction(ActionEvent actionEvent) {
        if (department == null) {
            throw new DbException("Department was null");
        } 
        if (departmentService == null) {
            throw new DbException("Service was null");
        }
        try {
            department = getFormData();
            departmentService.saveOrUpdate(department);
            notifyDataChangeListeners();
            Utils.currentStage(actionEvent).close();
        } catch (DbException e) {
            Alerts.showWarningPopup("Error saving object", null, e.getMessage(), AlertType.ERROR);
        } catch (ValidationException e) {
            setErrorMessages(e.getErrors());
        }
    }

    private void notifyDataChangeListeners() {
        for (DataChangerListener dataChangerListener : dataChangerListeners) {
            dataChangerListener.onDataChanged();
        }
    }

    private Department getFormData() {
        Department department = new Department();
        ValidationException exception = new ValidationException("Validation error");
        department.setId(Utils.tryParseToInt(txtId.getText()));
        if (txtName.getText() == null || txtName.getText().trim().equals("")) {
            exception.addError("name", "Field can't be empty");
        }
        department.setName(txtName.getText());
        if (exception.getErrors().size() > 0) {
            throw exception;
        }
        return department;
    }

    @FXML
    public void onBtCancelAction(ActionEvent actionEvent) {
        Utils.currentStage(actionEvent).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }  

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 30);
    }

    public void updateFormData() {
        if (department == null) {
            throw new IllegalStateException("Department was null");
        }
        txtId.setText(String.valueOf(department.getId()));
        txtName.setText(department.getName());
    }

    private void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();  
        if (fields.contains("name")) {
            labelError.setText(errors.get("name"));
        }  
    }
}
