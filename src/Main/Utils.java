package Main;

import java.text.Normalizer;

public abstract class Utils {
    
    static public String simplify(String texte) {
    if (texte == null) {
        return null;
    }
    
    //je supprime les accents 
    final String decomposed = Normalizer.normalize(texte, Normalizer.Form.NFD);
    texte = decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    
    // je transforme les espaces en tirets
    char a = ' ';
    char b = '-';
    String texte2 = texte.replace(a,b);
    
    //je renvoie le texte tout en minuscule
    return texte2.toLowerCase();
    }
}
