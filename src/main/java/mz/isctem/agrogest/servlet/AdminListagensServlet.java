package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.service.FornecedorService;
import mz.isctem.agrogest.service.NegocioException;
import mz.isctem.agrogest.service.ProdutoService;
import mz.isctem.agrogest.service.UtilizadorService;
import mz.isctem.agrogest.service.VendaService;

import java.io.IOException;

/**
 * Listagens com ordenacao:
 * tipo = produtos | fornecedores | clientes | armazem | compras
 * ordem = nome | preco | stock | data | valor (conforme o tipo)
 */
@WebServlet("/admin/listagens")
public class AdminListagensServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final ProdutoService produtoService = new ProdutoService();
    private final FornecedorService fornecedorService = new FornecedorService();
    private final UtilizadorService utilizadorService = new UtilizadorService();
    private final VendaService vendaService = new VendaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tipo = req.getParameter("tipo");
        if (tipo == null || tipo.isBlank()) tipo = "produtos";
        String ordem = req.getParameter("ordem");
        if (ordem == null) ordem = "";

        switch (tipo) {
            case "fornecedores" -> req.setAttribute("fornecedores", fornecedorService.listar(ordem));
            case "clientes" -> req.setAttribute("clientes", utilizadorService.listarClientes(ordem));
            case "armazem" -> req.setAttribute("produtos", produtoService.emArmazem(ordem));
            case "compras" -> req.setAttribute("vendas", vendaService.listar(ordem));
            default -> {
                tipo = "produtos";
                req.setAttribute("produtos", produtoService.listar(ordem));
            }
        }
        req.setAttribute("tipo", tipo);
        req.setAttribute("ordem", ordem);
        vista(req, resp, "admin/listagens");
    }

    /** Ativar/desativar a conta de um cliente a partir da listagem de clientes. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("alternarCliente".equals(req.getParameter("acao"))) {
            try {
                utilizadorService.alternarAtivo(id(req, "id"));
                sucesso(req, "Estado do cliente atualizado.");
            } catch (NegocioException e) {
                erro(req, e.getMessage());
            }
        }
        redirecionar(req, resp, "/admin/listagens?tipo=clientes");
    }
}
