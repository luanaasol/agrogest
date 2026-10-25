package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Utilizador;
import mz.isctem.agrogest.model.Venda;
import mz.isctem.agrogest.service.VendaService;

import java.io.IOException;

/** Mostra (e permite imprimir) uma factura. O cliente so ve as suas; o admin ve todas (RN03). */
@WebServlet("/factura")
public class FacturaServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final VendaService vendaService = new VendaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utilizador u = utilizador(req);
        Venda v = vendaService.procurar(id(req, "id"));
        if (v == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Factura não encontrada.");
            return;
        }
        if (!u.isAdmin() && v.getClienteId() != u.getId()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Só pode ver as suas próprias facturas.");
            return;
        }
        req.setAttribute("venda", v);
        req.setAttribute("taxaIva", 16);
        vista(req, resp, "factura");
    }
}
