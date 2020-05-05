package com.dpaula.ecommerce;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author Fernando de Lima on 25/04/20
 */
public class GenerateAllReportsServlet extends HttpServlet {

    private final KafkaDispatcher<String> bathDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();

        bathDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {

            bathDispatcher.send("SEND_MESSAGE_TO_ALL_USERS", "USER_GENERATE_READING_REPORT", "USER_GENERATE_READING_REPORT");

            System.out.println("Enviando geração de relatórios para todos os usuários!!");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Gerado as requisições para os relatórios!!");

        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
