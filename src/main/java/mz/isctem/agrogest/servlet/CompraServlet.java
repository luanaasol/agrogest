package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Carrinho;
import mz.isctem.agrogest.model.Venda;
import mz.isctem.agrogest.service.NegocioException;
import mz.isctem.agrogest.service.VendaService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Finalizar compra: GET mostra o resumo para confirmar, POST confirma e emite a factura. */
@WebServlet("/compra")
public class CompraServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final VendaService vendaService = new VendaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (utilizador(req).isAdmin()) {
            redirecionar(req, resp, "/admin/dashboard");
            return;
        }
        Carrinho c = carrinho(req);
        if (c.isVazio()) {
            erro(req, "O carrinho está vazio.");
            redirecionar(req, resp, "/carrinho");
            return;
        }
        BigDecimal subtotal = c.getTotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = subtotal.multiply(VendaService.TAXA_IVA).setScale(2, RoundingMode.HALF_UP);
        req.setAttribute("carrinho", c);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("iva", iva);
        req.setAttribute("total", subtotal.add(iva));
        vista(req, resp, "cliente/compra");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (utilizador(req).isAdmin()) {
            redirecionar(req, resp, "/admin/dashboard");
            return;
        }
        try {
            Venda v = vendaService.finalizarCompra(utilizador(req), carrinho(req));
            sucesso(req, "Compra confirmada. Factura " + v.getNumeroFactura() + " emitida.");
            redirecionar(req, resp, "/factura?id=" + v.getId());
        } catch (NegocioException e) {
            erro(req, e.getMessage());
            redirecionar(req, resp, "/carrinho");
        }
    }
}
