package controleur;

import controleur.notifications.Notification;
import controleur.notifications.Sujet;
import controleur.notifications.Observateur;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import views.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class Controleur implements Sujet {

    private MenuPrincipalInterface maVue;

        private FabriqueVues fabriqueVues;



        private Collection<Observateur> observateurs;

    public Controleur(FabriqueVues fabriqueVues)
        {
            this.fabriqueVues = fabriqueVues;
            observateurs = new ArrayList<>();
            this.initialisationVues();
            this.maVue.show();
            this.maVue.initWelcome();
        }


        private void initialisationVues() {
            this.maVue = this.fabriqueVues.buildMenuPrincipalView(this);

       }



    @Override
    public void enregistrerObservateur(Observateur s) {
        this.observateurs.add(s);

    }

    @Override
    public void broadCastNotification(Notification n) {
        for(Observateur s : observateurs) {
            s.notifier(n);
        }
    }


    public String getDefinitions(String text) throws Exception{
        String url = "https://fr.wiktionary.org/wiki/"+text;
        URL obj = new URL(url);
        Document doc =getHtmlContent((HttpURLConnection) obj.openConnection());
        Element content =doc.getElementsByClass("mw-parser-output").first();
        Element title = content.getElementsByTag("p").first();
        Element significations= content.getElementsByTag("ol").first();
        return applyWCSS(title+"\n"+significations);
    }

    private String applyWCSS(String wikiDef) {
        StringBuilder page = new StringBuilder("<!doctype html>\n" +
                "<html lang=\"fr\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n"
                + "<style> body {background-color: #C0C0C0;}");

        page.append(" </style>" + "</head>\n" + "<body>\n").append(wikiDef).append("</body>\n").append("</html>");
        // System.out.println(page);
        return page.toString();

    }

    private Document getHtmlContent(HttpURLConnection con) throws Exception {
        con.setRequestMethod("GET");
        String USER_AGENT = "Mozilla/5.0";
        con.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return Jsoup.parse(response.toString());
    }

    public void getSuggesions(String text, ListView<String> listSuggestion)  {

        Task<String []> getSugTask =new Task< String []>() {
            @Override
            protected  String [] call() throws Exception {
                String [] sug = new String[17];
                String url = "https://fr.wiktionary.org/w/api.php?action=opensearch&format=xml&formatversion=2&search="+text+"&namespace=0%7C100%7C106%7C110&limit=16&suggest=true";
                URL obj = new URL(url);
                Document doc =getHtmlContent((HttpURLConnection) obj.openConnection());
                Elements content =doc.getElementsByTag("Item");
                int i=0;
                for (Element e : content){
                    sug[i]=e.getElementsByTag("text").text();
                    i++;
                }
                return sug;
            }
        };
        getSugTask.setOnSucceeded(event -> listSuggestion.getItems().setAll(getSugTask.getValue()));
        new Service< String []>() {
            @Override
            protected Task< String []> createTask() {
                return getSugTask;
            }
        }.start();

        listSuggestion.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>(){
                    @Override
                    protected void updateItem(String m, boolean bln) {
                        super.updateItem(m, bln);
                        if (m != null) {
                            setText(m);
                        }
                    }
                };
            }
        });

    }

    public void translate(WebView transOrigin, WebView transTarget, WebView transOrigin2, String selectedItem, Label motTranslatedLabel, ListView<String> targetHistorique, ListView<String> historique)  {

        transOrigin.getEngine().load("https://translate.google.com/?hl=fr&tab=TT&authuser=0#view=home&op=translate&sl=fr&tl=ru&text="+selectedItem);
        transOrigin.getEngine().reload();
        ChangeListener<Worker.State> cl= new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                if (newValue != Worker.State.SUCCEEDED) {
                    transOrigin.getEngine().getLoadWorker().stateProperty().removeListener(this);
                    return;
                }
                Document doc = Jsoup.parse((String) transOrigin.getEngine().executeScript("document.documentElement.outerHTML"));
                Elements res = doc.getElementsByClass("gt-cd-c");
                Elements tr = doc.select("span.tlid-translation.translation");
                targetHistorique.getItems().add(tr.text());
                motTranslatedLabel.setText(tr.text());
                historique.getItems().add(historique.getItems().size(),selectedItem);

                Element shortDefinitions = res.get(1);
                Element translations = res.first();
                for (Element number : shortDefinitions.getElementsByClass("gt-def-num")) {
                    number.prependText("(");
                    number.appendText(") ");
                }
                transTarget.getEngine().loadContent(Controleur.this.applyGCSS(translations.toString()));
                transOrigin2.getEngine().loadContent(Controleur.this.applyGCSS(shortDefinitions.toString()));

            }
        };

        transOrigin.getEngine().getLoadWorker().stateProperty().addListener( cl);




    }

    private String applyGCSS(String toString) {
        StringBuilder page = new StringBuilder("<!doctype html>\n" +
                "<html lang=\"fr\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n"
                + "<style> div.gt-def-row {display:inline;}");
        ArrayList<String> ccsLines=new ArrayList<>();
        try{
            Files.lines(Paths.get(System.getProperty("user.dir")+"/src/main/resources/css/style1.css")).forEach(ccsLines::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s:ccsLines
             ) {
            page.append(s);
        }
        page.append("body {background-color: #C0C0C0;}");
        page.append("span.gt-def-num {color: #4169E1;}");
        page.append(".gt-cd-pos {color: #4169E1;}");
        page.append(".gt-baf-back {color: #666666;}");
        page.append(".gt-def-example {color: #666666;}");



        page.append("</style>" + "</head>\n" + "<body>\n").append(toString).append("</body>\n").append("</html>");
       // System.out.println(page);
        return page.toString();


    }
}
