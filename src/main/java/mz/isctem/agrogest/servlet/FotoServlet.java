package mz.isctem.agrogest.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Produto;
import mz.isctem.agrogest.service.ProdutoService;

import java.io.IOException;

/** Devolve a imagem carregada de um produto (guardada em memoria no proprio Produto). GET /foto?id= */
@WebServlet("/foto")
public class FotoServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final ProdutoService produtoService = new ProdutoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Produto p = produtoService.procurar(id(req, "id"));
        if (p == null || !p.isFotoCarregada()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagem não encontrada.");
            return;
        }
        resp.setContentType(p.getFotoTipo());
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Cache-Control", "private, max-age=3600");
        resp.setContentLength(p.getFotoDados().length);
        resp.getOutputStream().write(p.getFotoDados());
    }
}
