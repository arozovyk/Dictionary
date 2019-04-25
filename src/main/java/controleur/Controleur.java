package controleur;

import controleur.notifications.Notification;
import controleur.notifications.Sujet;
import controleur.notifications.Observateur;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import views.*;
import views.jfx.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
@SuppressWarnings("Duplicates")

public class Controleur implements Sujet {
    private final String USER_AGENT = "Mozilla/5.0";

        private MenuPrincipalInterface maVue;

        private FabriqueVues fabriqueVues;
        private String pseudo;
        private AccueilInterface accueilView;
        private ArrayList<String> liste;
        private long idM;



        private Collection<Observateur> observateurs;

    public Controleur(FabriqueVues fabriqueVues)
        {
            this.fabriqueVues = fabriqueVues;
            observateurs = new ArrayList<>();
            this.initialisationVues();
            this.maVue.show();
        }


        public void initialisationVues() {
            this.maVue = this.fabriqueVues.buildMenuPrincipalView(this);

       }


    public ArrayList<String> getEquipes(long idMatch){
        long idM = idMatch;
        ArrayList<String> listeVainqueurs = new ArrayList<String>();

        return listeVainqueurs;
        }


    public void goToMenuPrincipal(){
            this.accueilView.show();
        }

////////////////////////////////////////////////////////////////////////////:

        public void goToAccueil(String user, String password){
            pseudo = user;

                this.accueilView.show();

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
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        boolean foundDiv;
        while ((inputLine = in.readLine()) != null) {
            //if(inputLine.trim().equals("<div class=\"mw-parser-output\">"))
            response.append(inputLine);
        }
        in.close();
        Document doc = Jsoup.parse(response.toString());
        //print result
        Element content =doc.getElementsByClass("mw-parser-output").first();
        Element title = content.getElementsByTag("p").first();
        Element significations= content.getElementsByTag("ol").first();
        return title+"\n"+significations;
    }

    public String[] getSuggesions(String text) throws Exception {

        String [] sug = new String[5];
        String url = "https://fr.wiktionary.org/w/api.php?action=opensearch&format=xml&formatversion=2&search="+text+"&namespace=0%7C100%7C106%7C110&limit=5&suggest=true";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Document doc = Jsoup.parse(response.toString());
        //print result
        Elements content =doc.getElementsByTag("Item");
        int i=0;
        for (Element e : content){
            sug[i]=e.getElementsByTag("text").text();
            i++;
        }
        return sug;
    }

    public Document translate(String lala) throws Exception {
        System.out.println("we here");
        String [] sug = new String[5];
        String url = "https://translate.google.com/#fr|ru|"+lala;
        System.out.println(url);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response);
        return Jsoup.parse(response.toString());
        //print result
    }
}
