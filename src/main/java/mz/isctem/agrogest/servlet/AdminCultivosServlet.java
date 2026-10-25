package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Campanha;
import mz.isctem.agrogest.model.Cultivo;
import mz.isctem.agrogest.service.CampanhaService;
import mz.isctem.agrogest.service.CultivoService;
import mz.isctem.agrogest.service.NegocioException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Gestao de cultivos por campanha. */
@WebServlet("/admin/cultivos")
public class AdminCultivosServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final CultivoService service = new CultivoService();
    private final CampanhaService campanhaService = new CampanhaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("remover".equals(req.getParameter("confirmar"))) {
            Cultivo cu = service.procurar(id(req, "id"));
            if (cu != null) {
                confirmar(req, resp, "Remover o cultivo \"" + cu.getCultura() + " (" + cu.getVariedade() + ")\"?",
                        "Só é possível remover cultivos sem colheitas associadas.", "/admin/cultivos", "remover", cu.getId());
                return;
            }
        }
        Map<Integer, String> nomesCampanhas = new HashMap<>();
        for (Campanha c : campanhaService.listar()) nomesCampanhas.put(c.getId(), c.getNome());
        req.setAttribute("cultivos", service.listar());
        req.setAttribute("campanhas", campanhaService.listar());
        req.setAttribute("nomesCampanhas", nomesCampanhas);
        vista(req, resp, "admin/cultivos");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");
        try {
            if ("criar".equals(acao)) {
                service.criar(req.getParameter("campanhaId"), req.getParameter("cultura"),
                        req.getParameter("variedade"), req.getParameter("hectares"), req.getParameter("plantio"));
                sucesso(req, "Cultivo registado com sucesso.");
            } else if ("remover".equals(acao)) {
                service.remover(id(req, "id"));
                sucesso(req, "Cultivo removido.");
            }
        } catch (NegocioException e) {
            erro(req, e.getMessage());
        }
        redirecionar(req, resp, "/admin/cultivos");
    }
}
