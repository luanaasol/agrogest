package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.service.RelatorioService;

import java.io.IOException;

/** Relatorios de clientes. */
@WebServlet("/admin/relatorios")
public class AdminRelatoriosServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final RelatorioService service = new RelatorioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("porNome", service.clientesPorNome());
        req.setAttribute("porVolume", service.clientesPorVolume());
        req.setAttribute("assiduos", service.clientesMaisAssiduos());
        req.setAttribute("semCompras", service.clientesSemCompras());
        vista(req, resp, "admin/relatorios");
    }
}
