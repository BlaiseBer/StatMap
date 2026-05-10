package Main;

import Donne.Commune;
import Donne.Departement;
import Donne.Region;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import requetes.RDepartement;
import requetes.RNation;
import requetes.RRegion;
import requetes.Requete;

import java.awt.*;
import java.io.IOException;

public class Fenetre {
    public static int pix;
    public static int piy;
    public static StackPane sp;
    private static final Color color = Color.LIGHTBLUE;
    public Requete requete;
    
    //importation de l'image de la fenêtre
    Image icon = new Image(getClass().getResourceAsStream("/icon/logo.png"));
    
    //Création de deux labels pour afficher le nom et la valeur de la zone pointée. Ils devront être accédé depuis une autre classe
    public static Label nomzone = new Label("");
    public static Label valzone = new Label("");
    
    public static Color getCouleur() {
        return color;
    }
    
    public Fenetre(Stage stage) {

        //Initialisation des contours de chaque zone géographique
        try {
            Departement.init();
            Commune.init();
            Region.init();

        } catch (IOException ex) {
            System.getLogger(Main.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("IOException");
            alert.setContentText("Documents hors connection introuvables");

            alert.showAndWait();
            }

        //calcul du nombre de pixels de hauteur et de largeur nécessaire (le programme s'assure que la fenêtre tient bien sur l'écran de l'utilisateur).
        Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
        if (ssize.getHeight()>= ssize.getWidth()) {
            piy = (int) ssize.getWidth(); 
            pix = (piy*2)/3;
        } else {
            pix = (int) ssize.getHeight()*2/3;
            piy = (int) ssize.getHeight();
        }
        
        //  Définition des boutons et de leurs labels respectifs pour la VBox(à droite)
        //    Définition du menu défilant
        Label ldef = new Label("Choisir la statistique à afficher :");
        
        ChoiceBox cb = new ChoiceBox();
        cb.getItems().addAll("Chomage", "test1", "test2");
        cb.getSelectionModel().select(0);
        
        //    Définition du bouton de choix de la région
        Label lreg = new Label("Choix de l'échelle :");
        
        RadioButton rb1 = new RadioButton("National");
        RadioButton rb2 = new RadioButton("Régional");
        RadioButton rb3 = new RadioButton("Départemental");
        ToggleGroup g = new ToggleGroup();
        rb1.setToggleGroup(g);
        rb2.setToggleGroup(g);
        rb3.setToggleGroup(g);
        rb1.setSelected(true);
        
        //je crée une VBox pour ranger les 3 RadioButton
        VBox vbradio = new VBox();
        vbradio.getChildren().addAll(rb1,rb2,rb3);
        
        //    Définition De la zone de texte pour la région
        Label lzone = new Label("Entrer le nom de la zone géographique");
        lzone.setVisible(false);
        
        TextField tf = new TextField();
        tf.setMaxWidth(100);
        tf.setVisible(false);
        
        
        // Je fais en sorte qu'il dissparaisse quand Nation est séléctionn é
        rb1.setOnAction((ActionEvent e) -> {
            lzone.setVisible(false); tf.setVisible(false);
        });
        rb2.setOnAction((ActionEvent e) -> {
            lzone.setVisible(true); tf.setVisible(true);
        });
        rb3.setOnAction((ActionEvent e) -> {
            lzone.setVisible(true); tf.setVisible(true);
        });
        
        //    Définition du bouton validé
        Button valid = new Button("Valider");
        valid.setOnAction(
                e -> {
                    try {
                        // je dissocie chaque cas (en fonction de la case cochée).
                        if (rb1.isSelected()) {
                            requete = new RNation(cb.getValue().toString());
                            requete.executer();
                        } else if (rb2.isSelected()) {
                            requete = new RRegion(cb.getValue().toString(), tf.getText());
                            requete.executer();

                        } else {
                            requete = new RDepartement(cb.getValue().toString(), tf.getText());
                            requete.executer();
                        }

                    } catch (Exception d) {
                        d.printStackTrace(System.out);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Nom non reconnu");
                        alert.setContentText("Le nom de la zone géographique n'a pas été reconnu. Vérifier que vous n'avez pas fait d'erreur de frappes et vérifier votre connection internet.");
                        alert.showAndWait();


                    }
                }
        );
                
        //def de la VBox (à droite)
        VBox vb = new VBox(5);
        vb.setAlignment(Pos.CENTER);
        vb.setBorder( new Border(new BorderStroke(Color.GREY,BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))));
        vb.getChildren().addAll(ldef, cb, lreg, vbradio, lzone, tf, valid, nomzone, valzone); //ajout des Nodes
        VBox.setMargin(vbradio, new Insets(1, 0, 1, pix/5.));
        vb.setPrefSize(pix/2., pix);
        
        
        //def StackPane pour la carte
        StackPane sp = new StackPane();
        sp.setPrefSize(pix, pix);
        Fenetre.sp = sp;
        
        //def du HBox (root)
        HBox root =new HBox();
        root.getChildren().addAll(sp,vb);
         
         //def de la scène
        Scene scene=new Scene(root, piy, pix);
        stage.setScene(scene);
        //paramètres de la fenêtre
        stage.setResizable(false); //Les dimensions de la fenêtre doivent être conservée.
        stage.setTitle("MapStat");
        stage.getIcons().add(icon);
        
            
        //definition et execution de la première requête (qui affiche la carte d'accueil)
        requete = new RNation();
        requete.executer();
        
        stage.show();
    }
}
