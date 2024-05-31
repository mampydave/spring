package mg.itu.prom16.etu2564;

import java.lang.reflect.Method;

public class Mapping {
    private String className;
    private String methodName;
    private String valeur;

    public Mapping(String className, String methodName,String valeur) {
        this.className = className;
        this.methodName = methodName;
        this.valeur = valeur;
    }

    public String getValeur() {
        return valeur;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }
}