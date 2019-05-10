package views.jfx;

import controleur.Controleur;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.codefx.libfx.control.webview.WebViews;
import views.MenuPrincipalInterface;

import javax.swing.event.HyperlinkEvent;
import java.io.IOException;
import java.net.URL;

/**
 * Created by YohanBoichut on 10/11/15.
 */
public class MenuPrincipal implements MenuPrincipalInterface {

    public ListView<String> listSuggestion;
    public WebView hiddenWV;
    public WebView transOrigin;
    public WebView transTarget;
    private String res="";

    public WebView testWV;
    public TextField wordField;
    private Controleur controleur;

    public void setControleur(Controleur controleur) {
        this.controleur = controleur;
    }

    @FXML
    VBox topNiveau;







    private Stage primaryStage;


    private Scene scene;

    private void setScene(Scene scene) {
        this.scene = scene;
    }





    private void setPrimaryStage(Stage primaryStage) {
        WebViewHyperlinkListener eventPrintingListener = event -> {
            if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)){
                String linkClicked = event.getDescription().toLowerCase();
                try {
                    testWV.getEngine().loadContent(controleur.getDefinitions(linkClicked));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                controleur.translate(hiddenWV,transTarget, transOrigin,linkClicked);
                wordField.setText(linkClicked);
                controleur.getSuggesions(linkClicked,listSuggestion);
            }
            return false;
        };


        WebViews.addHyperlinkListener(testWV, eventPrintingListener);
        this.primaryStage = primaryStage;
    }

    @FXML

    static MenuPrincipal creerInstance(Controleur c, Stage primaryStage) {
        URL location = MenuPrincipal.class.getResource("/views/jfx/menuPrincipal.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Pane root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MenuPrincipal vue = fxmlLoader.getController();
        vue.setControleur(c);
        assert root != null;
        vue.setScene(new Scene(root, 1230, 660));
        vue.setPrimaryStage(primaryStage);
        return vue;
    }


    public void show() {
        primaryStage.setTitle("Dictionnaire");
        primaryStage.setScene(scene);
        primaryStage.show();
    }





    public void getSuggesions(KeyEvent keyEvent) {


        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            getDefinitionsListViewMouse();
            return;
        }
        if(keyEvent.getCode().equals(KeyCode.TAB))
            return;
        if(keyEvent.getCode().equals(KeyCode.DOWN)){
            listSuggestion.requestFocus();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listSuggestion.getSelectionModel().select(0);
                }
            });
            return;
        }

        if(keyEvent.getCode().equals(KeyCode.BACK_SPACE)&&!res.isEmpty())
          res=wordField.getText().substring(0,wordField.getText().length()-1);
        if(!keyEvent.getCode().equals(KeyCode.BACK_SPACE))
          res=wordField.getText()+ keyEvent.getText();

        controleur.getSuggesions(res,listSuggestion);
    }



    public void getDefinitionsListViewKey(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            getDefinitionsListViewMouse();
        }
    }



    public void getDefinitionsListViewMouse() {
        String selectedItem= listSuggestion.getSelectionModel().getSelectedItem();
        if(selectedItem.isEmpty()){
            selectedItem=res;
        }
        wordField.setText(selectedItem);
        try {
            testWV.getEngine().loadContent(controleur.getDefinitions(selectedItem));
            controleur.translate(hiddenWV,transTarget, transOrigin,selectedItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
