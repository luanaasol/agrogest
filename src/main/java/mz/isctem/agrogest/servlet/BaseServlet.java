package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mz.isctem.agrogest.model.Carrinho;
import mz.isctem.agrogest.model.Utilizador;

import java.io.IOException;

/** Metodos comuns aos controladores: vistas JSP, redirecionamentos e mensagens. */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String SESSAO_UTILIZADOR = "utilizador";
    public static final String SESSAO_CARRINHO = "carrinho";

    /** Encaminha para /WEB-INF/views/{vista}.jsp (as JSP nao sao acessiveis diretamente). */
    protected void vista(HttpServletRequest req, HttpServletResponse resp, String vista)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + vista + ".jsp").forward(req, resp);
    }

    protected void redirecionar(HttpServletRequest req, HttpServletResponse resp, String caminho) throws IOException {
        resp.sendRedirect(req.getContextPath() + caminho);
    }

    /** Mensagem de sucesso a mostrar na proxima pagina (padrao Post/Redirect/Get). */
    protected void sucesso(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("msgSucesso", texto);
    }

    /** Mensagem de erro a mostrar na proxima pagina. */
    protected void erro(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("msgErro", texto);
    }

    protected Utilizador utilizador(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (Utilizador) s.getAttribute(SESSAO_UTILIZADOR);
    }

    /** Carrinho de compras guardado na sessao HTTP (criado na primeira utilizacao). */
    protected Carrinho carrinho(HttpServletRequest req) {
        HttpSession s = req.getSession();
        Carrinho c = (Carrinho) s.getAttribute(SESSAO_CARRINHO);
        if (c == null) {
            c = new Carrinho();
            s.setAttribute(SESSAO_CARRINHO, c);
        }
        return c;
    }

    /**
     * Pagina de confirmacao sem JavaScript (substitui o confirm()).
     * O GET mostra a pergunta; o botao Confirmar faz POST para "destino" com a acao e o id.
     */
    protected void confirmar(HttpServletRequest req, HttpServletResponse resp, String pergunta, String aviso,
                             String destino, String acao, int id) throws ServletException, IOException {
        req.setAttribute("pergunta", pergunta);
        req.setAttribute("aviso", aviso);
        req.setAttribute("destino", destino);
        req.setAttribute("acaoConfirmar", acao);
        req.setAttribute("idConfirmar", id);
        vista(req, resp, "confirmar");
    }

    protected int id(HttpServletRequest req, String nome) {
        try {
            return Integer.parseInt(req.getParameter(nome));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
