package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Fornecedor;
import mz.isctem.agrogest.model.TipoFornecedor;
import mz.isctem.agrogest.service.FornecedorService;
import mz.isctem.agrogest.service.NegocioException;

import java.io.IOException;

/** Gestao de fornecedores: criar, listar e remover. */
@WebServlet("/admin/fornecedores")
public class AdminFornecedoresServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final FornecedorService service = new FornecedorService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("remover".equals(req.getParameter("confirmar"))) {
            Fornecedor f = service.procurar(id(req, "id"));
            if (f != null) {
                confirmar(req, resp, "Remover o fornecedor \"" + f.getNome() + "\"?",
                        "Só é possível remover fornecedores sem produtos associados.", "/admin/fornecedores", "remover", f.getId());
                return;
            }
        }
        req.setAttribute("fornecedores", service.listar("id"));
        req.setAttribute("tipos", TipoFornecedor.values());
        vista(req, resp, "admin/fornecedores");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");
        try {
            if ("criar".equals(acao)) {
                service.criar(req.getParameter("nome"), req.getParameter("tipo"),
                        req.getParameter("contacto"), req.getParameter("provincia"));
                sucesso(req, "Fornecedor criado com sucesso.");
            } else if ("remover".equals(acao)) {
                service.remover(id(req, "id"));
                sucesso(req, "Fornecedor removido.");
            }
        } catch (NegocioException e) {
            erro(req, e.getMessage());
        }
        redirecionar(req, resp, "/admin/fornecedores");
    }
}
