package Main;

import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;


public class Contour {
    private final String code;
    private final String nom;
    private javafx.scene.shape.Shape forme;
    private Point2D centre;
    public static double maxlong;
    
    public Contour(Feature feature) {
        GeoJsonObject geometry = feature.getGeometry();
        javafx.scene.shape.Shape sforme = null; //sforme sera le contour converti final
                
            
        if (geometry instanceof Polygon) {
            // Un polygon est une liste de liste de points.
            // Le premier élément de la liste forme le Polygone extérieur.
            // Les éléments suivants forment les "trous" c'est-à-dire les polygones qui doivent être soustraits au polygone extérieur
                

            Polygon polygon = (Polygon) geometry;
            List<List<LngLatAlt>> rings = polygon.getCoordinates(); // rings est la liste des polygones
                
            //j'initialise sforme avec le polygone extérieur
            sforme = convertir(rings.getFirst());
                
            for (List<LngLatAlt> poly : rings) {
                if (!poly.equals(rings.getFirst())) {
                    //je soustrais chaque polygone successif à sforme
                    sforme = Shape.subtract(sforme, convertir(poly));
                }
            }
                
        //sforme est maintenant une Shape javafx qui décrit la même forme que le polygon de geojson précédent
        // j'ai plus qu'à la ranger dans this.forme
                
        } else {
            MultiPolygon multipolygon = (MultiPolygon) geometry;
            //MultiPolygon est simplement une liste de Polyone (comme définit précédemment) q'il suffit d'ajouter par union
            // On répète donc les mêmes opérations sur toute la liste
                
            for (List<List<LngLatAlt>> rings : multipolygon.getCoordinates())  {
                forme = convertir(rings.getFirst());
                for (List<LngLatAlt> poly : rings) {
                    if (!poly.equals(rings.getFirst())) {
                        forme = Shape.subtract(forme, convertir(poly));
                    }
                }
                if (sforme == null) {
                    sforme = forme;
                } else {
                    //on fait l'union de chacun de ces polygones reconstruits 
                    sforme = Shape.union(sforme, forme);
                }
            }
        }
        
        this.nom = feature.getProperty("nom").toString();
        this.code = feature.getProperty("code");
        this.forme = sforme;
        Bounds bounds = forme.getBoundsInLocal();
        this.centre = new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
        this.maxlong = this.getMaxLong();
    }
    
    
    //cette méthode permet de convertir une liste de LngLatAlt en une shape javafx
    private static javafx.scene.shape.Shape convertir(List<LngLatAlt> coords) {
        javafx.scene.shape.Polygon temp = new javafx.scene.shape.Polygon();
        
        for (LngLatAlt coord : coords) {
            temp.getPoints().addAll(coord.getLongitude(), coord.getLatitude()*1.4);
        }
        
        return temp;
    }
    
    public String getNom() {
        return nom;
    }
    public String getCode() {
        return code;
    }
    
    //il est nécessaire que cette méthode renvoie une copie de la forme du contour, sinon elle pourra être modifiée au cours du programme.
    public javafx.scene.shape.Shape getForme() {
        return forme;
    }
    public Point2D getCentre() {
        return centre;
    }
    public void setCentre(double x, double y) {
        this.centre= new Point2D(x,y);
    }
    public double getMaxLong() {
        //this.getForme().getTransforms().clear();
        Bounds bound = forme.getLayoutBounds();
        double a = bound.getWidth();
        double b = bound.getHeight();
        return Math.max(b, a);
    }
}
