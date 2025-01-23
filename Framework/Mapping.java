package mg.itu.prom16.etu2564;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.*;



public class Mapping {
    private String className;
    private Set<VerbAction> verbActions;    
    
    public Mapping() {
    }

    public Mapping(String className, Set<VerbAction> verbActions) {
        this.className = className;
        this.verbActions = verbActions;
    }

    

    public Set<VerbAction> getVerbActions() {
        return verbActions;
    }


    public void setVerbActions(Set<VerbAction> verbActions) {
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

    public static Field trouverChamp(String nomField, Field[] listAttribut) {
        for (Field field : listAttribut) {
            if (field.getName().equalsIgnoreCase(nomField)) {
                return field; 
            }
        }
        return null; 
    }

    public static Number convertToNumber(String paramValue) throws Exception {
        try {
            return Integer.parseInt(paramValue);
        } catch (NumberFormatException e1) {
            try {
                
                return Double.parseDouble(paramValue);
            } catch (NumberFormatException e2) {
                throw new Exception("La valeur '" + paramValue + "' n'est pas un nombre valide.");
            }
        }
    }

    
    public static boolean isValidDate(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);  
            sdf.parse(dateStr);     
            return true;
        } catch (ParseException e) {
            return false;  
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        
        if (email == null) {
            return false;
        }
        
        return email.matches(emailRegex);
    }
    

}