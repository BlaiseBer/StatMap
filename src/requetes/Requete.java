package requetes;

import Donne.Stat;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

public abstract class Requete {
    String stat;
    String zone;
    Stat donnees;
    Group groupe;
    
    Requete() {}
    
    public void executer() throws Exception {}
}
