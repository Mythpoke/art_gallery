package artGallery;

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

public class AdminController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField roleField;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "SELECT * FROM Users";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                ));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
        }

        userTable.setItems(userList);
    }

    @FXML
    private void addUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleField.getText();

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully.");
            loadUsers();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user: " + e.getMessage());
        }
    }

    @FXML
    private void deleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to delete.");
            return;
        }

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = "DELETE FROM Users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedUser.getId());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
            loadUsers();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
