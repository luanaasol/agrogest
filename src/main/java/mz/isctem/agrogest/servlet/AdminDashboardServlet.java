package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.service.CampanhaService;
import mz.isctem.agrogest.service.FornecedorService;
import mz.isctem.agrogest.service.ProdutoService;
import mz.isctem.agrogest.service.UtilizadorService;
import mz.isctem.agrogest.service.VendaService;

import java.io.IOException;

/** Painel do administrador com os totais do sistema. */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("numFornecedores", new FornecedorService().listar().size());
        req.setAttribute("numProdutos", new ProdutoService().listar().size());
        req.setAttribute("numClientes", new UtilizadorService().listarClientes().size());
        req.setAttribute("numVendas", new VendaService().listar().size());
        req.setAttribute("numCampanhas", new CampanhaService().listar().size());
        vista(req, resp, "admin/dashboard");
    }
}
