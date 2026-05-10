package requetes;

import Donne.Stat;
import javafx.scene.Group;

public abstract class Requete {
    String stat;
    String zone;
    Stat donnees;
    Group groupe;
    
    Requete() {}
    
    public void executer() {}
}
