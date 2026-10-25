package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Perfil;
import mz.isctem.agrogest.model.Utilizador;
import mz.isctem.agrogest.repo.DataStore;
import mz.isctem.agrogest.util.PasswordUtil;
import mz.isctem.agrogest.util.Validacao;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Regras de negocio dos utilizadores: conta admin fixa, auto-registo e autenticacao. */
public class UtilizadorService {

    public static final String ADMIN_EMAIL = "admin@agro.mz";
    public static final String ADMIN_PASS = "admin234";

    private DataStore ds() {
        return DataStore.get();
    }

    /** Cria a conta fixa de administrador no arranque, se ainda nao existir. */
    public void garantirAdmin() {
        DataStore ds = ds();
        synchronized (ds) {
            if (procurarPorEmail(ADMIN_EMAIL) == null) {
                Utilizador admin = new Utilizador(ds.novoIdUtilizador(), "Administrador", ADMIN_EMAIL,
                        PasswordUtil.hash(ADMIN_PASS), Perfil.ADMIN, "", "");
                ds.getUtilizadores().add(admin);
            }
        }
    }

    /** Auto-registo de cliente: nome, email (unico), palavra-passe, telefone e endereço. */
    public Utilizador registarCliente(String nome, String email, String pass, String telefone, String endereco) {
        nome = Validacao.obrigatorio(nome, "Nome");
        email = Validacao.obrigatorio(email, "Email").toLowerCase();
        pass = Validacao.obrigatorio(pass, "Palavra-passe");
        telefone = Validacao.obrigatorio(telefone, "Telefone");
        endereco = Validacao.obrigatorio(endereco, "Endereço");
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new NegocioException("O email indicado não é válido.");
        }
        DataStore ds = ds();
        synchronized (ds) {
            if (procurarPorEmail(email) != null) {
                throw new NegocioException("Já existe uma conta registada com este email.");
            }
            Utilizador u = new Utilizador(ds.novoIdUtilizador(), nome, email, PasswordUtil.hash(pass),
                    Perfil.CLIENTE, telefone, endereco);
            ds.getUtilizadores().add(u);
            return u;
        }
    }

    public Utilizador autenticar(String email, String pass) {
        if (email == null || pass == null) throw new NegocioException("Indique o email e a palavra-passe.");
        Utilizador u = procurarPorEmail(email.trim().toLowerCase());
        if (u == null || !PasswordUtil.verificar(pass, u.getPass())) {
            throw new NegocioException("Email ou palavra-passe incorretos.");
        }
        if (!u.isAtivo()) throw new NegocioException("Esta conta está desativada. Contacte o administrador.");
        return u;
    }

    public Utilizador procurarPorEmail(String email) {
        for (Utilizador u : DataStore.copia(ds().getUtilizadores())) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    public Utilizador procurar(int id) {
        for (Utilizador u : DataStore.copia(ds().getUtilizadores())) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public List<Utilizador> listarClientes() {
        return DataStore.copia(ds().getUtilizadores()).stream()
                .filter(u -> u.getPerfil() == Perfil.CLIENTE)
                .collect(Collectors.toList());
    }

    /** Listagem de clientes ordenada por nome (crescente) ou por id. */
    public List<Utilizador> listarClientes(String ordem) {
        List<Utilizador> l = listarClientes();
        if ("nome".equals(ordem)) l.sort(Comparator.comparing(Utilizador::getNome, String.CASE_INSENSITIVE_ORDER));
        else l.sort(Comparator.comparingInt(Utilizador::getId));
        return l;
    }

    /** O administrador pode ativar ou desativar a conta de um cliente. */
    public void alternarAtivo(int id) {
        DataStore ds = ds();
        synchronized (ds) {
            Utilizador u = procurar(id);
            if (u == null || u.isAdmin()) throw new NegocioException("Cliente não encontrado.");
            u.setAtivo(!u.isAtivo());
        }
    }
}
