package mg.itu.prom16.etu2564;

import java.util.*;

public class VerbAction {
    private String annotateType;
    private String methodName;
    private Object valeur;
    private boolean estRestapi;
    private List<String> nbparam;
    private List<String> roles;

    @Override
    public boolean equals(Object obj) {
        // Vérifie si l'objet est de la même classe
        if (this == obj) return true;
        if (!(obj instanceof VerbAction)) return false;
        // Vérifie si les noms d'action sont identiques
        VerbAction that = (VerbAction) obj;
        return annotateType.equals(that.annotateType);
    }

    @Override
    public int hashCode() {
        // Utilise le nom d'action pour le code de hachage
        return annotateType.hashCode();
    }

    public VerbAction() {
    }
    public VerbAction(String annotateType, String methodName, List<String> nbparam, boolean estRestapi) {
        this.annotateType = annotateType;
        this.methodName = methodName;
        this.estRestapi = estRestapi;
        this.nbparam = nbparam;
    }
    public VerbAction(String annotateType, String methodName, Object valeur, boolean estRestapi) {
        this.annotateType = annotateType;
        this.methodName = methodName;
        this.valeur = valeur;
        this.estRestapi = estRestapi;
    }
    public VerbAction(String annotateType, String methodName, Object valeur, boolean estRestapi,List<String> roles) {
        this.annotateType = annotateType;
        this.methodName = methodName;
        this.valeur = valeur;
        this.estRestapi = estRestapi;
        this.roles = roles;
    }
    public VerbAction(String annotateType, String methodName, List<String> nbparam, boolean estRestapi,List<String> roles) {
        this.annotateType = annotateType;
        this.methodName = methodName;
        this.nbparam = nbparam;
        this.estRestapi = estRestapi;
        this.roles = roles;
    }
    public String getAnnotateType() {
        return annotateType;
    }
    public void setAnnotateType(String annotateType) {
        this.annotateType = annotateType;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public Object getValeur() {
        return valeur;
    }
    public void setValeur(Object valeur) {
        this.valeur = valeur;
    }
    public boolean isEstRestapi() {
        return estRestapi;
    }
    public void setEstRestapi(boolean estRestapi) {
        this.estRestapi = estRestapi;
    }
    public List<String> getNbparam() {
        return nbparam;
    }
    public void setNbparam(List<String> nbparam) {
        this.nbparam = nbparam;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    
}
