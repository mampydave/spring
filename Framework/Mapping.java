package mg.itu.prom16.etu2564;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.*;


public class Mapping {
    private String className;
    private String annotateType;
    private String methodName;
    private Object valeur;
    private boolean estRestapi;
    private List<String> nbparam;
    
    public Mapping(String className, String methodName,Object valeur) {
        this.className = className;
        this.methodName = methodName;
        this.valeur = valeur;
    }

    public Mapping() {
    }
    


    public Mapping(String className, String annotateType, String methodName, Object valeur, boolean estRestapi) {
        this.className = className;
        this.annotateType = annotateType;
        this.methodName = methodName;
        this.valeur = valeur;
        this.estRestapi = estRestapi;
    }

    public Mapping(String className, String annotateType, String methodName, List<String> nbparam, boolean estRestapi) {
        this.className = className;
        this.annotateType = annotateType;
        this.methodName = methodName;
        this.nbparam = nbparam;
        this.estRestapi = estRestapi;
    }
    
    public Mapping(String className, String methodName, Object valeur, boolean estRestapi) {
        this.className = className;
        this.methodName = methodName;
        this.valeur = valeur;
        this.estRestapi = estRestapi;
    }

    
    public Mapping(String className, String methodName, List<String> nbparam, boolean estRestapi) {
        this.className = className;
        this.methodName = methodName;
        this.nbparam = nbparam;
        this.estRestapi = estRestapi;
    }

    public Mapping(String className, String methodName, List<String> nbparam) {
        this.className = className;
        this.methodName = methodName;
        this.nbparam = nbparam;
    }

    public Mapping(String className, String methodName, Object valeur, List<String> nbparam) {
        this.className = className;
        this.methodName = methodName;
        this.valeur = valeur;
        this.nbparam = nbparam;
    }

    public List<String> getNbparam() {
        return nbparam;
    }



    public void setNbparam(List<String> nbparam) {
        this.nbparam = nbparam;
    }


    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }


    public String getAnnotateType() {
        return annotateType;
    }

    public void setAnnotateType(String annotateType) {
        this.annotateType = annotateType;
    }

    public Object getValeur() {
        return valeur;
    }

    public boolean isEstRestapi() {
        return estRestapi;
    }

    public void setEstRestapi(boolean estRestapi) {
        this.estRestapi = estRestapi;
    }

    public Object getDefaultValue(Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return 0;  
        } else if (type == boolean.class || type == Boolean.class) {
            return false;  
        }
         else {
            return null;  
        }
    }

}