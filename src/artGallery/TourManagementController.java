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

public class TourManagementController {

    @FXML
    private TableView<Tour> tourTable;

    @FXML
    private TableColumn<Tour, Integer> idColumn;

    @FXML
    private TableColumn<Tour, String> guideColumn;

    @FXML
    private TableColumn<Tour, String> dateColumn;

    @FXML
    private TableColumn<Tour, Integer> groupSizeColumn;

    @FXML
    private TextField guideField;

    @FXML
    private DatePicker tourDatePicker;

    @FXML
    private TextField groupSizeField;

    private ObservableList<Tour> tourList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("tourDate"));
        groupSizeColumn.setCellValueFactory(new PropertyValueFactory<>("groupSize"));

        loadTours();
    }

    private void loadTours() {
        tourList.clear();
        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "SELECT * FROM Tours";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tourList.add(new Tour(
                        rs.getInt("id"),
                        rs.getString("guide_name"),
                        rs.getString("tour_date"),
                        rs.getInt("group_size")
                ));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load tours: " + e.getMessage());
        }

        tourTable.setItems(tourList);
    }

    @FXML
    private void addTour() {
        String guideName = guideField.getText();
        String tourDate = tourDatePicker.getValue() != null ? tourDatePicker.getValue().toString() : "";
        String groupSize = groupSizeField.getText();

        if (guideName.isEmpty() || tourDate.isEmpty() || groupSize.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "INSERT INTO Tours (guide_name, tour_date, group_size) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, guideName);
            stmt.setString(2, tourDate);
            stmt.setInt(3, Integer.parseInt(groupSize));
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Tour added successfully.");
            loadTours();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add tour: " + e.getMessage());
        }
    }

    @FXML
    private void updateTour() {
        Tour selectedTour = tourTable.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a tour to update.");
            return;
        }

        String guideName = guideField.getText();
        String tourDate = tourDatePicker.getValue() != null ? tourDatePicker.getValue().toString() : "";
        String groupSize = groupSizeField.getText();

        if (guideName.isEmpty() || tourDate.isEmpty() || groupSize.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "UPDATE Tours SET guide_name = ?, tour_date = ?, group_size = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, guideName);
            stmt.setString(2, tourDate);
            stmt.setInt(3, Integer.parseInt(groupSize));
            stmt.setInt(4, selectedTour.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Tour updated successfully.");
            loadTours();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update tour: " + e.getMessage());
        }
    }

    @FXML
    private void deleteTour() {
        Tour selectedTour = tourTable.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a tour to delete.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "DELETE FROM Tours WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedTour.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Tour deleted successfully.");
            loadTours();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete tour: " + e.getMessage());
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
        Stage stage = (Stage) tourTable.getScene().getWindow();
        stage.close();
    }
}
