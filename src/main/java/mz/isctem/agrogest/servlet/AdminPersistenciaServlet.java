package mz.isctem.agrogest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.isctem.agrogest.repo.DataStore;
import mz.isctem.agrogest.repo.Persistencia;

import java.io.IOException;

/** Endpoint de gravacao e eliminacao manual do ficheiro de estado. */
@WebServlet("/admin/persistencia")
public class AdminPersistenciaServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("eliminar".equals(req.getParameter("confirmar"))) {
            confirmar(req, resp, "Eliminar o ficheiro de estado gravado em disco?",
                    "Os dados em memória não são apagados. Ao encerrar o Tomcat o estado volta a ser gravado.",
                    "/admin/persistencia", "eliminar", 0);
            return;
        }
        req.setAttribute("ficheiro", Persistencia.ficheiro().toAbsolutePath().toString());
        req.setAttribute("existe", Persistencia.existe());
        vista(req, resp, "admin/persistencia");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");
        try {
            if ("gravar".equals(acao)) {
                Persistencia.gravar(DataStore.get());
                sucesso(req, "Estado do sistema gravado com sucesso.");
            } else if ("eliminar".equals(acao)) {
                if (Persistencia.eliminar()) sucesso(req, "Ficheiro de estado eliminado.");
                else erro(req, "Não existe ficheiro gravado para eliminar.");
            }
        } catch (IOException e) {
            erro(req, "Erro ao aceder ao ficheiro: " + e.getMessage());
        }
        redirecionar(req, resp, "/admin/persistencia");
    }
}
