package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Categoria;
import mz.isctem.agrogest.service.ProdutoService;

import java.io.IOException;

/** Catalogo de produtos com filtro por categoria. */
@WebServlet("/catalogo")
public class CatalogoServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final ProdutoService produtoService = new ProdutoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String categoria = req.getParameter("categoria");
        req.setAttribute("produtos", produtoService.catalogo(categoria));
        req.setAttribute("categorias", Categoria.values());
        req.setAttribute("categoriaAtual", categoria == null ? "" : categoria);
        vista(req, resp, "cliente/catalogo");
    }
}
