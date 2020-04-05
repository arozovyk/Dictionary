package views.jfx;

import controleur.Controleur;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.codefx.libfx.control.webview.WebViews;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLIFrameElement;
import views.MenuPrincipalInterface;

import javax.swing.event.HyperlinkEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Set;


public class MenuPrincipal implements MenuPrincipalInterface {

    public ListView<String> listSuggestion;
    public WebView hiddenWV;
    public WebView transOrigin;
    public WebView transTarget;
    public ListView<String> historique;
    public Label motOrigineLabel;
    public Label motTranslatedLabel;
    public ListView<String> targetHistory;
    private String res="";
    public WebView definitionWV;
    public TextField wordField;
    private Controleur controleur;
    EventListener mouseOverEventListener;
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
        vue.setScene(new Scene(root, 1230, 830));
        vue.setPrimaryStage(primaryStage);
        return vue;
    }


    public void show() {
        primaryStage.setTitle("Dictionnaire");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void initWelcome(){

        WebViewHyperlinkListener eventPrintingListener = event -> {
            if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)){
                res = event.getDescription().toLowerCase();
                defineWord(res);
                wordField.setText(res);
                controleur.getSuggesions(res,listSuggestion);
            }
            return false;
        };

        WebViews.addHyperlinkListener(definitionWV, eventPrintingListener);

        WebViewHyperlinkListener eventPrintingListener2 = event -> {
            System.out.println(event);
            return false;
        };

        WebViews.addHyperlinkListener(transTarget, eventPrintingListener2);


        defineWord("bienvenue");
        wordField.setPromptText("Saisissez le mot à définir");
        Set<Node> nodes =historique.lookupAll(".scroll-bar");
        for(Node n1: nodes){
            if (n1 instanceof ScrollBar) {
                final ScrollBar bar1 = (ScrollBar) n1;
                Set<Node> nodes2 =targetHistory.lookupAll(".scroll-bar");
                for (Node n2 : nodes2){
                    if (n2 instanceof ScrollBar) {
                        final ScrollBar bar2 = (ScrollBar) n2;
                        if((bar1.getOrientation()== Orientation.HORIZONTAL
                                &&bar2.getOrientation()==Orientation.HORIZONTAL)
                                ||(bar1.getOrientation()==Orientation.VERTICAL&&bar2.getOrientation()==Orientation.VERTICAL)){
                            bar1.valueProperty().bindBidirectional(bar2.valueProperty());
                        }
                    }
                }

            }
        }





        (new Thread(()->{
            while (true){
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(
                        () -> {wordField.requestFocus();}
                );
            }
        })).start();


        WebEngine we =transTarget.getEngine();
        we.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {

                mouseOverEventListener = new EventListener() {
                    @Override
                    public void handleEvent(Event ev) {
                        String href = getNextHref((Element) ev.getTarget());
                        if (href != null && !href.isEmpty()) {
                            if (href.startsWith("/")) {
                                href = ((Element) ev.getTarget()).getBaseURI() + href;
                            }
                            targetHistory.getItems().set(targetHistory.getItems().size()-1,href);
                        }
                    }

                    private String getNextHref(Element target) {
                        while (target.getAttribute("class") == null) {
                            if (target.toString().contains("HTMLHtmlElement")) {
                                return "";
                            }
                            target = (Element) target.getParentNode();
                            if (target == null) {
                                return "";
                            }
                        }
                        return target.getTextContent();
                    }
                };



                Document document = we.getDocument();
                addListener(document.getElementsByTagName("*"));
            }
        });

    }

    private void addListener(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            try {
                HTMLIFrameElement iFrame = ((HTMLIFrameElement) nodeList.item(i));
                addListener(iFrame.getContentDocument().getElementsByTagName("*"));
            } catch (Exception e) {
                Element el = (Element) nodeList.item(i);
                while (!el.toString().contains("HTMLHtmlElement")) {
                    el = (Element) el.getParentNode();
                    ((EventTarget) el).removeEventListener("click", mouseOverEventListener, false);
                }
                ((EventTarget) nodeList.item(i)).addEventListener("click", mouseOverEventListener, false);
            }
        }
    }


    public void getSuggesions(KeyEvent keyEvent) {

        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            defineWord(res);
            return;
        }
        if(keyEvent.getCode().equals(KeyCode.TAB))
            return;
        if(keyEvent.getCode().equals(KeyCode.DOWN)){
            listSuggestion.requestFocus();
            Platform.runLater(() -> listSuggestion.getSelectionModel().select(0));
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
        wordField.setText(selectedItem);
        defineWord(selectedItem);

    }


    private void defineWord(String word){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    definitionWV.getEngine().loadContent(controleur.getDefinitions(word));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        motOrigineLabel.setText(word);

        controleur.translate(hiddenWV,transTarget, transOrigin,word,motTranslatedLabel,targetHistory,historique);

    }

    public void getDefinitionsListViewKeyHist(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            getDefinitionsListViewMouseHist();
        }
    }

    public void getDefinitionsListViewMouseHist() {
        String selectedItem= historique.getSelectionModel().getSelectedItem();
        if(selectedItem.length()>0){
            wordField.setText(selectedItem);
            defineWord(selectedItem);
        }

    }

    public void effacerLHistorique(ActionEvent actionEvent) {
        historique.getSelectionModel().clearSelection();
        historique.getItems().clear();
        targetHistory.getSelectionModel().clearSelection();
        targetHistory.getItems().clear();
    }

    public void saveHistory(ActionEvent actionEvent) {


        controleur.saveHistory(transTarget);
    }

    public void targetOnClicked(MouseEvent mouseEvent) {

    }
}
