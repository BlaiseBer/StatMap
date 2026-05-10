package Donne;

import Main.Contour;
import Main.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import org.geojson.Feature;
import org.geojson.FeatureCollection;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;

public class Region {
    private static final String lien = "https://france-geojson.gregoiredavid.fr/repo/regions.geojson";
   //J'utilise un dictionnaire pour facilement accéder à chaque département
   private static final Hashtable<String, Contour> regdic = new Hashtable();
   
    public static final Hashtable<String, String> codeVersString = new Hashtable();
    public static final Hashtable<String, String> stringVersCode = new Hashtable();
   
    public static void init() throws IOException {
        String jsonString;
        try {
            //je définis l'input stream correspondant
            URL url = new URL(lien);
            InputStream is = url.openStream();
            jsonString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            is.close();
            
        } catch (IOException e) {
            //je définis l'input stream correspondant
            jsonString = Files.readString(Path.of("regions.geojson"));
            
            //j'affiche un message d'erreur si les données ne peuvent pas être récupérée en ligne
            //La carte d'accueil peut quand même s'afficher, mais si l'odinateur est hors connection,
            // aucune donnée ne pourra être affichée.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("IOException");
            alert.setContentText("l'API forme géographique est introuvable. Activer votre connection internet pour afficher des données ");
        }
        
        // def ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // Transformer le String en un objet FeatureCollection
        FeatureCollection featureCollection = mapper.readValue(jsonString, FeatureCollection.class);
        
        
        // Complétion de depdic
        for (Feature feature : featureCollection.getFeatures()) {
            regdic.put(Utils.simplify(feature.getProperty("nom")), new Contour(feature));
            codeVersString.put(feature.getProperty("code"), Utils.simplify(feature.getProperty("nom")));
            stringVersCode.put(Utils.simplify(feature.getProperty("nom")), feature.getProperty("code"));
        }
    }

    public static Contour getContour(String nom) {
        return regdic.get(nom);
    }
    public static ArrayList<Contour> getList() {
        ArrayList<Contour> temp = new ArrayList();
        
        for (String k : regdic.keySet()) {
            temp.add(regdic.get(k));
        }
        return temp;
    }
}