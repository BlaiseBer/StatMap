package Donne;

import Main.Fenetre;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.scene.paint.Color;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.util.Hashtable;


public class Stat {
    String stat;
    String zone;
    String codezone;
    Hashtable<String, Double> taux = new Hashtable();
    Hashtable<String, Double> coef = new Hashtable();
    
    private JsonNode getJson(String surl) throws MalformedURLException, IOException{
        
        URL url = new URL(surl);
        JsonNode donnee = null;

        HttpURLConnection conn = (HttpURLConnection)
        url.openConnection();
            
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
            "Accept", "application/json");
        
        InputStream is = conn.getInputStream();
        
        ObjectMapper mapper = new ObjectMapper();
        donnee = mapper.readTree(is);
        
        is.close();
        return donnee;
    }
        
    
    
    public Stat(String stat, String zone, String codezone) throws MalformedURLException, IOException {
        //def variables de base
        this.stat = stat;
        this.zone = zone;
        this.codezone = codezone;
        
        
        //On choisit le(s) bon(s) url pour la requête
        switch (stat) {
            // je gère les différentes stats
            case "Chomage" -> {
                String urlChomage = "";
                String urlPopActive = "";
                
                //selon la zone recherchée, la requête n'est pas la même
                switch (zone) {
                    case "DEP" -> {    
                        urlChomage = "https://api.insee.fr/melodi/data/DS_RP_EMPLOI_LR_PRINC?TIME_PERIOD=2022&SEX=_T&EMPSTA_ENQ=2&AGE=Y_GE15&EDUC=_T&GEO=2025-DEP-" + codezone + "*COM";
                        urlPopActive = "https://api.insee.fr/melodi/data/DS_RP_EMPLOI_LR_COMP?EMPSTA_ENQ=1T2&TIME_PERIOD=2022&PCS=_T&GEO=2025-DEP-"+ codezone +"*COM";
                    }
                    
                    case "REG" -> {
                        urlChomage = "https://api.insee.fr/melodi/data/DS_RP_EMPLOI_LR_PRINC?EMPSTA_ENQ=2&SEX=_T&TIME_PERIOD=2022&AGE=Y_GE15&GEO=2025-REG-" + codezone + "*DEP";
                        urlPopActive = "https://api.insee.fr/melodi/data/DS_RP_EMPLOI_LR_COMP?EMPSTA_ENQ=1T2&TIME_PERIOD=2022&PCS=_T&GEO=2025-REG-"+ codezone +"*DEP";
                    }
                    
                    case "NAT" -> {
                        urlChomage = "https://api.insee.fr/melodi/data/DS_RP_EMPLOI_LR_PRINC?TIME_PERIOD=2022&SEX=_T&EMPSTA_ENQ=2&AGE=Y15T64&AGE=Y_GE15&EDUC=_T&GEO=2025-FRANCE-F&GEO=REG";
                        urlPopActive = "https://api.insee.fr/melodi/data/DS_RP_EMPLOI_LR_PRINC?TIME_PERIOD=2022&SEX=_T&EMPSTA_ENQ=1&AGE=Y15T64&AGE=Y_GE15&EDUC=_T&GEO=2025-FRANCE-F&GEO=REG";
                    }
                }
                
                
                //les listes des valeurs de ces deux JSonNode ne sont pas dans le même ordre, pour pouvoir accéder
                //à une même commune dans les deux, je dois passer par un dictionnaire intermédiaire
                JsonNode jsonChomage = this.getJson(urlChomage);
                JsonNode jsonPopActive = this.getJson(urlPopActive);
                
                //Je crée un dictionaire pour accéder aux données de la population à partir du code de commune rapidement
                Hashtable<String, Double> PopActive = new Hashtable();
                    for (JsonNode commune : jsonPopActive.get("observations")) {
                        //je récupère l'identifiant utilisé par les données de l'insee
                        String cléTot = commune.get("dimensions").get("GEO").toString();
                        
                        //seule la partie avec le code INSEE (le code Région, Département, ou commune) est utile ici
                        //je coupe le reste
                        String clé = cléTot.substring(10, cléTot.length()-1);
                        
                        // je récupère la donnée observée
                        Double  value = commune.get("measures").get("OBS_VALUE_NIVEAU").get("value").asDouble();
                        PopActive.put(clé, value);
                    }
                    
                    for (JsonNode commune : jsonChomage.get("observations")) {
                        //je récupère l'identifiant utilisé par les données de l'insee
                        String cléTot = commune.get("dimensions").get("GEO").toString();
                        
                        //seule la partie avec le code INSEE (le code Région, Département, ou commune) est utile ici
                        //je coupe le reste
                        String clé = cléTot.substring(10, cléTot.length()-1);
                        
                        // je récupère la donnée observée
                        Double  nbChomeur = commune.get("measures").get("OBS_VALUE_NIVEAU").get("value").asDouble();
                        //je calcule la pop active
                        if (PopActive.get(clé) != null && PopActive.get(clé) != 0) {
                                taux.put(clé, nbChomeur/PopActive.get(clé));
                    } else {
                            taux.put(clé, -1.0);
                        }
                    }
                
            }
        }
        
        //calcul du maximum et du minimum pour normaliser les proportions
        double max = 0;
        double min = 1;
        for (String commune : taux.keySet()) {
            if (max<=taux.get(commune) && taux.get(commune) !=-1) {
                max = taux.get(commune);
            }
            if (min>=taux.get(commune) && taux.get(commune) != -1) {
                min = taux.get(commune);
            }
        }
        
        //Normalisation (de 0.3 à 0.9 pour  éviter que certaines zones soient trop sombres ou trop claires)
        for (String commune : taux.keySet()) {
            if (taux.get(commune) != -1.0 && taux.get(commune) != null) {
                coef.put(commune, 0.9 + (taux.get(commune)-min) * ((0.3-0.9)/(max-min)));
            } else { coef.put(commune, -1.0);}
        }
    }
    
    public double getTaux(String codeSousZone) {
        if (taux.get(codeSousZone) == -1.0) {
            return -1.0;
        } else {
            return taux.get(codeSousZone);
        }
    }
    
    public Color getCouleur(String codeSousZone) {
        
        //je gère d'abord la situation on la valeur n'a pas été évaluée pour une zone
        if ((coef.get(codeSousZone) == null) || (coef.get(codeSousZone) == -1.0)) {
            //la couleur pour les zones pour lesquels il n'y a aucune données est grise
            return Color.LIGHTGREY;
        
        //Puis la situation généale
        } else {
        
            double blue = Fenetre.getCouleur().getBlue();
            double red = Fenetre.getCouleur().getRed();
            double green = Fenetre.getCouleur().getGreen();
            double t = coef.get(codeSousZone);
            return Color.color(red*t, green*t, blue*t);
        }
    }
}