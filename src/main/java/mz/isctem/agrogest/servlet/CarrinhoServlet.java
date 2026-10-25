package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Carrinho;
import mz.isctem.agrogest.service.CarrinhoService;
import mz.isctem.agrogest.service.NegocioException;

import java.io.IOException;

/**
 * Carrinho de compras (persiste na sessao).
 * GET  /carrinho              -> ver carrinho
 * POST /carrinho/adicionar    -> adicionar produto com quantidade
 * POST /carrinho/atualizar    -> alterar quantidade
 * POST /carrinho/remover      -> remover item
 * POST /carrinho/esvaziar     -> esvaziar carrinho
 */
@WebServlet(urlPatterns = {"/carrinho", "/carrinho/*"})
public class CarrinhoServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final CarrinhoService service = new CarrinhoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (utilizador(req).isAdmin()) {
            redirecionar(req, resp, "/admin/dashboard");
            return;
        }
        req.setAttribute("carrinho", carrinho(req));
        vista(req, resp, "cliente/carrinho");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (utilizador(req).isAdmin()) {
            redirecionar(req, resp, "/admin/dashboard");
            return;
        }
        String acao = req.getPathInfo() == null ? "" : req.getPathInfo();
        Carrinho carrinho = carrinho(req);
        String destino = "/carrinho";
        try {
            switch (acao) {
                case "/adicionar" -> {
                    service.adicionar(carrinho, id(req, "produtoId"), req.getParameter("quantidade"));
                    sucesso(req, "Produto adicionado ao carrinho.");
                    destino = "/catalogo";
                }
                case "/atualizar" -> {
                    service.atualizar(carrinho, id(req, "produtoId"), req.getParameter("quantidade"));
                    sucesso(req, "Quantidade atualizada.");
                }
                case "/remover" -> {
                    service.remover(carrinho, id(req, "produtoId"));
                    sucesso(req, "Item removido do carrinho.");
                }
                case "/esvaziar" -> {
                    service.esvaziar(carrinho);
                    sucesso(req, "O carrinho foi esvaziado.");
                }
                default -> erro(req, "Operação inválida.");
            }
        } catch (NegocioException e) {
            erro(req, e.getMessage());
            if ("/adicionar".equals(acao)) destino = "/catalogo";
        }
        redirecionar(req, resp, destino);
    }
}
