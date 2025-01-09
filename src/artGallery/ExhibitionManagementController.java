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

public class ExhibitionManagementController {

    @FXML
    private TableView<Exhibition> exhibitionTable;

    @FXML
    private TableColumn<Exhibition, Integer> idColumn;

    @FXML
    private TableColumn<Exhibition, String> titleColumn;

    @FXML
    private TableColumn<Exhibition, String> startDateColumn;

    @FXML
    private TableColumn<Exhibition, String> startTimeColumn;

    @FXML
    private TableColumn<Exhibition, String> endDateColumn;

    @FXML
    private TableColumn<Exhibition, String> endTimeColumn;

    @FXML
    private TextField titleField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField startTimeField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField endTimeField;

    private ObservableList<Exhibition> exhibitionList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        loadExhibitions();
    }

    private void loadExhibitions() {
        exhibitionList.clear();
        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "SELECT * FROM Exhibitions";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exhibitionList.add(new Exhibition(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("start_date"),
                        rs.getString("start_time"),
                        rs.getString("end_date"),
                        rs.getString("end_time")
                ));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load exhibitions: " + e.getMessage());
        }

        exhibitionTable.setItems(exhibitionList);
    }

    @FXML
    private void addExhibition() {
        String title = titleField.getText();
        String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : "";
        String startTime = startTimeField.getText();
        String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : "";
        String endTime = endTimeField.getText();

        if (title.isEmpty() || startDate.isEmpty() || startTime.isEmpty() || endDate.isEmpty() || endTime.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "INSERT INTO Exhibitions (title, start_date, start_time, end_date, end_time) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, startDate);
            stmt.setString(3, startTime);
            stmt.setString(4, endDate);
            stmt.setString(5, endTime);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Exhibition added successfully.");
            loadExhibitions();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add exhibition: " + e.getMessage());
        }
    }

    @FXML
    private void updateExhibition() {
        Exhibition selectedExhibition = exhibitionTable.getSelectionModel().getSelectedItem();
        if (selectedExhibition == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an exhibition to update.");
            return;
        }

        String title = titleField.getText();
        String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : "";
        String startTime = startTimeField.getText();
        String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : "";
        String endTime = endTimeField.getText();

        if (title.isEmpty() || startDate.isEmpty() || startTime.isEmpty() || endDate.isEmpty() || endTime.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "UPDATE Exhibitions SET title = ?, start_date = ?, start_time = ?, end_date = ?, end_time = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, startDate);
            stmt.setString(3, startTime);
            stmt.setString(4, endDate);
            stmt.setString(5, endTime);
            stmt.setInt(6, selectedExhibition.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Exhibition updated successfully.");
            loadExhibitions();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update exhibition: " + e.getMessage());
        }
    }

    @FXML
    private void deleteExhibition() {
        Exhibition selectedExhibition = exhibitionTable.getSelectionModel().getSelectedItem();
        if (selectedExhibition == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an exhibition to delete.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "DELETE FROM Exhibitions WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedExhibition.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Exhibition deleted successfully.");
            loadExhibitions();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete exhibition: " + e.getMessage());
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
        Stage stage = (Stage) exhibitionTable.getScene().getWindow();
        stage.close();
    }
}
