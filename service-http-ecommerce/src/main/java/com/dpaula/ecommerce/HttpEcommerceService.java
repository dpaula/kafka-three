package com.dpaula.ecommerce;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletContext;

/**
 * @author Fernando de Lima on 25/04/20
 */
public class HttpEcommerceService {

    public static void main(String[] args) throws Exception {

        //definindo a porta do servidor jetty
        final var server = new Server(8080);

        var contexto = new ServletContextHandler();
        //definindo path como raiz
        contexto.setContextPath("/");
        //definindo o servlet que sera chamado ao acessar o path informado com /new
        contexto.addServlet(new ServletHolder(new NewOrderServlet()), "/new");

        contexto.addServlet(new ServletHolder(new GenerateAllReportsServlet()), "/admin/generate-reports");

        //passando um contexto para o servidor trabalhar com as requisicoes
        server.setHandler(contexto);

        //startando o servidor
        server.start();
        //espera o servidor terminar para finalizar a aplicacao
        server.join();
    }
}
