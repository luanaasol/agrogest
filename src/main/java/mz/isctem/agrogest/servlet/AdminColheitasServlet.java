package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Colheita;
import mz.isctem.agrogest.model.Cultivo;
import mz.isctem.agrogest.service.ColheitaService;
import mz.isctem.agrogest.service.CultivoService;
import mz.isctem.agrogest.service.NegocioException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Gestao de colheitas por cultivo. O sistema calcula o valor total. */
@WebServlet("/admin/colheitas")
public class AdminColheitasServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final ColheitaService service = new ColheitaService();
    private final CultivoService cultivoService = new CultivoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<Integer, String> nomesCultivos = new HashMap<>();
        for (Cultivo c : cultivoService.listar()) {
            nomesCultivos.put(c.getId(), c.getCultura() + " (" + c.getVariedade() + ")");
        }
        if ("remover".equals(req.getParameter("confirmar"))) {
            int id = id(req, "id");
            for (Colheita co : service.listar()) {
                if (co.getId() == id) {
                    confirmar(req, resp, "Remover a colheita de " + nomesCultivos.get(co.getCultivoId())
                            + " registada em " + co.getDataFmt() + "?", null, "/admin/colheitas", "remover", id);
                    return;
                }
            }
        }
        req.setAttribute("colheitas", service.listar());
        req.setAttribute("cultivos", cultivoService.listar());
        req.setAttribute("nomesCultivos", nomesCultivos);
        vista(req, resp, "admin/colheitas");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");
        try {
            if ("criar".equals(acao)) {
                service.criar(req.getParameter("cultivoId"), req.getParameter("data"),
                        req.getParameter("kg"), req.getParameter("precoKg"));
                sucesso(req, "Colheita registada com sucesso.");
            } else if ("remover".equals(acao)) {
                service.remover(id(req, "id"));
                sucesso(req, "Colheita removida.");
            }
        } catch (NegocioException e) {
            erro(req, e.getMessage());
        }
        redirecionar(req, resp, "/admin/colheitas");
    }
}
