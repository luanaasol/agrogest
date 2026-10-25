package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.service.NegocioException;
import mz.isctem.agrogest.service.UtilizadorService;

import java.io.IOException;

/** Auto-registo de clientes. */
@WebServlet("/registo")
public class RegistoServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final UtilizadorService service = new UtilizadorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        vista(req, resp, "registo");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            service.registarCliente(req.getParameter("nome"), req.getParameter("email"), req.getParameter("pass"),
                    req.getParameter("telefone"), req.getParameter("endereco"));
            sucesso(req, "Registo efetuado com sucesso. Já pode entrar com o seu email e palavra-passe.");
            redirecionar(req, resp, "/login");
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            vista(req, resp, "registo");
        }
    }
}
