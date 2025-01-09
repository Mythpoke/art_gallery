package artgallery;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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
                "Exhibit Maintenance Status",
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
            System.out.println("No report type selected.");
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a report type.");
            return;
        }
        System.out.println("Generating report for: " + selectedReport);
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
            System.out.println("Report data loaded successfully.");
            reportTable.setItems(reportData);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate report: " + e.getMessage());
        }
    }
    @FXML
    private void goBack() {
        Stage stage = (Stage) reportTable.getScene().getWindow();
        stage.close();
    }


    private String getQueryForReport(String reportType) {
        switch (reportType) {
            case "Exhibits on Exhibition":
                return "SELECT e.name, e.artist, COALESCE(ex.title, 'No Exhibition') AS exhibition_title " +
                        "FROM Exhibits e " +
                        "LEFT JOIN Exhibit_Exhibition ee ON e.id = ee.exhibit_id " +
                        "LEFT JOIN Exhibitions ex ON ee.exhibition_id = ex.id " +
                        "WHERE e.status = 'on_display'";

            case "Exhibit Maintenance Status":
                return "SELECT e.name, m.maintenance_date, m.details FROM Exhibits e " +
                        "JOIN Maintenance m ON e.id = m.exhibit_id";

            case "Ticket Sales":
                return "SELECT ex.title, COUNT(t.id) AS tickets_sold, SUM(t.price) AS total_revenue FROM Tickets t " +
                        "JOIN Exhibitions ex ON t.exhibition_id = ex.id GROUP BY ex.title";

            case "Maintenance Schedule":
                return "SELECT e.name, m.maintenance_date, m.details FROM Maintenance m " +
                        "JOIN Exhibits e ON m.exhibit_id = e.id";

            default:
                return null;
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
