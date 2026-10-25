package mz.isctem.agrogest.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mz.isctem.agrogest.model.Utilizador;

import java.io.IOException;

/**
 * Controlo de acesso por sessao e perfil.
 * RN04: /admin/* exclusivo do administrador (HTTP 403 caso contrario).
 * RN05: /catalogo, /carrinho/*, /compra, /factura, /minhas-compras exigem sessao.
 */
@WebFilter(urlPatterns = {"/admin/*", "/catalogo", "/carrinho", "/carrinho/*", "/compra", "/factura", "/minhas-compras"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession sessao = req.getSession(false);
        Utilizador u = sessao == null ? null : (Utilizador) sessao.getAttribute("utilizador");

        // Sem sessao: vai para o login
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Rotas de administracao: apenas o perfil ADMIN
        String caminho = req.getRequestURI().substring(req.getContextPath().length());
        // RN04: inclui "/admin" sem barra final, que o padrao /admin/* tambem apanha
        if ((caminho.equals("/admin") || caminho.startsWith("/admin/")) && !u.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso reservado ao administrador.");
            return;
        }

        // Evita que paginas protegidas fiquem em cache depois do logout
        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}
