package be.vghf.controllers;

import be.vghf.domain.Game;
import be.vghf.domain.User;
import be.vghf.repository.GenericRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class GameAdminController implements Controller {
    private BaseController baseController;

    @FXML
    private TextField titleField;

    @FXML
    private TextField releaseDateField;

    @FXML
    private TextField genreField;

    @FXML
    private Button ownerButton;

    @FXML
    private Button homeBaseButton;

    @FXML
    private Button currentLocationButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    private BrowseController listener;

    // Inject your Game instance here
    private Game game;

    // Set the Game instance from your main application
    public void setGame(Game game) {
        this.game = game;
        populateFields();
    }
    public void saveGame(ActionEvent event) {
        game.setTitle(titleField.getText());
        game.setReleaseDate(releaseDateField.getText());
        game.setGenre(genreField.getText());

        GenericRepository.update(game);

        listener.updateGameDetails(game);

        Button sourceButton = (Button) event.getSource();
        Stage stage = (Stage) sourceButton.getScene().getWindow();
        stage.close();
    }

    public void deleteGame(ActionEvent actionEvent) {
        GenericRepository.delete(game);
    }

    private void populateFields() {
        titleField.setText(game.getTitle());
        releaseDateField.setText(game.getReleaseDate());
        genreField.setText(game.getGenre());
        ownerButton.setText(game.getOwner().getFirstName() + " " + game.getOwner().getLastName());
        homeBaseButton.setText(game.getHomeBase().toString());
        currentLocationButton.setText(game.getCurrentLocation().toString());
    }

    @FXML protected void editOwner(ActionEvent event){
        EditOwnerLocationController eolController = new EditOwnerLocationController();
        baseController.showView("Edit owner", eolController, "/editOwnerLocations-view.fxml");
        eolController.setListener(this);
    }

    public void selectedUserConfirmed(User user){
        game.setOwner(user);
        ownerButton.setText(game.getOwner().getFirstName() + " " + game.getOwner().getLastName());
    }

    public void editHomeBase(ActionEvent event){

    }

    public void editCurrentLocation(ActionEvent event){

    }

    @Override
    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
    }

    @Override
    public void setListener(Controller controller){
        this.listener = (BrowseController) controller;
    }
}
