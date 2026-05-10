package requetes;

import Donne.Commune;
import Donne.Departement;
import Donne.Stat;
import Main.Contour;
import Main.Fenetre;
import Main.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

import java.io.IOException;

import static Main.Fenetre.pix;

public class RDepartement extends Requete {
    
    public RDepartement(String stat, String zone) throws IOException {
        super();
        this.stat = stat;
        this.zone = Utils.simplify(zone);
        this.donnees = new Stat(stat, "DEP", Departement.stringVersCode.get(this.zone));
        this.groupe = new Group();

    }
    
    @Override
    public void executer() {
        Contour forme = Departement.getContour(zone);
        
        //je réinitialise les éventuelles modifications de scaling faites dans des requêtes précédentes
        forme.getForme().getTransforms().clear();
        groupe.getTransforms().clear();
        groupe.getChildren().clear();
        
        for (Contour commune : Commune.getList(zone)) {
            commune.getForme().setStrokeWidth(0.01);
            commune.getForme().getTransforms().clear();
            groupe.getChildren().add(commune.getForme());
            
        }

        groupe.layout();

        Bounds bound = groupe.getLayoutBounds();
        double a = bound.getWidth();
        double b = bound.getHeight();
        double maxlong = Math.max(b, a);


        Fenetre.sp.getChildren().clear();
        double centrey = forme.getCentre().getY();
        double centrex = forme.getCentre().getX();
        double scale = pix/(maxlong*1.05);
        
        for (Contour commune : Commune.getList(zone)) {
            //groupe.getChildren().add(commune.getForme());
            commune.getForme().setFill(donnees.getCouleur(commune.getCode()));
            commune.getForme().setStroke(Color.GREY);
            commune.getForme().setStrokeWidth(pix/(scale*700));
            
            // je définis les évènements à exécuter lorsque l'on passe sur la zone avec la souris
            commune.getForme().setOnMouseEntered(e -> {
                commune.getForme().setStroke(Color.BLACK);
                Fenetre.nomzone.setText(commune.getNom());
                Fenetre.valzone.setText(Math.floor(100.0 * donnees.getTaux(commune.getCode())) + "%");
                
            });
            commune.getForme().setOnMouseExited(e -> {
                commune.getForme().setStroke(Color.GREY);
            });
        }
        
        Scale formescale = new Scale();
        formescale.setX(scale);
        formescale.setY(-scale);
        formescale.setPivotX(forme.getCentre().getX());
        formescale.setPivotY(forme.getCentre().getY());
        groupe.getTransforms().addAll(formescale);
        
        Fenetre.sp.getChildren().add(groupe);

        
        
    }
    
}
