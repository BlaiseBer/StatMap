package Donne;

import Main.Contour;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import javafx.scene.control.Alert;

import org.geojson.Feature;
import org.geojson.FeatureCollection;

public class Commune {
   private static FeatureCollection featureCollection;
   private static final String lien = "https://france-geojson.gregoiredavid.fr/repo/communes.geojson";
   //J'utilise un dictionnaire pour facilement accéder à chaque département
   private static final Hashtable<String, Contour> comdic = new Hashtable();
   //j'utilise un dictionnaire pour renvoyer la liste des communes d'un département à partir de son numéro
   private static Hashtable<String, ArrayList<String>> catalog = new Hashtable();
     
    public static void init() throws MalformedURLException, IOException {
        String jsonString;
        try {
            //je défini l'input stream correspondant
            URL url = new URL(lien);
            InputStream is = url.openStream();
            jsonString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            is.close();
            
        } catch (IOException e) {
            
            //j'affiche un message d'erreur si les données ne peuvent pas être récupérée en ligne
            //La carte d'accueil peut quand même s'afficher, mais si l'odinateur est hors connection,
            // aucune donnée ne pourra être affichée.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("IOException");
            alert.setContentText("l'API forme géographique est introuvable. Activer votre connection internet pour afficher des données");

            alert.showAndWait();
            
            
            //je défini l'input stream correspondant
            jsonString = Files.readString(Path.of("communes.geojson"));
        }
        
        // def ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // Transformer le String en un objet FeatureCollection
        featureCollection = mapper.readValue(jsonString, FeatureCollection.class);
        
        
        // Complétion de depdic
        for (Feature feature : featureCollection.getFeatures()) {
            
           comdic.put(feature.getProperty("code"), new Contour(feature));
           
           String codeRegion = feature.getProperty("code").toString().substring(0,2);
           if (catalog.get(codeRegion) == null) {
               ArrayList<String> value = new ArrayList();
               value.add(feature.getProperty("code").toString());
               catalog.put(codeRegion, value);
           } else {
            catalog.get(codeRegion).add(feature.getProperty("code"));
            }
        }
    
    }
    public static ArrayList<Contour> getList() {
        ArrayList<Contour> temp = new ArrayList();
        
        for (String k : comdic.keySet()) {
            temp.add(comdic.get(k));
        }
        return temp;
    }
    public static ArrayList<Contour> getList(String nomdep) {
        ArrayList<Contour> temp = new ArrayList();
        for (String k : catalog.get(Departement.getContour(nomdep).getCode())) {
            temp.add(comdic.get(k));
        }
        return temp;

    }
}
