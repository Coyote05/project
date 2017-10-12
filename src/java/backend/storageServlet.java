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
import org.json.JSONArray;
import org.json.JSONObject;

/*
 -----------------------------------------------------------------------------
|                                   Norman Teibert
|                                  Frontend project
|       
------------------------------------------------------------------------------
 */
public class storageServlet extends HttpServlet {

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

            if (request.getParameter("task").equals("listProductsFirstPage")) {

                if (request.getSession(false) != null) {

                    StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllProductsFirstPage");
                    List<Object[]> products = spq.getResultList();

                    JSONArray productsArray = new JSONArray();

                    for (Object[] product : products) {

                        JSONObject productData = new JSONObject();

                        productData.put("id", product[0]);
                        productData.put("name", product[1]);
                        productData.put("type", product[2]);
                        productData.put("quantity", product[3]);
                        productData.put("unit", product[4]);
                        productsArray.put(productData);
                    }

                    out.write(productsArray.toString());
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("listFirstPage", 0);
                    out.write(vissza.toString());
                    out.close();
                }
            }

            if (request.getParameter("task").equals("listProductsSecondPage")) {

                if (request.getSession(false) != null) {

                    StoredProcedureQuery spq = em.createStoredProcedureQuery("getAllProductsSecondPage");
                    List<Object[]> products = spq.getResultList();

                    JSONArray productsArray = new JSONArray();

                    for (Object[] product : products) {

                        JSONObject productData = new JSONObject();

                        productData.put("id", product[0]);
                        productData.put("name", product[1]);
                        productData.put("type", product[2]);
                        productData.put("quantity", product[3]);
                        productData.put("unit", product[4]);
                        productsArray.put(productData);
                    }

                    out.write(productsArray.toString());
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("listSecondPage", 0);
                    out.write(vissza.toString());
                    out.close();
                }
            }

            if (request.getParameter("task").equals("createProduct")) {

                if (request.getSession(false) != null) {

                    String name = request.getParameter("nameP");
                    String type = request.getParameter("typeP");
                    String unit = request.getParameter("unitP");

                    int id = productNameExist(name, em);

                    if (id == 0) {

                        try {

                            StoredProcedureQuery spq = em.createStoredProcedureQuery("addNewProduct");
                            spq.registerStoredProcedureParameter("name", String.class, ParameterMode.IN);
                            spq.setParameter("name", name);
                            spq.registerStoredProcedureParameter("type", String.class, ParameterMode.IN);
                            spq.setParameter("type", type);
                            spq.registerStoredProcedureParameter("unit", String.class, ParameterMode.IN);
                            spq.setParameter("unit", unit);
                            spq.execute();

                            JSONObject vissza = new JSONObject();
                            vissza.put("newProduct", 1);
                            out.write(vissza.toString());
                            out.close();

                        } catch (Exception ex) {
                            JSONObject vissza = new JSONObject();
                            vissza.put("newProduct", 0);
                            out.write(vissza.toString());
                            out.close();
                        }
                    }
                    if (id > 0) {

                        JSONObject vissza = new JSONObject();
                        vissza.put("newProduct", 2);
                        out.write(vissza.toString());
                        out.close();
                    }
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("newProduct", 3);
                    out.write(vissza.toString());
                    out.close();
                }
            }

            if (request.getParameter("task").equals("deleteProduct")) {

                if (request.getSession(false) != null) {

                    String name = request.getParameter("nameP");

                    int id = productNameExist(name, em);
                    int quantity = -1;

                    if (id > 0) {

                        try {

                            StoredProcedureQuery spq = em.createStoredProcedureQuery("emptyStock");
                            spq.registerStoredProcedureParameter("id", Integer.class, ParameterMode.IN);
                            spq.setParameter("id", id);
                            spq.registerStoredProcedureParameter("quantity", Integer.class, ParameterMode.OUT);
                            spq.execute();

                            quantity = Integer.parseInt(spq.getOutputParameterValue("quantity").toString());

                            if (quantity == 0) {

                                StoredProcedureQuery storedProcedureQuery = em.createStoredProcedureQuery("deleteProduct");
                                storedProcedureQuery.registerStoredProcedureParameter("id", Integer.class, ParameterMode.IN);
                                storedProcedureQuery.setParameter("id", id);
                                storedProcedureQuery.execute();

                                JSONObject vissza = new JSONObject();
                                vissza.put("deleteProduct", 1);
                                out.write(vissza.toString());
                                out.close();

                            }
                            if (quantity > 0) {

                                JSONObject vissza = new JSONObject();
                                vissza.put("deleteProduct", 3);
                                out.write(vissza.toString());
                                out.close();

                            }

                        } catch (Exception ex) {
                            JSONObject vissza = new JSONObject();
                            vissza.put("deleteProduct", 0);
                            out.write(vissza.toString());
                            out.close();
                        }
                    }
                    if (id == 0) {

                        JSONObject vissza = new JSONObject();
                        vissza.put("deleteProduct", 2);
                        out.write(vissza.toString());
                        out.close();
                    }
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("deleteProduct", 4);
                    out.write(vissza.toString());
                    out.close();
                }
            }

            if (request.getParameter("task").equals("takeInProduct")) {

                if (request.getSession(false) != null) {

                    String name = request.getParameter("nameP");
                    Integer quantity = Integer.parseInt(request.getParameter("quantityP"));

                    int id = productNameExist(name, em);

                    if (id > 0) {

                        try {

                            StoredProcedureQuery spq = em.createStoredProcedureQuery("takeInProduct");
                            spq.registerStoredProcedureParameter("quantity", Integer.class, ParameterMode.IN);
                            spq.setParameter("quantity", quantity);
                            spq.registerStoredProcedureParameter("id", Integer.class, ParameterMode.IN);
                            spq.setParameter("id", id);
                            spq.execute();

                            JSONObject vissza = new JSONObject();
                            vissza.put("takeInProduct", 1);
                            out.write(vissza.toString());
                            out.close();

                        } catch (Exception ex) {
                            JSONObject vissza = new JSONObject();
                            vissza.put("takeInProduct", 0);
                            out.write(vissza.toString());
                            out.close();
                        }
                    }
                    if (id == 0) {

                        JSONObject vissza = new JSONObject();
                        vissza.put("takeInProduct", 2);
                        out.write(vissza.toString());
                        out.close();
                    }
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("takeInProduct", 3);
                    out.write(vissza.toString());
                    out.close();
                }
            }
            if (request.getParameter("task").equals("expendingProduct")) {

                if (request.getSession(false) != null) {

                    String name = request.getParameter("nameP");
                    Integer quantity = Integer.parseInt(request.getParameter("quantityP"));

                    int id = productNameExist(name, em);
                    int storedQuantity = -1;

                    if (id > 0) {

                        try {

                            StoredProcedureQuery spq = em.createStoredProcedureQuery("emptyStock");
                            spq.registerStoredProcedureParameter("id", Integer.class, ParameterMode.IN);
                            spq.setParameter("id", id);
                            spq.registerStoredProcedureParameter("quantity", Integer.class, ParameterMode.OUT);
                            spq.execute();

                            storedQuantity = Integer.parseInt(spq.getOutputParameterValue("quantity").toString());

                            if (storedQuantity >= quantity) {

                                StoredProcedureQuery storedProcedureQuery = em.createStoredProcedureQuery("expendingProduct");
                                storedProcedureQuery.registerStoredProcedureParameter("quantity", Integer.class, ParameterMode.IN);
                                storedProcedureQuery.setParameter("quantity", quantity);
                                storedProcedureQuery.registerStoredProcedureParameter("id", Integer.class, ParameterMode.IN);
                                storedProcedureQuery.setParameter("id", id);
                                storedProcedureQuery.execute();

                                JSONObject vissza = new JSONObject();
                                vissza.put("expendingProduct", 1);
                                out.write(vissza.toString());
                                out.close();
                            }
                            
                            if (storedQuantity < quantity) {
                            
                                JSONObject vissza = new JSONObject();
                                vissza.put("expendingProduct", 4);
                                out.write(vissza.toString());
                                out.close();
                            }

                        } catch (Exception ex) {
                            JSONObject vissza = new JSONObject();
                            vissza.put("expendingProduct", 0);
                            out.write(vissza.toString());
                            out.close();
                        }
                    }
                    if (id == 0) {

                        JSONObject vissza = new JSONObject();
                        vissza.put("expendingProduct", 2);
                        out.write(vissza.toString());
                        out.close();
                    }
                } else {
                    JSONObject vissza = new JSONObject();
                    vissza.put("expendingProduct", 3);
                    out.write(vissza.toString());
                    out.close();
                }
            }
        } catch (Exception ex) {

        }
    }

    private int productNameExist(String name, EntityManager em) {

        int id = 0;

        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("productNameExist");
            spq.registerStoredProcedureParameter("name", String.class, ParameterMode.IN);
            spq.setParameter("name", name);
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
