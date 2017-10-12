package backend;

import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/*-----------------------------------------------------------------------------
|                                   Norman Teibert
|                                  Frontend project
|       
------------------------------------------------------------------------------*/
public class indexServlet extends HttpServlet {

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
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("Frontend_project-PU");
            EntityManager em = emf.createEntityManager();

            if (request.getParameter("task").equals("login")) {

                String username = request.getParameter("userP");
                String password = request.getParameter("passwordP");

                int id = login(username, password, em); //password stored in SH1 in the database

                if (id > 0) {
                    HttpSession session = request.getSession();
                    em.getTransaction().begin();
                    Users users = em.find(Users.class, id);
                    session.setAttribute(username, out);
                    em.getTransaction().commit();
                    JSONObject vissza = new JSONObject();
                    vissza.put("id", users.getId());
                    out.write(vissza.toString());
                    out.close();
                }

                if (id == 0) {
                    JSONObject vissza = new JSONObject();
                    vissza.put("id", 0);
                    out.write(vissza.toString());
                    out.close();
                }
            }

        } catch (Exception ex) {
            PrintWriter out = response.getWriter();
            JSONObject vissza = new JSONObject();
            vissza.put("username", 0);
            out.write(vissza.toString());
            out.close();
        }
    }

    private int login(String username, String password, EntityManager em) {

        int id = 0;

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("login");
            spq.registerStoredProcedureParameter("username", String.class, ParameterMode.IN);
            spq.setParameter("username", username);
            spq.registerStoredProcedureParameter("password", String.class, ParameterMode.IN);
            spq.setParameter("password", password);
            spq.registerStoredProcedureParameter("idOut", String.class, ParameterMode.OUT);
            spq.execute();

            id = Integer.parseInt(spq.getOutputParameterValue("idOut").toString());
            return id;

        } catch (Exception ex) {

            return 0;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
