package mg.itu.prom16.etu2564;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class ValidationResult {
    private Map<String, List<String>> errors = new HashMap<>();
    private Map<String, String> fieldValues = new HashMap<>();

    public void addError(String field, String message) {
        errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
    }

    public void addFieldValue(String field, String value) {
        fieldValues.put(field, value);
    }

    // Vérifier s'il y a des erreurs
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    // Obtenir les erreurs pour un champ
    public List<String> getFieldErrors(String field) {
        return errors.getOrDefault(field, new ArrayList<>());
    }

    // Obtenir la valeur d'un champ
    public String getFieldValue(String field) {
        return fieldValues.get(field);
    }

    // Sauvegarder dans la session
    public void saveToSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("validationErrors", errors);
        session.setAttribute("fieldValues", fieldValues);
    }

    // Récupérer de la session
    public static ValidationResult loadFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ValidationResult result = new ValidationResult();
        
        @SuppressWarnings("unchecked")
        Map<String, List<String>> sessionErrors = 
            (Map<String, List<String>>) session.getAttribute("validationErrors");
        @SuppressWarnings("unchecked")
        Map<String, String> sessionValues = 
            (Map<String, String>) session.getAttribute("fieldValues");
        
        if (sessionErrors != null) {
            result.errors = sessionErrors;
            session.removeAttribute("validationErrors");
        }
        
        if (sessionValues != null) {
            result.fieldValues = sessionValues;
            session.removeAttribute("fieldValues");
        }
        
        return result;
    }
}
