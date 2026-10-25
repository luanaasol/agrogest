package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Categoria;
import mz.isctem.agrogest.model.Produto;
import mz.isctem.agrogest.service.FornecedorService;
import mz.isctem.agrogest.service.NegocioException;
import mz.isctem.agrogest.service.ProdutoService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Gestao de produtos: criar, listar, repor stock e remover.
 * @MultipartConfig permite receber o ficheiro de imagem do formulario (enctype multipart/form-data).
 */
@WebServlet("/admin/produtos")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class AdminProdutosServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final ProdutoService service = new ProdutoService();
    private final FornecedorService fornecedorService = new FornecedorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("remover".equals(req.getParameter("confirmar"))) {
            Produto p = service.procurar(id(req, "id"));
            if (p != null) {
                confirmar(req, resp, "Remover o produto \"" + p.getNome() + "\"?",
                        "As facturas já emitidas não são alteradas.", "/admin/produtos", "remover", p.getId());
                return;
            }
        }
        req.setAttribute("produtos", service.listar("id"));
        req.setAttribute("fornecedores", fornecedorService.listar("nome"));
        req.setAttribute("categorias", Categoria.values());
        vista(req, resp, "admin/produtos");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // Se o ficheiro exceder o limite do @MultipartConfig, o Tomcat recusa o pedido inteiro
        if (req.getContentType() != null && req.getContentType().startsWith("multipart/")) {
            try {
                req.getParts();
            } catch (IllegalStateException e) {
                erro(req, "A imagem é demasiado grande (máximo 2 MB).");
                redirecionar(req, resp, "/admin/produtos");
                return;
            }
        }
        String acao = req.getParameter("acao");
        try {
            switch (acao == null ? "" : acao) {
                case "criar" -> {
                    service.criar(req.getParameter("nome"), req.getParameter("categoria"),
                            req.getParameter("fornecedorId"), req.getParameter("preco"),
                            req.getParameter("quantidade"), req.getParameter("unidade"),
                            req.getParameter("descricao"), req.getParameter("foto"), req.getParameter("validade"),
                            lerImagem(req), tipoImagem(req));
                    sucesso(req, "Produto criado com sucesso.");
                }
                case "repor" -> {
                    service.reporStock(id(req, "id"), req.getParameter("quantidade"));
                    sucesso(req, "Stock reposto.");
                }
                case "remover" -> {
                    service.remover(id(req, "id"));
                    sucesso(req, "Produto removido.");
                }
                default -> erro(req, "Operação inválida.");
            }
        } catch (NegocioException e) {
            erro(req, e.getMessage());
        }
        redirecionar(req, resp, "/admin/produtos");
    }

    /** Bytes da imagem carregada no campo "imagem" (null se nao foi escolhido nenhum ficheiro). */
    private byte[] lerImagem(HttpServletRequest req) throws IOException, ServletException {
        Part parte = parteImagem(req);
        if (parte == null) return null;
        try (InputStream in = parte.getInputStream()) {
            return in.readAllBytes();
        }
    }

    private String tipoImagem(HttpServletRequest req) throws IOException, ServletException {
        Part parte = parteImagem(req);
        return parte == null ? null : parte.getContentType();
    }

    private Part parteImagem(HttpServletRequest req) throws IOException, ServletException {
        if (req.getContentType() == null || !req.getContentType().startsWith("multipart/")) return null;
        Part parte = req.getPart("imagem");
        return (parte == null || parte.getSize() == 0) ? null : parte;
    }
}
