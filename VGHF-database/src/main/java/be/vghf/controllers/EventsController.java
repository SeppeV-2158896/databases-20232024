package be.vghf.controllers;

import be.vghf.domain.Game;
import be.vghf.domain.Location;
import be.vghf.enums.LocationType;
import be.vghf.enums.UserType;
import be.vghf.models.ActiveUser;
import be.vghf.repository.GameRepository;
import be.vghf.repository.LocationRepository;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class EventsController implements Controller {
    private BaseController baseController;
    private LocationRepository locationRepository;
    @FXML private TilePane locationsTilePane;
    @FXML private Button addLocationButton;
    @FXML private TextField locationSearchText;
    @FXML private ComboBox<String> locationTypeComboBox;

    @FXML public void initialize(){
        addLocationButton.setVisible(false);
        locationRepository = new LocationRepository();
        setLocations(locationRepository.getAll());

        locationTypeComboBox.setPromptText("Location type");
        locationTypeComboBox.getItems().add("All");
        locationTypeComboBox.getItems().add("Expo");
        locationTypeComboBox.getItems().add("Library");
        locationTypeComboBox.getItems().add("Museum");
        if (ActiveUser.user != null && ActiveUser.user.getUserType() == UserType.VOLUNTEER) {
            locationTypeComboBox.getItems().add("Private");
            locationTypeComboBox.getItems().add("Storage");
        }

        locationTypeComboBox.setOnAction(this::handleLocationTypeChanged);
        locationSearchText.setOnKeyReleased(this::handleLocationSearch);
    }

    public void update(){
        if (ActiveUser.user == null) {
            addLocationButton.setVisible(false);
             return;
        }
        if (ActiveUser.user.getUserType().equals(UserType.VOLUNTEER)) {
            addLocationButton.setVisible(true);
        }
    }

    public void updateLocationDetails(Location newLocation){
        List<Location> locations = locationRepository.getAll();
        setLocations(locations);
    }
    @FXML protected void handleLocationTypeChanged(ActionEvent actionEvent) {
        String locationType = locationTypeComboBox.getValue();
        List<Location> locationResults = null;

        if(this.locationSearchText.getText() != ""){
           queryLocationsWithAddress();
           return;
        }

        if(locationType == "Private"){
            locationResults = locationRepository.getLocationByType(LocationType.PRIVATE, locationRepository.getAll());
        }else if(locationType == "Expo"){
            locationResults = locationRepository.getLocationByType(LocationType.EXPO, locationRepository.getAll());
        }else if(locationType == "Library"){
            locationResults = locationRepository.getLocationByType(LocationType.LIBRARY, locationRepository.getAll());
        }else if(locationType == "Storage") {
            locationResults = locationRepository.getLocationByType(LocationType.STORAGE, locationRepository.getAll());
        }else if(locationType == "Museum") {
            locationResults = locationRepository.getLocationByType(LocationType.MUSEUM, locationRepository.getAll());
        }else{
            locationResults = locationRepository.getAll();
        }
        setLocations(locationResults);
    }

    @FXML protected void handleLocationSearch(KeyEvent event) {
        if(event.getCode() != KeyCode.ENTER){
            return;
        }
        queryLocationsWithAddress();
    }

    private void queryLocationsWithAddress(){
        String locationType = locationTypeComboBox.getValue();

        String locationSearchText = this.locationSearchText.getText();
        String[] locationSearch = locationSearchText.split("\\s+");

        List<Location> locationResults = null;

        if(locationType == "Private"){
            locationResults = queryLocationsWithType(locationSearch, LocationType.PRIVATE);
        }else if(locationType == "Expo"){
            locationResults = queryLocationsWithType(locationSearch, LocationType.EXPO);
        }else if(locationType == "Library"){
            locationResults = queryLocationsWithType(locationSearch, LocationType.LIBRARY);
        }else if(locationType == "Storage"){
            locationResults = queryLocationsWithType(locationSearch, LocationType.STORAGE);
        }else if(locationType == "Museum"){
            locationResults = queryLocationsWithType(locationSearch, LocationType.MUSEUM);
        }else{
            locationResults = queryLocationsWithoutType(locationSearch);
        }
        setLocations(locationResults);
    }

    private List<Location> queryLocationsWithoutType(String[] locationSearch) {
        var results = locationRepository.getLocationByAddress(locationSearch);
        return results;
    }

    private List<Location> queryLocationsWithType(String[] locationSearch, LocationType type) {
        var addressResults = locationRepository.getLocationByAddress(locationSearch);
        var results = locationRepository.getLocationByType(type, addressResults);

        return results;
    }

    public void setLocations(List<Location> locations) {
        locationsTilePane.getChildren().clear();
        locationsTilePane.setVgap(10);
        locationsTilePane.setHgap(10);
        locationsTilePane.getChildren().addAll(
                locations.stream()
                        .filter(location -> {
                            if (ActiveUser.user != null && ActiveUser.user.getUserType() == UserType.VOLUNTEER) {
                                return true;
                            } else {
                                return location.getLocationType() != LocationType.PRIVATE &&
                                        location.getLocationType() != LocationType.STORAGE;
                            }
                        })
                        .map(this::createLocationTile)
                        .collect(Collectors.toList())
        );
    }

    private AnchorPane createLocationTile(Location location) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(200, 100);
        anchorPane.setStyle("-fx-background-color: #DFA5B9");

        Label ownerLabel = new Label("Owner: " + location.getOwner().getFirstName() + " " + location.getOwner().getLastName());
        ownerLabel.setWrapText(true);

        Label addressLabel = new Label("Address: " + location);
        addressLabel.setWrapText(true);

        Label typeLabel = new Label("Type: " + location.getLocationType());
        typeLabel.setWrapText(true);

        Hyperlink gamesLink = new Hyperlink("View Games");
        gamesLink.setOnAction(event -> handleGames(location));

        Hyperlink controlPanelLink = new Hyperlink("Open Control Panel");
        controlPanelLink.setOnAction(event -> handleControl(location));
        controlPanelLink.setVisible(ActiveUser.user != null && ActiveUser.user.getUserType() == UserType.VOLUNTEER);

        anchorPane.getChildren().addAll(ownerLabel, addressLabel, typeLabel, gamesLink, controlPanelLink);

        AnchorPane.setTopAnchor(ownerLabel, 10.0);
        AnchorPane.setLeftAnchor(ownerLabel, 10.0);
        AnchorPane.setTopAnchor(addressLabel, 30.0);
        AnchorPane.setLeftAnchor(addressLabel, 10.0);
        AnchorPane.setTopAnchor(typeLabel, 50.0);
        AnchorPane.setLeftAnchor(typeLabel, 10.0);
        AnchorPane.setTopAnchor(gamesLink, 70.0);
        AnchorPane.setLeftAnchor(gamesLink, 10.0);
        AnchorPane.setTopAnchor(controlPanelLink, 90.0);
        AnchorPane.setLeftAnchor(controlPanelLink, 10.0);

        var listener = this;
        anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (ActiveUser.user == null){
                    return;
                }
                if (ActiveUser.user.getUserType() == UserType.VOLUNTEER){
                    LocationAdminController laController = new LocationAdminController();
                    baseController.showView("Location details", laController, "/locationAdmin-view.fxml");
                    laController.setLocation(location);
                    laController.setListener(listener);
                }
            }
        });

        return anchorPane;
    }

    private void handleControl(Location location) {
        var controller = new ControlPannelController();
        baseController.showView("Location Control Panel", controller, "/controlPanel-view.fxml");
        controller.setLocation(location);
    }

    private void handleGames(Location location) {
            if (ActiveUser.user == null || ActiveUser.user.getUserType() == UserType.CUSTOMER){
                BrowseController bController = new BrowseController();
                baseController.changeSubscene("/browse-view.fxml", bController);
                bController.initializeGamesWithLocation(location);
            }else if (ActiveUser.user.getUserType() == UserType.VOLUNTEER){
                var controller = new GamesAtLocationController();
                baseController.changeSubscene("/location-games-tree-view.fxml", controller);
                controller.initialize(location);
                controller.setBaseController(baseController);
            }
    }

    @FXML protected void handleAddLocation(ActionEvent event){
        LocationAdminController adminController = new LocationAdminController();
        adminController.setNewLocation(true);
        adminController.setListener(this);
        baseController.showView("Create new location", adminController, "/LocationAdmin-view.fxml");
    }

    @Override
    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
        baseController.setListener(this);
    }

    @Override
    public void setListener(Controller controller) {

    }
}
