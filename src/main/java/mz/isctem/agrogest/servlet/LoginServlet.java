package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mz.isctem.agrogest.model.Utilizador;
import mz.isctem.agrogest.service.NegocioException;
import mz.isctem.agrogest.service.UtilizadorService;

import java.io.IOException;

/** Autenticacao de administrador e clientes. */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final UtilizadorService service = new UtilizadorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilizador u = utilizador(req);
        if (u != null) {
            redirecionar(req, resp, u.isAdmin() ? "/admin/dashboard" : "/catalogo");
            return;
        }
        vista(req, resp, "login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Utilizador u = service.autenticar(req.getParameter("email"), req.getParameter("pass"));
            // Nova sessao apos login (evita fixacao de sessao)
            HttpSession antiga = req.getSession(false);
            if (antiga != null) antiga.invalidate();
            HttpSession s = req.getSession(true);
            s.setAttribute(SESSAO_UTILIZADOR, u);
            redirecionar(req, resp, u.isAdmin() ? "/admin/dashboard" : "/catalogo");
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("email", req.getParameter("email"));
            vista(req, resp, "login");
        }
    }
}
