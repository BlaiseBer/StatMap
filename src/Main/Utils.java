/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import java.text.Normalizer;

/**
 *
 * @author blaise
 */
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
