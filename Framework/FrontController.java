package mg.itu.prom16.etu2564;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
// import mg.itu.util.Mapping;

import java.lang.ModuleLayer.Controller;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.RequestingUserName;


public class FrontController extends HttpServlet { 
    Map<String, Mapping> hmap;
    Map<String, List<String>> nbParam; 


    @Override
    public void init() throws ServletException {

        hmap = new HashMap<>();
        nbParam = new HashMap<>();
        try {
            ServletContext context = getServletContext();
            String chemin = context.getInitParameter("scan");
        
            List<String> controllers = scan(chemin); 
            boolean GetMethodPresent = false; // Flag pour vérifier les méthodes @GET
            for (String controller : controllers) {
                Class<?> trouver = Class.forName(controller);
                Method[] methods = trouver.getDeclaredMethods();
                for (Method method : methods) {   
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Parameter[] parameters = method.getParameters();
                    List<String> paramNames = new ArrayList<>();
                    
                    if (method.isAnnotationPresent(Get.class)) {
                        GetMethodPresent=true;
                        Object pris=trouver.getDeclaredConstructor().newInstance();
                        Get annotation = method.getAnnotation(Get.class);
                        String url = annotation.value();
                        Mapping truest;
                        if (parameters.length>0) {
                            Object[] arguments = new Object[parameterTypes.length];
                            Annotation[][] parametreAnnot=method.getParameterAnnotations();
                            for (int i = 0; i < arguments.length; i++) {
                                Class<?> paramType = parameterTypes[i];
                                for(Annotation getting : parametreAnnot[i]){
                                    if (getting instanceof Param) {
                                        Param paramAnnotation = (Param) getting;
                                        arguments[i] = paramAnnotation.value();
                                        paramNames.add(paramType.getName());
                                    }
                               }
                                // Class<?> paramType = parameterTypes[i];
                                // if (paramType == int.class) {
                                //     arguments[i] = 0; 
                                // } else if (paramType == double.class) {
                                //     arguments[i] = 0.0; 
                                // } else if (paramType == String.class) {
                                //     arguments[i] = ""; 
                                // } else {
                                //     arguments[i] = null; 
                                // }
                            }
                            nbParam.put(method.getName(), paramNames);
                            truest = new Mapping(trouver.getName(), method.getName(),method.invoke(pris,arguments));                    
                            hmap.put(url, truest);
                        }else{

                            truest = new Mapping(trouver.getName(), method.getName(),method.invoke(pris));
                            if (hmap.containsKey(url)) {
                                throw new Exception("url existant ["+ url +"] dans "+ trouver.getName() + " et "+ hmap.get(url).getClassName());
                            }
                            hmap.put(url, truest);
                        }

                    }
                    hmap.put(url, truest);
                }
                if (!GetMethodPresent) {
                    throw new Exception("La classe " + trouver.getName() + " n'a aucune méthode annotée avec @GET."); 
                }
            }    
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'initialisation du FrontController", e);
        }
        
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, Exception {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
       
        String requestUrl = request.getRequestURI().substring(request.getContextPath().length());
        String reference=requestUrl;
        // requestUrl ="/"+requestUrl.substring(requestUrl.lastIndexOf("/") + 1);
        Mapping mapping = hmap.get(requestUrl);
        

        
        out.println("<html>");
        out.println("<head><title>Sprint5</title></head>");
        out.println("<body>");
        if (mapping != null) {
            // out.println(mapping.getMethodName());

            Class<?> newc=Class.forName(mapping.getClassName());
            Object controller=newc.getDeclaredConstructor().newInstance();
            Method method;
            Object result;
            Enumeration<String> parameterNames = request.getParameterNames();

            if (parameterNames.hasMoreElements()) {
                out.println(mapping.getMethodName());
                
                List<String> typeParametre= nbParam.get(mapping.getMethodName());

                    Class<?>[] pyte = new Class<?>[typeParametre.size()];
                
                    for (int i = 0; i < typeParametre.size(); i++) {
                        out.println(typeParametre.get(i));
                        try {
                            Class<?> allParamtype=Class.forName(typeParametre.get(i));
                            pyte[i] = allParamtype;
                            
                            
                        } catch (ClassNotFoundException e) {
                            out.println(e);
                            pyte[i] = null;
                        }
                    }
                    
                    method=controller.getClass().getDeclaredMethod(mapping.getMethodName(),pyte);
                    Annotation[][] parametreNotion=method.getParameterAnnotations();
                    
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Object[] arguments = new Object[parameterTypes.length];
                        
                    int i=0;    
                    while (parameterNames.hasMoreElements()) {
                        String paramName = parameterNames.nextElement();
                        String paramValue = request.getParameter(paramName);
                        for(Annotation getting : parametreNotion[i]){
                            if (getting instanceof Param) {
                                Param paramAnnotation = (Param) getting;
                                if(paramName.equalsIgnoreCase(paramAnnotation.value())){
                                    arguments[i] = paramValue;
                                }
                            }
                        }
                        i++;
                    }
                result = method.invoke(controller, arguments);
            }else{
                method=controller.getClass().getDeclaredMethod(mapping.getMethodName());
                result=method.invoke(controller);
            }

            out.println("<h1>URL: " + requestUrl + "</h1>");
            out.println("<li>Class: " + mapping.getClassName() + ":");
            out.println("<ul>Method: " + mapping.getMethodName() + "</ul>");


            if (result instanceof String) {
                out.println("<ul>Value of method: " + (String) result+ "</ul></li>");                                        
            } else if (result instanceof ModelView) {
                ModelView model=(ModelView) result;
                String url=model.getUrl();
                HashMap<String, Object> data=model.getData();
                for(String key : data.keySet()){
                    request.setAttribute(key,data.get(key));
                }


                // int lastIndex = url.lastIndexOf("/");
                // url = url.substring(0, lastIndex) + url.substring(lastIndex + 1, url.lastIndexOf("."));
                // url = reference.substring(0, reference.lastIndexOf("/")) + url;

                // int lastSlashIndex = reference.lastIndexOf("/");
                // int secondLastSlashIndex = reference.lastIndexOf("/", lastSlashIndex - 1);
                // url = reference.substring(0, secondLastSlashIndex + 1)+url;
                RequestDispatcher dispatcher = request.getRequestDispatcher(url);
                dispatcher.forward(request, response);
            }else {
                throw new Exception("invalide retour ou type de retour est non reconue");
            }                

        } else {
            out.println("<h1>THE URL : " + requestUrl + " NOT EXIST</h1>");
        }
        out.println("</body>");
        out.println("</html>");
       
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