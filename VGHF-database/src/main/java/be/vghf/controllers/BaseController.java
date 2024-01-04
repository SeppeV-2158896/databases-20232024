package be.vghf.controllers;

import be.vghf.domain.User;
import be.vghf.enums.UserType;
import be.vghf.models.ActiveUser;
import be.vghf.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class BaseController{

    @FXML private Text testTekst;
    @FXML private Button accountButton;
    @FXML private Button usersButton;
    @FXML private Button eventsButton;
    @FXML private Button browseButton;
    @FXML private VBox browseVBox;
    @FXML private Button loanedItemsButton;

    private ActiveUser loggedInUser;

    @FXML
    public void initialize() {
        usersButton.setVisible(false);
        loanedItemsButton.setVisible(false);

        browseVBox.setVisible(false);

        ActiveUser.user = null;
    }

    @FXML protected void handleAccountButtonPressed(ActionEvent event) throws IOException {
        showView("/loginOrRegister-view.fxml", new AccountController());
    }

    @FXML protected void handleBrowseButtonPressed(ActionEvent event) throws IOException {
        browseVBox.setVisible(!browseVBox.isVisible());
    }

    public Stage showView(String path, Controller controller){
        try {
            var loader = new FXMLLoader(BaseController.class.getResource(path));

            if (controller != null) {
                loader.setController(controller);
                controller.setBaseController(this);
            }
                Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("VGHF Database Software");
            stage.setScene(new Scene(root));
            stage.show();

            return stage;

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void update() {

        if (ActiveUser.user == null){
            usersButton.setVisible(false);
            loanedItemsButton.setVisible(false);

            return;
        }

        loanedItemsButton.setVisible(true);

        if (ActiveUser.user.getUserType().equals(UserType.VOLUNTEER)){
            usersButton.setVisible(true);
        }
    }

}
