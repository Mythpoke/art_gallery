package artGallery;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class DashboardController {

    @FXML
    private Button userManagementButton;

    @FXML
    private Button exhibitManagementButton;

    @FXML
    private Button exhibitionManagementButton;

    @FXML
    private Button reportManagementButton;

    @FXML
    private Button staffManagementButton;

    @FXML
    private Button tourManagementButton;

    @FXML
    private Button systemConfigButton;

    @FXML
    public void openUserManagement() throws Exception {
        openModule("/resources/user_management.fxml", "User Management");
    }

    @FXML
    public void openExhibitManagement() throws Exception {
        openModule("/resources/exhibit_management.fxml", "Exhibit Management");
    }

    @FXML
    public void openExhibitionManagement() throws Exception {
        openModule("/resources/exhibition_management.fxml", "Exhibition Management");
    }

    @FXML
    public void openReportManagement() throws Exception {
        openModule("/resources/report_management.fxml", "Report Management");
    }

    @FXML
    public void openStaffManagement() throws Exception {
        openModule("/resources/staff_management.fxml", "Staff Management");
    }

    @FXML
    public void openTourManagement() throws Exception {
        openModule("/resources/tour_management.fxml", "Tour Management");
    }

    @FXML
    public void openSystemConfig() throws Exception {
        openModule("/resources/system_config.fxml", "System Configuration");
    }

    private void openModule(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(title);
        stage.show();
    }
}
