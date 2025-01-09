package artGallery;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SystemConfigController {

    @FXML
    private TextField openingHoursField;

    @FXML
    private TextField ticketPriceNormalField;

    @FXML
    private TextField ticketPriceDiscountedField;

    @FXML
    private TextField ticketPriceGroupField;

    @FXML
    private void initialize() {
        loadConfig();
    }

    private void loadConfig() {
        try (Connection conn = DatabaseHandler.getConnection()) {
            openingHoursField.setText(getConfigValue(conn, "opening_hours"));
            ticketPriceNormalField.setText(getConfigValue(conn, "ticket_price_normal"));
            ticketPriceDiscountedField.setText(getConfigValue(conn, "ticket_price_discounted"));
            ticketPriceGroupField.setText(getConfigValue(conn, "ticket_price_group"));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load system configuration: " + e.getMessage());
        }
    }

    private String getConfigValue(Connection conn, String key) throws Exception {
        String query = "SELECT config_value FROM SystemConfig WHERE config_key = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, key);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("config_value");
        }
        return "";
    }

    @FXML
    private void saveChanges() {
        try (Connection conn = DatabaseHandler.getConnection()) {
            updateConfigValue(conn, "opening_hours", openingHoursField.getText());
            updateConfigValue(conn, "ticket_price_normal", ticketPriceNormalField.getText());
            updateConfigValue(conn, "ticket_price_discounted", ticketPriceDiscountedField.getText());
            updateConfigValue(conn, "ticket_price_group", ticketPriceGroupField.getText());

            showAlert(Alert.AlertType.INFORMATION, "Success", "System configuration updated successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update system configuration: " + e.getMessage());
        }
    }

    private void updateConfigValue(Connection conn, String key, String value) throws Exception {
        String query = "UPDATE SystemConfig SET config_value = ? WHERE config_key = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, value);
        stmt.setString(2, key);
        stmt.executeUpdate();
    }

    @FXML
    private void backupDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Backup Location");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        Stage stage = (Stage) openingHoursField.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Runtime.getRuntime().exec("mysqldump -u root -p --databases art_gallery > " + file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Database backup created successfully.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create database backup: " + e.getMessage());
            }
        }
    }

    @FXML
    private void restoreDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Backup File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        Stage stage = (Stage) openingHoursField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Runtime.getRuntime().exec("mysql -u root -p art_gallery < " + file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Database restored successfully.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to restore database: " + e.getMessage());
            }
        }
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) openingHoursField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
