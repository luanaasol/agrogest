package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.model.Campanha;
import mz.isctem.agrogest.service.CampanhaService;
import mz.isctem.agrogest.service.NegocioException;

import java.io.IOException;

/** Gestao de campanhas: criar, fechar, reabrir e remover. */
@WebServlet("/admin/campanhas")
public class AdminCampanhasServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final CampanhaService service = new CampanhaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("remover".equals(req.getParameter("confirmar"))) {
            Campanha c = service.procurar(id(req, "id"));
            if (c != null) {
                confirmar(req, resp, "Remover a campanha \"" + c.getNome() + "\"?",
                        "Só é possível remover campanhas sem cultivos associados.", "/admin/campanhas", "remover", c.getId());
                return;
            }
        }
        req.setAttribute("campanhas", service.listar());
        vista(req, resp, "admin/campanhas");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");
        try {
            switch (acao == null ? "" : acao) {
                case "criar" -> {
                    service.criar(req.getParameter("nome"), req.getParameter("inicio"), req.getParameter("fim"));
                    sucesso(req, "Campanha criada com sucesso.");
                }
                case "fechar" -> {
                    service.fechar(id(req, "id"));
                    sucesso(req, "Campanha fechada.");
                }
                case "reabrir" -> {
                    service.reabrir(id(req, "id"));
                    sucesso(req, "Campanha reaberta.");
                }
                case "remover" -> {
                    service.remover(id(req, "id"));
                    sucesso(req, "Campanha removida.");
                }
                default -> erro(req, "Operação inválida.");
            }
        } catch (NegocioException e) {
            erro(req, e.getMessage());
        }
        redirecionar(req, resp, "/admin/campanhas");
    }
}
