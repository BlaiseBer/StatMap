package Donne;

import Main.Contour;
import Main.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import org.geojson.Feature;
import org.geojson.FeatureCollection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;


public class Departement {
    private static final String lien = "https://france-geojson.gregoiredavid.fr/repo/departements.geojson";
   
    //J'utilise un dictionnaire pour facilement accéder à chaque département (nom -> Contour).
    private static final Hashtable<String, Contour> depdic = new Hashtable();
   
    // j'utilise un dictionnaire qui à chaque code de région renvoie la liste des codes de ses départements 
    private static final Hashtable<String, ArrayList<String>> catalog = new Hashtable<>() {{
       put("84", new ArrayList<>(Arrays.asList("01", "03", "07", "15", "26", "38", "42", "43", "63", "69", "73", "74")));
       put("27", new ArrayList<>(Arrays.asList("21", "25", "39", "58", "70", "71", "89","90")));
       put("53", new ArrayList<>(Arrays.asList("22", "29", "35", "56")));
       put("24", new ArrayList<>(Arrays.asList("18", "28", "36", "37", "41", "45")));
       put("94", new ArrayList<>(Arrays.asList("2A", "2B")));
       put("44", new ArrayList<>(Arrays.asList("08", "10", "51", "52", "54", "55", "57", "67", "68", "88")));
       put("32", new ArrayList<>(Arrays.asList("02", "59", "60", "62", "80")));
       put("11", new ArrayList<>(Arrays.asList("75", "77", "78", "91", "92", "93", "94", "95")));
       put("28", new ArrayList<>(Arrays.asList("14", "27", "50", "61", "76")));
       put("75", new ArrayList<>(Arrays.asList("16", "17", "19", "23", "24", "33", "40", "47", "64", "79", "86", "87")));
       put("76", new ArrayList<>(Arrays.asList("09", "11", "12", "30", "31", "32","34", "46", "48", "65", "66", "81", "82")));
       put("52", new ArrayList<>(Arrays.asList("44", "49", "53", "72", "85")));
       put("93", new ArrayList<>(Arrays.asList("04", "05", "06", "13", "83", "84")));
       put("1", new ArrayList<>(List.of("971")));
       put("2", new ArrayList<>(List.of("972")));
       put("3", new ArrayList<>(List.of("973")));
       put("4", new ArrayList<>(List.of("974")));
       put("5", new ArrayList<>(List.of("976")));
   }};
   
    //j'utilise un dictionnaire pour renvoyer le nom d'un département à partir de son code
    public static final Hashtable<String, String> codeVersString = new Hashtable(); // CODE_VERS_STRING
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
            jsonString = Files.readString(Path.of("departements.geojson"));
            
            //j'affiche un message d'erreur si les données ne peuvent pas être récupérée en ligne
            //La carte d'accueil peut quand même s'afficher, mais si l'odinateur est hors connection,
            // aucune donnée ne pourra être affichée.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("IOException");
            alert.setContentText("l'API forme géographique est introuvable. Activer votre connection internet pour afficher des données");
        }
        
        
        // def ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // Transformer le String en un objet FeatureCollection
        FeatureCollection featureCollection = mapper.readValue(jsonString, FeatureCollection.class);
        
        
        // Complétion de depdic
            for (Feature feature : featureCollection.getFeatures()) {
                depdic.put(Utils.simplify(feature.getProperty("nom")), new Contour(feature));
                codeVersString.put(feature.getProperty("code"), Utils.simplify(feature.getProperty("nom")));
                stringVersCode.put(Utils.simplify(feature.getProperty("nom")), feature.getProperty("code"));
        }
    }

    public static Contour getContour(String nom) {
        return depdic.get(nom);
    }
    public static ArrayList<Contour> getList() {
        ArrayList<Contour> temp = new ArrayList();
        
        for (String k : depdic.keySet()) {
            temp.add(depdic.get(k));
        }
        return temp;
    }
    public static ArrayList<Contour> getList(String nomregion) {
        ArrayList<Contour> temp = new ArrayList();
        
        for (String code : catalog.get(Region.getContour(nomregion).getCode())) {
            temp.add(depdic.get(codeVersString.get(code)));
        }
        return temp;
    }

}
