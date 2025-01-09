package artgallery;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ExhibitController {

    @FXML
    private TableView<Exhibit> exhibitTable;

    @FXML
    private TableColumn<Exhibit, Integer> idColumn;

    @FXML
    private TableColumn<Exhibit, String> nameColumn;

    @FXML
    private TableColumn<Exhibit, String> artistColumn;

    @FXML
    private TableColumn<Exhibit, String> statusColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField artistField;

    @FXML
    private TextField statusField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    private ObservableList<Exhibit> exhibitList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadExhibits();
    }

    private void loadExhibits() {
        exhibitList.clear();
        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "SELECT * FROM Exhibits";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exhibitList.add(new Exhibit(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("artist"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load exhibits: " + e.getMessage());
        }

        exhibitTable.setItems(exhibitList);
    }

    @FXML
    private void addExhibit() {
        String name = nameField.getText();
        String artist = artistField.getText();
        String status = statusField.getText();

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "INSERT INTO Exhibits (name, artist, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, artist);
            stmt.setString(3, status);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Exhibit added successfully.");
            loadExhibits();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add exhibit: " + e.getMessage());
        }
    }

    @FXML
    private void updateExhibit() {
        Exhibit selectedExhibit = exhibitTable.getSelectionModel().getSelectedItem();
        if (selectedExhibit == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an exhibit to update.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "UPDATE Exhibits SET name = ?, artist = ?, status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nameField.getText());
            stmt.setString(2, artistField.getText());
            stmt.setString(3, statusField.getText());
            stmt.setInt(4, selectedExhibit.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Exhibit updated successfully.");
            loadExhibits();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update exhibit: " + e.getMessage());
        }
    }

    @FXML
    private void deleteExhibit() {
        Exhibit selectedExhibit = exhibitTable.getSelectionModel().getSelectedItem();
        if (selectedExhibit == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an exhibit to delete.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "DELETE FROM Exhibits WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedExhibit.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Exhibit deleted successfully.");
            loadExhibits();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete exhibit: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
