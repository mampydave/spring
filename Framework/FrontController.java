package mg.itu.prom16.etu2564;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import mg.itu.prom16.etu2564.Max;
import mg.itu.prom16.etu2564.Min;
import mg.itu.prom16.etu2564.Mydate;
import mg.itu.prom16.etu2564.Myemail;
import mg.itu.prom16.etu2564.Required;
import mg.itu.prom16.etu2564.Roles;
import mg.itu.prom16.etu2564.ValidationResult;

import java.lang.ModuleLayer.Controller;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.gson.*;

import javax.print.attribute.standard.RequestingUserName;

@MultipartConfig
public class FrontController extends HttpServlet { 
    Map<String, Mapping> hmap;

    @Override
    public void init() throws ServletException {
        hmap = new HashMap<>();

        Mapping forhavingDefaultVal=new Mapping();
        try {
            
            ServletContext context = getServletContext();
            String chemin = context.getInitParameter("scan");
            
            List<String> controllers = scan(chemin); 
            boolean GetMethodPresent = true; // Flag pour vérifier les méthodes @GET
            for (String controller : controllers) {
                Class<?> trouver = Class.forName(controller);
                Method[] methods = trouver.getDeclaredMethods();
                for (Method method : methods) {
                    
                    String notionType="GET";   
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Parameter[] parameters = method.getParameters();
                    List<String> paramNames = new ArrayList<>();
                    List<String> roles = new ArrayList<>();
                    if (method.isAnnotationPresent(Url.class)) {
                        if (method.isAnnotationPresent(Post.class)) {
                            notionType = "POST";
                           
                        }    
                        if (method.isAnnotationPresent(Roles.class)) {
                            Roles recuperationRole = method.getAnnotation(Roles.class);
                            String[] tableaudeRole = recuperationRole.value();
                            for (int ro = 0; ro < tableaudeRole.length; ro++) {
                                roles.add(tableaudeRole[ro]);
                            }
                        }
                        
                        // GetMethodPresent=true;
                        Object pris=trouver.getDeclaredConstructor().newInstance();
                        Url annotation = method.getAnnotation(Url.class);
                        String url = annotation.value();
                        Mapping truest;
                        VerbAction verbact;
                        
                            
                        
                        if (parameters.length>0) {
                            Object[] arguments = new Object[parameterTypes.length];
                            Annotation[][] parametreAnnot=method.getParameterAnnotations();
                            
                            for (int i = 0; i < arguments.length; i++) {
                                Class<?> paramType = parameterTypes[i];

                                if (paramType.isPrimitive() || paramType.equals(String.class) || paramType.equals(Part.class)) {
                                                                
                                    if (parametreAnnot[i].length>0) {
                                        for(Annotation getting : parametreAnnot[i]){
                                            if (getting instanceof Param) {
                                                // Param paramAnnotation = (Param) getting;
                                                arguments[i] = forhavingDefaultVal.getDefaultValue(paramType);
                                                paramNames.add(paramType.getName());

                                            }
                                        }                                    
                                    }
                                    else{

                                        System.out.println("nandalo tato @l condition"+method.getName()+"length annot"+parametreAnnot[i].length+" paramType:"+paramType.getSimpleName());
                                        arguments[i] = forhavingDefaultVal.getDefaultValue(paramType);
                                        paramNames.add(paramType.getName());
                                    }
                                }else if (!paramType.isPrimitive() && !paramType.equals(String.class) && !paramType.equals(HttpServletRequest.class)) {
                                    Class ObjectParam1 = paramType;
                                    Object instanciate=ObjectParam1.getDeclaredConstructor().newInstance();
                                    Method[] listMethod=ObjectParam1.getDeclaredMethods(); 
                                    for (Method meth : listMethod ) {

                                        if (meth.getName().startsWith("set") && meth.getParameterCount()==1) {
                                            Class<?>[] param=meth.getParameterTypes();

                                            try {
                                                meth.invoke(instanciate,forhavingDefaultVal.getDefaultValue(param[0]));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }                                                    
                                        }
                                    }
                                    // System.out.println("nanadalo ato @ l bloc noon primitive :"+paramType.getName()+": "+instanciate);
                                    arguments[i]=instanciate;
                                    paramNames.add(paramType.getName());

                                }else if(paramType.equals(HttpServletRequest.class)){
                                    // System.out.println("instanciation : "+ paramType);
                                    paramNames.add(paramType.getName());   
                                }
                                else if(paramType.equals(ValidationResult.class)){
                                    paramNames.add(paramType.getName());
                                }else if (paramType.equals(Mysession.class)) {
                                    // HttpSession httpSession1=new HttpSession();
                                    // Mysession sess=new Mysession();
                                    // sess.add("no", "no");
                                    // arguments[i] = sess;
                                    paramNames.add(paramType.getName());
                                }

                            }
                            
                            if (method.isAnnotationPresent(Restapi.class)) {
                                // truest = new Mapping(trouver.getName(), notionType ,method.getName(),paramNames,true);                                
                                verbact = new VerbAction(notionType ,method.getName(),paramNames,true,roles);    
                                Mapping newtruest = hmap.computeIfAbsent(url, k -> new Mapping(trouver.getName(), new HashSet<>()));
                                
                                if (!newtruest.getVerbActions().add(verbact)) {
                                    throw new IllegalArgumentException("La notation \"" + notionType + "\" existe déjà pour l'URL \"" + url + " associer a la methode "+method.getName()+"\".");
                                    
                                }
                                // hmap.computeIfAbsent(url, k -> new Mapping(trouver.getName(), new ArrayList<>())).getVerbActions().add(verbact);
                            }else{                            
                                // truest = new Mapping(trouver.getName(), notionType,method.getName(),paramNames,false);
                                verbact = new VerbAction(notionType ,method.getName(),paramNames,false,roles);               
                                Mapping newtruest = hmap.computeIfAbsent(url, k -> new Mapping(trouver.getName(), new HashSet<>()));
                                
                                if (!newtruest.getVerbActions().add(verbact)) {
                                    throw new IllegalArgumentException("La notation \"" + notionType + "\" existe déjà pour l'URL \"" + url + " associer a la methode "+method.getName()+"\".");
                                    
                                }
                                // hmap.computeIfAbsent(url, k -> new Mapping(trouver.getName(), new ArrayList<>())).getVerbActions().add(verbact);
                                
                            }
                            // System.out.println(url +"Taille parame" + truest.getNbparam().size()+": "+truest.getNbparam());

                            // hmap.put(url, truest);
                        
                        }
                        else{
                            if (method.isAnnotationPresent(Restapi.class)) {
                                // truest = new Mapping(trouver.getName(), notionType,method.getName(),method.invoke(pris),true);                                
                                verbact = new VerbAction(notionType,method.getName(),method.invoke(pris),true,roles);
                                Mapping newtruest = hmap.computeIfAbsent(url, k -> new Mapping(trouver.getName(), new HashSet<>()));
                                
                                if (!newtruest.getVerbActions().add(verbact)) {
                                    throw new IllegalArgumentException("La notation \"" + notionType + "\" existe déjà pour l'URL \"" + url + " associer a la methode "+method.getName()+"\".");
                                    
                                }
                                // hmap.computeIfAbsent(url, k -> new Mapping(trouver.getName(), new ArrayList<>())).getVerbActions().add(verbact);
                            
                            }else{
                                // truest = new Mapping(trouver.getName(), notionType,method.getName(),method.invoke(pris),false);
                                verbact = new VerbAction(notionType,method.getName(),method.invoke(pris),false,roles);
                                Mapping newtruest = hmap.computeIfAbsent(url, k -> new Mapping(trouver.getName(), new HashSet<>()));
                                
                                if (!newtruest.getVerbActions().add(verbact)) {
                                    throw new IllegalArgumentException("La notation \"" + notionType + "\" existe déjà pour l'URL \"" + url + " associer a la methode "+method.getName()+"\".");
                                    
                                }
                                // hmap.computeIfAbsent(url, k -> new Mapping(trouver.getName(), new ArrayList<>())).getVerbActions().add(verbact);

                            }

                            // if (hmap.containsKey(url)) {
                            //     throw new Exception("url existant ["+ url +"] dans "+ trouver.getName() + " et "+ hmap.get(url).getClassName());
                            // }
                            // hmap.put(url, truest);
                        }

                    }
                }

                // if (!GetMethodPresent) {
                //     throw new Exception("La classe " + trouver.getName() + " n'a aucune méthode annotée avec @GET."); 
                // }
            }
    
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'initialisation du FrontController", e);
        }
    }


    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, Exception {
        
       
        String requestUrl = request.getRequestURI().substring(request.getContextPath().length());
        String reference=requestUrl;
        // requestUrl ="/"+requestUrl.substring(requestUrl.lastIndexOf("/") + 1);
        Mapping mapping = hmap.get(requestUrl);
        
  

        PrintWriter out = response.getWriter();
        
        // out.println("<html>");
        // out.println("<head><title>Sprint5</title></head>");
        // out.println(mapping.isEstRestapi()+"dave");
        // out.println("<body>");
        String methodFormul=request.getMethod();
        Map<String, List<String>> geterror= new HashMap<>();
        List<String> error = new ArrayList<>();

        ValidationResult erreurofValidation = new ValidationResult();
        
        try {
            VerbAction myVerbAction=new VerbAction();

            if (mapping != null) {

                for (VerbAction verbAction : mapping.getVerbActions()) {
                     if (verbAction.getAnnotateType().equalsIgnoreCase(methodFormul)) {
                        myVerbAction = verbAction;
                        break;                        
                    } else {
                        myVerbAction = verbAction;
                    }
                }
           
                mapping.getVerbActions().stream().forEach(element -> out.println(element));
                if (myVerbAction.isEstRestapi()) {
                    response.setContentType("application/json;charset=UTF-8");
                }
                if (!myVerbAction.isEstRestapi()){
                    response.setContentType("text/html;charset=UTF-8");
                }
    
                if (!myVerbAction.getAnnotateType().equalsIgnoreCase(methodFormul)) {
                    response.sendError(405,"la methode associer est :" + myVerbAction.getAnnotateType() + "alors que dans le formulaire c'est : "+methodFormul);
                }
                if (myVerbAction.getRoles().size()>0) {
                    HttpSession session = request.getSession();

                    ArrayList<Object> userInfo = (ArrayList<Object>) session.getAttribute("userInfo");

                    if (userInfo != null) {
                        boolean isAuthentified = (boolean) userInfo.get(0);
                        String role = (String) userInfo.get(1);
                        String accessAuthorized = "none";
                        for (int auth = 0; auth < myVerbAction.getRoles().size(); auth++) {
                            if (role.equalsIgnoreCase(myVerbAction.getRoles().get(auth))) {
                                accessAuthorized = role;
                                break;
                            }else{
                                accessAuthorized = myVerbAction.getRoles().get(auth);
                            }                            
                        }
                        if (!accessAuthorized.equalsIgnoreCase(role)) {
                            response.sendError(405,"Acces seulement reserver aux : "+ accessAuthorized +" alors que vous etes un : " +role);                            
                        }
                    } else {
                        response.sendError(405,"L'utilisateur doit etre connecter pour acceder a cette url");
                    }
    
                }

                Class<?> newc=Class.forName(mapping.getClassName());
                Object controller=newc.getDeclaredConstructor().newInstance();
                Method method;
                Object result;
                Enumeration<String> parameterNames = request.getParameterNames();
                List<String> typeParametre= myVerbAction.getNbparam();
                
                // System.out.println("nb parametre :"+ parameterNames.hasMoreElements());
                // out.println("nenandalo");

                for (Field field : newc.getDeclaredFields()) {
                    if (field.getType().equals(Mysession.class)) {
                        field.setAccessible(true);
                        HttpSession httpSession = request.getSession();
                        Mysession session = new Mysession(httpSession);

                        field.set(controller, session);
                    }
                }

                // out.println(typeParametre.size());
                
                if (typeParametre!=null && typeParametre.size()>0) {

                        Class<?>[] pyte = new Class<?>[typeParametre.size()];
                        for (int i = 0; i < typeParametre.size(); i++) {
                            //out.println(typeParametre.get(i));
                            try {
                                Class<?> allParamtype=Class.forName(typeParametre.get(i));

                                pyte[i] = allParamtype;
                                // out.println(pyte[i]);

                            } catch (ClassNotFoundException e) {
                                out.println(e);

                                pyte[i] = null;
                            }
                        }
                        
                        method=controller.getClass().getDeclaredMethod(myVerbAction.getMethodName(),pyte);
                        Annotation[][] parametreNotion=method.getParameterAnnotations();
                        
                        Object[] arguments = new Object[typeParametre.size()];
                        String paramName="default.default";
                        String[] knowObject=paramName.split("\\.");
                        String paramValue=null;
                            
                        for (int i=0; i < typeParametre.size();i++) {
                            
                            if (parameterNames.hasMoreElements()) {
                                paramName = parameterNames.nextElement();
                                
                                System.out.println("misy anaran "+ paramName);

                                // List<String> paramList = Collections.list(parameterNames);
                                knowObject=paramName.split("\\.");
                                // out.println(" dans la boucle parametre name: "+paramName+" l'indice' "+ i);
                                //out.print(knowObject.length);
                                //out.print(i);

                                
                                paramValue = request.getParameter(paramName);                                
                            }

    
                            if (pyte[i].isPrimitive() || pyte[i].equals(String.class)) {
                                
                                if (parametreNotion[i].length>0) {
                                    for(Annotation getting : parametreNotion[i]){
                                        if (getting instanceof Param) {
                                            Param paramAnnotation = (Param) getting;
                                            // out.println("parametre name: "+paramName+" l'indice' "+ i +" element "+paramAnnotation.value());

                                            if(paramName.equalsIgnoreCase(paramAnnotation.value())){
                                                arguments[i] = paramValue;
                                            }
    
                                        }
    
                                    }                                
                                }
                                else if(typeParametre.size()!=parametreNotion[i].length) {
                                    throw new Exception("ETU 002564 :les parametre doit etre annoter a @Param ");   
                                }
                                else 
                                {
                                    arguments[i] = paramValue;
                                    // out.println(arguments[i]);
                                }
                                
                            }
                            else if(!pyte[i].isPrimitive() && !pyte[i].equals(String.class) && knowObject.length>1){
                                // out.println("here");
                                    
                                Class<?> ObjectParam=Class.forName(typeParametre.get(i));
                                Object instanciate=ObjectParam.getDeclaredConstructor().newInstance();
                                String makeMaj=knowObject[1].substring(0,1).toUpperCase()+knowObject[1].substring(1);
                                // out.println(makeMaj);
    
                                Method[] listMethod=ObjectParam.getDeclaredMethods(); 
                                Field[] listAttribut = ObjectParam.getDeclaredFields();
                                
                                
                                for (int j = 0; j < listMethod.length; j++) {
                                    // out.println(knowObject[1] +": " + paramValue);
                                    System.out.println(knowObject[1] +": " + paramValue);
                                    erreurofValidation.addFieldValue(knowObject[1], paramValue);
                                    // out.println(listMethod[j].getName()+" = "+"set"+makeMaj +"indice j"+j +"<br>");
                                    if (listMethod[j].getName().equalsIgnoreCase("set"+makeMaj)) {
                                        
                                        Field trouverParRapportFormulaire=Mapping.trouverChamp(knowObject[1], listAttribut); 
                                        Annotation[] annotations = trouverParRapportFormulaire.getAnnotations();

                                        for (Annotation annotation : annotations) {
                                            if (annotation instanceof Required) {
                                                if (paramValue == null || paramValue.isEmpty()) {
                                                    
                                                    error.add("champ obligatoire");
                                                    erreurofValidation.addError(trouverParRapportFormulaire.getName(), "champ obligatoire");

                                                    // throw new Exception("Le champ " + trouverParRapportFormulaire.getName() + " est obligatoire !");
                                                }
                                                System.out.println(" - Annotation required " + trouverParRapportFormulaire.getName() + ": " + annotation.annotationType().getSimpleName());
                                            }
                                        
                                            if (annotation instanceof Min) {
                                                Min miniAnnote = (Min) annotation;
                                                Number paramValueNumber = Mapping.convertToNumber(paramValue); 
                                                
                                                if (paramValueNumber.doubleValue() < miniAnnote.value()) {
                                                    // erreurofValidation.addFieldValue(trouverParRapportFormulaire.getName(), paramValue);
                                                    error.add("La valeur doit être supérieure à : " + miniAnnote.value());
                                                    erreurofValidation.addError(trouverParRapportFormulaire.getName(), "La valeur doit être supérieure à : " + miniAnnote.value());
                                                    // throw new Exception("La valeur doit être supérieure à : " + miniAnnote.value());
                                                }
                                                // System.out.println(" - Annotation Min " + trouverParRapportFormulaire.getName() + ": " + annotation.annotationType().getSimpleName());
                                            }
                                            if (annotation instanceof Max) {
                                                Max maxAnnote = (Max) annotation;
                                                Number paramValueNumber = Mapping.convertToNumber(paramValue);

                                                if (paramValueNumber.doubleValue() > maxAnnote.value()) {
                                                    // erreurofValidation.addFieldValue(trouverParRapportFormulaire.getName(), paramValue);
                                                    error.add("La valeur doit être inférieure à : " + maxAnnote.value());
                                                    erreurofValidation.addError(trouverParRapportFormulaire.getName(), "La valeur doit être inférieure à : " + maxAnnote.value());

                                                    // throw new Exception("La valeur doit être inférieure à : " + maxAnnote.value());
                                                }
                                            }
                                            if (annotation instanceof Mydate) {
                                                Mydate dateFormatAnnote = (Mydate) annotation;
                                                String dateFormat = dateFormatAnnote.value();  
                                        
                                        
                                                if (!Mapping.isValidDate(paramValue, dateFormat)) {
                                                    // erreurofValidation.addFieldValue(trouverParRapportFormulaire.getName(), paramValue);
                                                    error.add("Format invalide, attendu : " + dateFormat);
                                                    erreurofValidation.addError(trouverParRapportFormulaire.getName(), "Format invalide, attendu : " + dateFormat);

                                                    // throw new Exception("La date '" + paramValue + "' n'est pas valide au format : " + dateFormat);
                                                }
                                        
                                                // System.out.println(" - Annotation Mydate " + trouverParRapportFormulaire.getName() + ": " + annotation.annotationType().getSimpleName());
                                            }

                                            if (annotation instanceof Myemail) {
                                                if (!Mapping.isValidEmail(paramValue)) {
                                                    // erreurofValidation.addFieldValue(trouverParRapportFormulaire.getName(), paramValue);
                                                    error.add("invalid email");
                                                    erreurofValidation.addError(trouverParRapportFormulaire.getName(), "invalid email");
                                                    // throw new Exception("L'email '" + paramValue + "' n'est pas valide.");
                                                }
                                        
                                                // System.out.println(" - Annotation MyEmail " + trouverParRapportFormulaire.getName() + ": " + annotation.annotationType().getSimpleName());
                                            }
                                        

                                        }
                                        if (error.size()!=0) {
                                            geterror.put(trouverParRapportFormulaire.getName(), error);                                            
                                        }
                                        // out.println("nom method: "+listMethod[j].getName()+"\n");
                                        // out.println("a l'indice "+j);
                                        listMethod[j].invoke(instanciate, paramValue);
    
    
                                        if (parameterNames.hasMoreElements()) {
                                            paramName = parameterNames.nextElement();
                                            knowObject=paramName.split("\\.");
                                            paramValue = request.getParameter(paramName);
                                            


                                            if (knowObject.length>1) {


                                                makeMaj=knowObject[1].substring(0,1).toUpperCase()+knowObject[1].substring(1);
                                                j=-1;
                                                out.println(paramName);
                                                out.println(knowObject.length);                                                
                                            }
                                            else {
                                                break;
                                            }

                                        }
                                    }
    

                                    // out.println("value of param: "+paramValue);
                                    // out.println("in emp: "+makeMaj+"\n");
                            
                                }
                                
                                // if (geterror != null && !geterror.isEmpty()) {
                                //     request.setAttribute("error", geterror);
                                //     // Dispatcher vers une page JSP
                                //     request.getRequestDispatcher("/except.jsp").forward(request, response);
                                // }
                                arguments[i]=instanciate;
                            }

                            if (pyte[i].getName().equals(ValidationResult.class.getName())) {
                                arguments[i] = erreurofValidation;
                            }
                            if (pyte[i].getName().equals(HttpServletRequest.class.getName())) {
                                arguments[i] = request;
                            }
                            if (pyte[i].getName().equals(Mysession.class.getName())) {
                                // out.println("session"); 
                                HttpSession httpSession = request.getSession();
                                Mysession session = new Mysession(httpSession);
                                arguments[i] = session;   
                            }
                            if (pyte[i].equals(Part.class)) {
                                
                                                                
                                if (parametreNotion[i].length>0) {
                                    for(Annotation getting : parametreNotion[i]){
                                        if (getting instanceof Param) {
                                            Param paramAnnotation = (Param) getting;
                                            Part part = request.getPart(paramAnnotation.value());
                                            arguments[i] = part;
                                        
    
                                        }
    
                                    }                                
                                }
                                else if(typeParametre.size()!=parametreNotion[i].length) {
                                    throw new Exception("ETU 002564 :les parametre doit etre annoter a @Param ");   
                                }
                                // else 
                                // {
                                //     arguments[i] = paramValue;
                                //     // out.println(arguments[i]);
                                // }   
                            }
                        }
                        
                        // out.println(arguments[1]);

                    result = method.invoke(controller, arguments);
                    // result=0;
                
                }else{
                    method=controller.getClass().getDeclaredMethod(myVerbAction.getMethodName());
                    result=method.invoke(controller);
                }
    
                out.println("<h1>URL: " + requestUrl + "</h1>");
                out.println("<li>Class: " + mapping.getClassName() + ":");
                out.println("<ul>Method: " + myVerbAction.getMethodName() + "</ul>");

                if (myVerbAction.isEstRestapi()) {
                    if (result instanceof String) {
                        Gson gson=new Gson();
                        String json = gson.toJson(result);
                        out.print(json);
                        out.flush();
                    } 
                    else if (result instanceof ModelView) {
                        ModelView model=(ModelView) result;
                        String url=model.getUrl();
                        HashMap<String, Object> data=model.getData();
                        Gson gsonModV=new Gson();
                        String jsonModV = gsonModV.toJson(data);
                        out.print(jsonModV);
                        out.flush();
                    }
                    else {
                        throw new Exception("invalide retour ou type de retour est non reconue");
                    }                   
                }
                else{
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
                        request = new Wrapper(request, "GET");
                        request.getRequestDispatcher(url).forward(request, response);

                    }else {
                        throw new Exception("invalide retour ou type de retour est non reconue");
                    }
                }               
    
            } 
            else {
                response.sendError(404, "THE URL : " + requestUrl + " NOT EXIST");
            }            
        } catch (Exception e) {
            // out.println(e);
            e.printStackTrace();
        }

        // out.println("</body>");
        // out.println("</html>");

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){
        
        try {
            PrintWriter out = response.getWriter();

            processRequest(request, response);

        } catch (Exception e) {
            // System.out.println(e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response){
        
        try {
            PrintWriter out = response.getWriter();

            processRequest(request, response);

        } catch (Exception e) {
            // System.out.println(e);
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