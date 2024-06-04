package mg.itu.prom16.etu2564;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
// import mg.itu.util.Mapping;

import java.lang.reflect.*;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.RequestingUserName;


public class FrontController extends HttpServlet { 
    Map<String, Mapping> hmap;

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
                        Object pris=trouver.getDeclaredConstructor().newInstance();
                        Get annotation = method.getAnnotation(Get.class);
                        String url = annotation.value();
                        Mapping truest = new Mapping(trouver.getName(), method.getName(),method.invoke(pris));
                    
                        hmap.put(url, truest);
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

                Class<?> newc=Class.forName(mapping.getClassName());
                Object controller=newc.getDeclaredConstructor().newInstance();
                Method method=controller.getClass().getDeclaredMethod(mapping.getMethodName());
                
                out.println("<h1>URL: " + requestUrl + "</h1>");

                out.println("<li>Class: " + mapping.getClassName() + ":");
                out.println("<ul>Method: " + mapping.getMethodName() + "</ul>");

                Object result = method.invoke(controller);

                if (result instanceof String) {
                    out.println("<ul>Value of method: " + (String) result+ "</ul></li>");                                        
                } else if (result instanceof ModelView) {
                    ModelView model=(ModelView) result;
                    String url=model.getUrl();
                    HashMap<String, Object> data=model.getData();
                    for(String key : data.keySet()){
                        request.setAttribute(key,data.get(key));
                    }
                    RequestDispatcher dispatcher = request.getRequestDispatcher(url);
                    dispatcher.forward(request, response);
                }else{
                    out.println("Non reconnu");
                }                

            } else {
                out.println("<h1>THE URL : " + requestUrl + " NOT EXIST</h1>");
            }
            out.println("</body>");
            out.println("</html>");
        }catch (Exception e) {
            System.out.println(e);
        }
         finally {
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
                                if (annotation.value()!=" ") {
                                    liste.add(chemin+"."+nomClasse );
                                    //liste.add("Controller: " +annotation.value());
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
            
        }catch(Exception e){

            throw e;

        }
        return liste;
        
    }
}