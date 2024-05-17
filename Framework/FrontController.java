package mg.itu.prom16.etu2564;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

public class FrontController extends HttpServlet { 

    private boolean test = false;
    List<String>valiny; 
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
    public void processRequest(HttpServletRequest request, HttpServletResponse response)throws Exception {
        
        PrintWriter out = response.getWriter();
        ServletContext context = getServletContext();
        String chemin = context.getInitParameter("scan");
                
        
        // out.println("icci c'est vrai");

        if (!test) {
            valiny = scan(chemin);
            // out.println("mbol eto");
        }
        for (String string : valiny) {
            out.println(string);
        }
    }
    public List<String> scan(String chemin)throws Exception{
        List<String> liste = new ArrayList<String>();
        try 
        {
            String cheminRepertoire = chemin.replace('.','/');
            URL urPackage = Thread.currentThread().getContextClassLoader().getResource(cheminRepertoire);
            
            if (urPackage != null) {
                File directory = new File(urPackage.getFile());
                File[] fichiers = directory.listFiles();
                if (fichiers != null) {
                    for (File fichier : fichiers) {
                        if (fichier.isFile() && fichier.getName().endsWith(".class")) {
                            String nomClasse = fichier.getName().substring(0, fichier.getName().length() - 6);
                            String nomCompletClasse = chemin + "." + nomClasse;
                            // liste.add(nomCompletClasse);
                            Class class1 = Class.forName(nomCompletClasse);
                            
                            if (class1.isAnnotationPresent(ControllerAnnotation.class)) {
                                ControllerAnnotation annotation = (ControllerAnnotation) class1.getAnnotation(ControllerAnnotation.class);
                                if (annotation.value().equalsIgnoreCase("Controlleur")) {
                                    liste.add("Controller:  "+nomClasse);
                                    // System.out.println(liste);
                                }
                                // else{
                                //     liste.add("tsy tafiditra");
                                // }
                            } 
                        }
                        else if(fichier.isDirectory()){
                            List<String> li =  scan(cheminRepertoire + "." + fichier.getName());
                            // liste.addAll(li);
                        }
                    }
                }
            }
            test = true;
            
        }
        catch(Exception e){

            throw e;

        }
        return liste;
        
    }
}