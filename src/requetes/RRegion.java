package requetes;

import java.io.IOException;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import Main.Contour;
import Donne.Departement;
import Donne.Stat;
import Main.Fenetre;
import Donne.Region;
import static Main.Fenetre.pix;
import Main.Utils;
import java.net.MalformedURLException;
import javafx.scene.transform.Scale;

public class RRegion extends Requete {
    
    public RRegion(String stat, String zone) throws MalformedURLException, IOException {
        super();
        this.stat = stat;
        this.zone = Utils.simplify(zone);
        this.donnees = new Stat(stat, "REG", Region.stringVersCode.get(this.zone));
        this.groupe = new Group();
    }
    
    @Override
    public void executer() {
        Contour forme = Region.getContour(zone);
        
        //je réinitialise les éventuelles modifications de scaling faites dans des requêtes précédentes
        forme.getForme().getTransforms().clear();
        forme.getForme().setStrokeWidth(1.0);
        groupe.getTransforms().clear();
        groupe.getChildren().clear();
        
        Fenetre.sp.getChildren().clear();
        double centrey = forme.getCentre().getY();
        double centrex = forme.getCentre().getX();
        double scale = 1.5*pix/(forme.getMaxLong());
        

        for (Contour departement : Departement.getList(zone)) {
            groupe.getChildren().add(departement.getForme());
            departement.getForme().setFill(donnees.getCouleur(departement.getCode()));
            departement.getForme().setStroke(Color.GREY);
            departement.getForme().setStrokeWidth(pix/(scale*700));
            
            // je défini les évènements à exécuter lors que l'on passe sur la zone avec la souris
            departement.getForme().setOnMouseEntered(e -> {
                departement.getForme().setStroke(Color.BLACK);
                Fenetre.nomzone.setText(departement.getNom());
                Fenetre.valzone.setText(Math.floor(100.0 * donnees.getTaux(departement.getCode())) + "%");
                
            });
            departement.getForme().setOnMouseExited(e -> {
                departement.getForme().setStroke(Color.GREY);
            });
        }
        
            Scale formescale = new Scale();
            formescale.setX(scale);
            formescale.setY(-scale);
            formescale.setPivotX(forme.getCentre().getX());
            formescale.setPivotY(forme.getCentre().getY());
            
            Fenetre.sp.getChildren().add(groupe);
            groupe.getTransforms().addAll(formescale);
    }    
}   
