package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.service.VendaService;

import java.io.IOException;

/** Historico de compras do cliente autenticado. */
@WebServlet("/minhas-compras")
public class MinhasComprasServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final VendaService vendaService = new VendaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (utilizador(req).isAdmin()) {
            redirecionar(req, resp, "/admin/listagens?tipo=compras");
            return;
        }
        req.setAttribute("vendas", vendaService.listarPorCliente(utilizador(req).getId()));
        vista(req, resp, "cliente/minhas-compras");
    }
}
