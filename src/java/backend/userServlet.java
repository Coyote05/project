package backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
import org.json.JSONArray;
import org.json.JSONObject;

/*
 -----------------------------------------------------------------------------
|                                   Norman Teibert
|                                  Frontend project
|       
------------------------------------------------------------------------------
 */
public class userServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("Frontend_project-PU");
            EntityManager em = emf.createEntityManager();
            HttpSession session = request.getSession();

            if (request.getParameter("task").equals("listUsers")) {

                if (session.getAttribute("admin") != null) {

                    StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllUser");
                    List<Object[]> users = spq.getResultList();

                    JSONArray usersArray = new JSONArray();

                    for (Object[] user : users) {

                        JSONObject userData = new JSONObject();

                        userData.put("id", user[0]);
                        userData.put("username", user[1]);
                        usersArray.put(userData);
                    }

                    out.write(usersArray.toString());
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("list", 0);
                    out.write(vissza.toString());
                    out.close();
                }
            }

            if (request.getParameter("task").equals("createUser")) {

                if (session.getAttribute("admin") != null) {

                    String newUsername = request.getParameter("newUsernameP");
                    String newPassword = request.getParameter("newPasswordP");

                    int id = usernameExist(newUsername, em);

                    if (id == 0) {

                        try {

                            StoredProcedureQuery spq = em.createStoredProcedureQuery("addNewUser");
                            spq.registerStoredProcedureParameter("username", String.class, ParameterMode.IN);
                            spq.setParameter("username", newUsername);
                            spq.registerStoredProcedureParameter("password", String.class, ParameterMode.IN);
                            spq.setParameter("password", newPassword);
                            spq.execute();

                            JSONObject vissza = new JSONObject();
                            vissza.put("newUser", 1);
                            out.write(vissza.toString());
                            out.close();
                        } catch (Exception ex) {
                            JSONObject vissza = new JSONObject();
                            vissza.put("newUser", 0);
                            out.write(vissza.toString());
                            out.close();
                        }
                    }
                    if (id > 0) {

                        JSONObject vissza = new JSONObject();
                        vissza.put("newUser", 2);
                        out.write(vissza.toString());
                        out.close();
                    }
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("newUser", 3);
                    out.write(vissza.toString());
                    out.close();
                }
            }

            if (request.getParameter("task").equals("updateUser")) {

                if (session.getAttribute("admin") != null) {

                    String oldUsername = request.getParameter("oldUsernameP");
                    String newUsername = request.getParameter("newUsernameP");
                    String newPassword = request.getParameter("newPasswordP");

                    int id = usernameExist(oldUsername, em);

                    if (id > 0) {

                        try {
                            StoredProcedureQuery spq = em.createStoredProcedureQuery("updateUser");

                            spq.registerStoredProcedureParameter("username", String.class, ParameterMode.IN);
                            spq.setParameter("username", newUsername);
                            spq.registerStoredProcedureParameter("password", String.class, ParameterMode.IN);
                            spq.setParameter("password", newPassword);
                            spq.registerStoredProcedureParameter("id", Integer.class, ParameterMode.IN);
                            spq.setParameter("id", id);
                            spq.execute();

                            JSONObject vissza = new JSONObject();
                            vissza.put("updateUser", 1);
                            out.write(vissza.toString());
                            out.close();
                        } catch (Exception ex) {
                            JSONObject vissza = new JSONObject();
                            vissza.put("updateUser", 0);
                            out.write(vissza.toString());
                            out.close();
                        }
                    }
                    if (id == 0) {

                        JSONObject vissza = new JSONObject();
                        vissza.put("updateUser", 2);
                        out.write(vissza.toString());
                        out.close();
                    }
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("updateUser", 3);
                    out.write(vissza.toString());
                    out.close();
                }
            }

            if (request.getParameter("task").equals("deleteUser")) {

                if (session.getAttribute("admin") != null) {

                    String username = request.getParameter("usernameP");

                    int id = usernameExist(username, em);

                    if (id > 1) {

                        try {
                            StoredProcedureQuery spq = em.createStoredProcedureQuery("deleteUser");

                            spq.registerStoredProcedureParameter("id", Integer.class, ParameterMode.IN);
                            spq.setParameter("id", id);
                            spq.execute();

                            JSONObject vissza = new JSONObject();
                            vissza.put("deleteUser", 1);
                            out.write(vissza.toString());
                            out.close();
                        } catch (Exception ex) {
                            JSONObject vissza = new JSONObject();
                            vissza.put("deleteUser", 0);
                            out.write(vissza.toString());
                            out.close();
                        }
                    }
                    if (id == 0) {

                        JSONObject vissza = new JSONObject();
                        vissza.put("deleteUser", 2);
                        out.write(vissza.toString());
                        out.close();
                    }
                    if (id == 1) {

                        JSONObject vissza = new JSONObject();
                        vissza.put("deleteUser", 3);
                        out.write(vissza.toString());
                        out.close();
                    }
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("deleteUser", 4);
                    out.write(vissza.toString());
                    out.close();
                }
            }
        } catch (Exception ex) {

        }
    }

    private int usernameExist(String newUsername, EntityManager em) {

        int id = 0;

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("usernameExist");
            spq.registerStoredProcedureParameter("username", String.class, ParameterMode.IN);
            spq.setParameter("username", newUsername);
            spq.registerStoredProcedureParameter("idOut", Integer.class, ParameterMode.OUT);
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
