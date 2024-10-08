package mg.itu.prom16.etu2564;

import java.util.*;

public class VerbAction {
    private String annotateType;
    private String methodName;
    private Object valeur;
    private boolean estRestapi;
    private List<String> nbparam;

    
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

    
}
