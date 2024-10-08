package mg.itu.prom16.etu2564;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.*;



public class Mapping {
    private String className;
    private List<VerbAction> verbActions;
    
    
    public Mapping() {
    }

    public Mapping(String className, List<VerbAction> verbActions) {
        this.className = className;
        this.verbActions = verbActions;
    }

    

    public List<VerbAction> getVerbActions() {
        return verbActions;
    }


    public void setVerbActions(List<VerbAction> verbActions) {
        this.verbActions = verbActions;
    }



    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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