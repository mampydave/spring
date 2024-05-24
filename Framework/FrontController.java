package mg.itu.prom16.etu2564;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class FrontController extends HttpServlet { 
    private Map<String, Mapping> hmap;

    @Override
    public void init() throws ServletException {
        hmap = new HashMap<>();
        try {
            ServletContext context = getServletContext();
            String chemin = context.getInitParameter("scan");
            List<String> controllers = scan(chemin); 
            for (String controller : controllers) {
                Class<?> trouver = Class.forName(controller);
                Method[] methods = trouver.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Get.class)) {
                        Get annotation = method.getAnnotation(Get.class);
                        String url = annotation.value();
                        hmap.put(url, new Mapping(trouver.getName(), method.getName()));
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'initialisation du FrontController", e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String requestUrl = request.getRequestURI().substring(request.getContextPath().length());
            Mapping mapping = hmap.get(requestUrl);
            out.println("<html>");
            out.println("<head><title>Sprint2</title></head>");
            out.println("<body>");
            if (mapping != null) {
                out.println("<h1>URL: " + requestUrl + "</h1>");
                out.println("<p>Class: " + mapping.getClassName() + "</p>");
                out.println("<p>Method: " + mapping.getMethodName() + "</p>");
            } else {
                out.println("<h1>THE URL : " + requestUrl + " NOT EXIST</h1>");
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
        
        try {
            // PrintWriter out = response.getWriter();
            processRequest(request, response);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            processRequest(request, response);

        } catch (Exception e) {
            System.out.println(e);
        }
        
    }

    private List<String> scan(String chemin) throws Exception {
        List<String> liste = new ArrayList<>();
        try {
            String cheminRepertoire = chemin.replace('.', '/');
            URL urPackage = Thread.currentThread().getContextClassLoader().getResource(cheminRepertoire);
            if (urPackage != null) {
                File directory = new File(urPackage.getFile());
                File[] fichiers = directory.listFiles();
                if (fichiers != null) {
                    for (File fichier : fichiers) {
                        if (fichier.isFile() && fichier.getName().endsWith(".class")) {
                            String nomClasse = fichier.getName().substring(0, fichier.getName().length() - 6);
                            String nomCompletClasse = chemin + "." + nomClasse;
                            liste.add(nomCompletClasse);
                        } else if (fichier.isDirectory()) {
                            List<String> li = scan(chemin + "." + fichier.getName());
                            liste.addAll(li);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return liste;
    }
 

}