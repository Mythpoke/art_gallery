package artGallery;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StaffManagementController {

    @FXML
    private TableView<Staff> staffTable;

    @FXML
    private TableColumn<Staff, Integer> idColumn;

    @FXML
    private TableColumn<Staff, String> nameColumn;

    @FXML
    private TableColumn<Staff, String> positionColumn;

    @FXML
    private TableColumn<Staff, String> hireDateColumn;

    @FXML
    private TableColumn<Staff, Double> salaryColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField positionField;

    @FXML
    private DatePicker hireDatePicker;

    @FXML
    private TextField salaryField;

    private ObservableList<Staff> staffList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        loadStaff();
    }

    private void loadStaff() {
        staffList.clear();
        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "SELECT * FROM Staff";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                staffList.add(new Staff(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("position"),
                        rs.getString("hire_date"),
                        rs.getDouble("salary")
                ));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load staff: " + e.getMessage());
        }

        staffTable.setItems(staffList);
    }

    @FXML
    private void addStaff() {
        String name = nameField.getText();
        String position = positionField.getText();
        String hireDate = hireDatePicker.getValue() != null ? hireDatePicker.getValue().toString() : "";
        String salary = salaryField.getText();

        if (name.isEmpty() || position.isEmpty() || hireDate.isEmpty() || salary.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "INSERT INTO Staff (name, position, hire_date, salary) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, position);
            stmt.setString(3, hireDate);
            stmt.setDouble(4, Double.parseDouble(salary));
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Staff member added successfully.");
            loadStaff();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add staff: " + e.getMessage());
        }
    }

    @FXML
    private void updateStaff() {
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a staff member to update.");
            return;
        }

        String name = nameField.getText();
        String position = positionField.getText();
        String hireDate = hireDatePicker.getValue() != null ? hireDatePicker.getValue().toString() : "";
        String salary = salaryField.getText();

        if (name.isEmpty() || position.isEmpty() || hireDate.isEmpty() || salary.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "UPDATE Staff SET name = ?, position = ?, hire_date = ?, salary = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, position);
            stmt.setString(3, hireDate);
            stmt.setDouble(4, Double.parseDouble(salary));
            stmt.setInt(5, selectedStaff.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Staff member updated successfully.");
            loadStaff();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update staff: " + e.getMessage());
        }
    }

    @FXML
    private void deleteStaff() {
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a staff member to delete.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "DELETE FROM Staff WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedStaff.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Staff member deleted successfully.");
            loadStaff();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete staff: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) staffTable.getScene().getWindow();
        stage.close();
    }
}
