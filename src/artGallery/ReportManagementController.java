package artGallery;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportManagementController {

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @FXML
    private TableView<Report> reportTable;

    @FXML
    private TableColumn<Report, String> column1;

    @FXML
    private TableColumn<Report, String> column2;

    @FXML
    private TableColumn<Report, String> column3;

    private ObservableList<Report> reportData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
                "Exhibits on Exhibition",
                "Ticket Sales",
                "Maintenance Schedule"
        ));

        column1.setCellValueFactory(new PropertyValueFactory<>("column1"));
        column2.setCellValueFactory(new PropertyValueFactory<>("column2"));
        column3.setCellValueFactory(new PropertyValueFactory<>("column3"));
    }

    @FXML
    private void generateReport() {
        String selectedReport = reportTypeComboBox.getValue();

        if (selectedReport == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a report type.");
            return;
        }

        reportData.clear();

        try (Connection conn = DatabaseHandler.getConnection()) {
            String query = getQueryForReport(selectedReport);
            if (query == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid report type.");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reportData.add(new Report(rs.getString(1), rs.getString(2), rs.getString(3)));
            }

            reportTable.setItems(reportData);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate report: " + e.getMessage());
        }
    }

    private String getQueryForReport(String reportType) {
        switch (reportType) {
            case "Exhibits on Exhibition":
                return "SELECT e.name, e.artist, COALESCE(ex.title, 'No Exhibition') AS exhibition_title " +
                        "FROM Exhibits e " +
                        "LEFT JOIN Exhibit_Exhibition ee ON e.id = ee.exhibit_id " +
                        "LEFT JOIN Exhibitions ex ON ee.exhibition_id = ex.id " +
                        "WHERE e.status = 'on_display'";

            case "Ticket Sales":
                return "SELECT ex.title, COUNT(t.id) AS tickets_sold, COALESCE(SUM(t.price), 0) AS total_revenue " +
                        "FROM Tickets t " +
                        "JOIN Exhibitions ex ON t.exhibition_id = ex.id " +
                        "GROUP BY ex.title";

            case "Maintenance Schedule":
                return "SELECT e.name, m.maintenance_date, m.details FROM Maintenance m " +
                        "JOIN Exhibits e ON m.exhibit_id = e.id " +
                        "WHERE m.maintenance_date >= CURDATE() " +
                        "ORDER BY m.maintenance_date";

            default:
                return null;
        }
    }

    @FXML
    private void exportToCSV() {
        if (reportData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Export Error", "No data available to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Stage stage = (Stage) reportTable.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write header
                writer.println("Column 1,Column 2,Column 3");

                // Write data
                for (Report report : reportData) {
                    writer.printf("%s,%s,%s%n",
                            report.getColumn1(),
                            report.getColumn2(),
                            report.getColumn3());
                }

                showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Report exported to " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Export Error", "Failed to export report: " + e.getMessage());
            }
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
        Stage stage = (Stage) reportTable.getScene().getWindow();
        stage.close();
    }
}
