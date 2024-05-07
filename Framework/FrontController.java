package mg.itu.prom16.etu2564;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FrontController extends HttpServlet { 

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                response.setContentType("text/html");
        try{
 
            PrintWriter out= response.getWriter(); 
            out.println("<h2>pattern    : " + request.getRequestURI() +"</h2>"); 

        } catch (Exception ex) {
            throw ex;     
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}