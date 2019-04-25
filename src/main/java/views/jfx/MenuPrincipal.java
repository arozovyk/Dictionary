package views.jfx;
import org.jsoup.nodes.Document;

import com.sun.webkit.WebPage;
import controleur.Controleur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import views.MenuPrincipalInterface;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by YohanBoichut on 10/11/15.
 */
public class MenuPrincipal implements MenuPrincipalInterface {

    public ListView listSuggestion;
    public WebView transOrigin;
    public WebView transTarget;
    String res="";

    public WebView testWV;
    public TextField wordField;
    Controleur controleur;

    public void setControleur(Controleur controleur) {
        this.controleur = controleur;
    }

    @FXML
    VBox topNiveau;





    @FXML
    private Button monBouton;


    private Stage primaryStage;


    private Scene scene;

    public void setScene(Scene scene) {
        this.scene = scene;
    }





    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private TextField monChamp;

    public static MenuPrincipal creerInstance(Controleur c, Stage primaryStage) {

        URL location = MenuPrincipal.class.getResource("/views/jfx/menuPrincipal.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Pane root = null;
        try {
            root = (Pane) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MenuPrincipal vue = fxmlLoader.getController();
        vue.setControleur(c);
        vue.setScene(new Scene(root, 1230, 660));
        vue.setPrimaryStage(primaryStage);
        return vue;
    }


    public void show() {
        listSuggestion.setOnKeyPressed(keyEvent->{
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                String lala=(String)listSuggestion.getSelectionModel().getSelectedItem();
                try {
                    testWV.getEngine().loadContent(controleur.getDefinitions(lala));
                    Document translationGoogle = controleur.translate(lala);
                    System.out.println(controleur.translate(lala).getElementsByClass("gt-cd-c").first());
                    //transOrigin.getEngine().loadContent(translationGoogle.getElementsByClass("gt-cd-c").toString());
                    //transTarget.getEngine().loadContent(translationGoogle.getElementsByClass("gt-baf-table").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        primaryStage.setTitle("Menu principal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public void getDefinitions(ActionEvent actionEvent) {
        try {
            testWV.getEngine().loadContent(controleur.getDefinitions(res));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    public void majSuggesions2(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)||keyEvent.getCode().equals(KeyCode.TAB))
            return;
        if(keyEvent.getCode().equals(KeyCode.DOWN)){
            listSuggestion.requestFocus();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listSuggestion.scrollTo(0);
                    listSuggestion.getSelectionModel().select(0);
                }
            });
        }
        if(keyEvent.getCode().equals(KeyCode.BACK_SPACE)&&!res.isEmpty())
          res=wordField.getText().substring(0,wordField.getText().length()-1);
        if(!keyEvent.getCode().equals(KeyCode.BACK_SPACE))
          res=wordField.getText()+ keyEvent.getText();

        String [] suggestions={};
        try {
            suggestions = controleur.getSuggesions(res);
            //TextFields.bindAutoCompletion(wordField,controleur.getSuggesions(wordField.getText()+keyEvent.getCharacter()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        listSuggestion.getItems().setAll(suggestions);
        listSuggestion.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String m, boolean bln) {
                        super.updateItem(m, bln);
                        if (m != null) {
                            setText(m);
                        }
                    }
                };
                return cell;
            }
        });
        //TextFields.bindAutoCompletion(wordField,suggestions);
    }

    public void getDefinitions2(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            try {
                testWV.getEngine().loadContent(controleur.getDefinitions((String)listSuggestion.getSelectionModel().getSelectedItem()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
