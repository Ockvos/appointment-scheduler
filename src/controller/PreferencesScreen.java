package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import model.Globals;
import model.Languages;
import model.Tools;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 *  This class displays a preferences screen to the user.
 */
public class PreferencesScreen implements Initializable {


    public AnchorPane applicationPane;
    public Button backButton;
    public TilePane iconSelector;
    public Label iconLabel;
    public Label changeNotificationLabel;
    public Label supportLabel;

    String[] languages = {"English", "Dutch", "French"};

    public ComboBox<String> languageComboBox;

    /**
     *  Calls methods to set up the language options combo box and profile icon selector.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        languageComboBox.getItems().addAll(languages);

        languageComboBox.setPromptText("Select Language");

        setupIconSelector();
    }


    /**
     *  Changes all text to selected language.
     */
    public void onLanguageComboBox() {

        supportLabel.setText("");
        Languages.setLanguageManually(languageComboBox.getValue());
        backButton.setText(Languages.translateWord("back", true));
        iconLabel.setText(Languages.translateSentence("select profile image", true));

        if (!changeNotificationLabel.getText().isBlank()){
               changeNotificationLabel.setText(Languages.translateSentence("image changed", true));
        }
    }

    /**
     *  Returns user to main menu screen.
     */
    public void onBackButton(ActionEvent actionEvent) throws IOException {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/mainMenuScreen.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, Globals.MAIN_MENU_WIDTH, Globals.MAIN_MENU_HEIGHT); // Length, height
            stage.setTitle(Globals.WINDOW_NAME);
            stage.setScene(scene);
            stage.show();

    }


    /**
     *  Sets up icon selector.
     *  If mouse click is detected on image, label will inform user of selection, and change color.
     */
    public void setupIconSelector() {

        //Adding the array of buttons to the pane
        ImageView image1 = new ImageView(new Image(getClass().getResourceAsStream(Globals.PROFILE_1)));
        ImageView image2 = new ImageView(new Image(getClass().getResourceAsStream(Globals.PROFILE_2)));
        ImageView image3 = new ImageView(new Image(getClass().getResourceAsStream(Globals.PROFILE_3)));

        image1.setPickOnBounds(true); // allows click on transparent areas
        image1.setOnMouseClicked((MouseEvent e) -> {
            changeNotificationLabel.setText(Languages.translateSentence("image changed", true));
            changeNotificationLabel.setTextFill(Paint.valueOf("#19b411"));  // green text
            User.setProfilePicture(Globals.PROFILE_1);
        });

        image2.setPickOnBounds(true); // allows click on transparent areas
        image2.setOnMouseClicked((MouseEvent e) -> {
            changeNotificationLabel.setText(Languages.translateSentence("image changed", true));
            changeNotificationLabel.setTextFill(Paint.valueOf("#206ed6"));  // blue text
            User.setProfilePicture(Globals.PROFILE_2);
        });

        image3.setPickOnBounds(true); // allows click on transparent areas
        image3.setOnMouseClicked((MouseEvent e) -> {
            changeNotificationLabel.setText(Languages.translateSentence("image changed", true));
            changeNotificationLabel.setTextFill(Paint.valueOf("#830cd1"));  // purple text
            User.setProfilePicture(Globals.PROFILE_3);
        });

        ObservableList list = iconSelector.getChildren();

        list.add(image1);
        list.add(image2);
        list.add(image3);
    }
}
