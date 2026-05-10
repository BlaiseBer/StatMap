package requetes;

import Donne.Region;
import Donne.Stat;
import Main.Contour;
import Main.Fenetre;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

import java.io.IOException;

import static Main.Fenetre.pix;

public class RNation extends Requete {
    public RNation() {
        super();
        this.stat = null;
        this.groupe = new Group();
    }
    
    public RNation(String stat) throws IOException {
        super();
        this.stat = stat;
        this.donnees = new Stat(stat, "NAT", null);
        this.groupe = new Group();
    }
     
    @Override
    public void executer() {
        Fenetre.sp.getChildren().clear();
        double centrey = 46.763060*1.4;
        double centrex = 2;
        double scale = pix/14.;
        
        groupe.getTransforms().clear();
        groupe.getChildren().clear();
        
        for (Contour region : Region.getList()) {
            double rcentrex = region.getCentre().getX();
            double rcentrey = region.getCentre().getY();
            if (region.getNom().equals("Corse")) {
                    region.getForme().setTranslateX(7.341015); 
                    region.getForme().setTranslateY(42.505012);
            }
            groupe.getChildren().add(region.getForme());        
            region.getForme().setStrokeWidth(pix/(scale*1400));
            region.getForme().setStroke(Color.GREY);
                            
            //stat peut être nulle uniquement au lancement du programme, dans ce cas les régions sont simplement remplis en blanc
            if (stat==null) {
                region.getForme().setFill(Color.WHITE);
                
            //dans le reste des cas, on colorie selon les informations données par l'objet donnees
            } else {
                region.getForme().setFill(donnees.getCouleur(region.getCode()));
            }
           
             // je définis les évènements à exécuter lors que l'on passe sur la region avec la souris
            region.getForme().setOnMouseEntered(e -> {
                region.getForme().setStroke(Color.BLACK);
                Fenetre.nomzone.setText(region.getNom());
                if (stat != null) {
                    Fenetre.valzone.setText(Math.floor(100.0 * donnees.getTaux(region.getCode())) + "%");
                }
            });
            region.getForme().setOnMouseExited(e -> {
                region.getForme().setStroke(Color.GREY);
            });
            
        }
        
        Scale formescale = new Scale();
        formescale.setX(scale);
        formescale.setY(-scale);
        formescale.setPivotX(centrex);
        formescale.setPivotY(centrey);
        
        groupe.getTransforms().addAll(formescale);
        Fenetre.sp.getChildren().add(groupe);
    }
}