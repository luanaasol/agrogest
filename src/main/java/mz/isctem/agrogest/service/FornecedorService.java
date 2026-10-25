package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Fornecedor;
import mz.isctem.agrogest.model.TipoFornecedor;
import mz.isctem.agrogest.repo.DataStore;
import mz.isctem.agrogest.util.Validacao;

import java.util.Comparator;
import java.util.List;

/** Gestao de fornecedores: criar, listar e remover. */
public class FornecedorService {

    private DataStore ds() {
        return DataStore.get();
    }

    public Fornecedor criar(String nome, String tipo, String contacto, String provincia) {
        nome = Validacao.obrigatorio(nome, "Nome");
        TipoFornecedor t = Validacao.enumeracao(TipoFornecedor.class, tipo, "Tipo");
        contacto = Validacao.obrigatorio(contacto, "Contacto");
        provincia = Validacao.obrigatorio(provincia, "Província");
        DataStore ds = ds();
        synchronized (ds) {
            Fornecedor f = new Fornecedor(ds.novoIdFornecedor(), nome, t, contacto, provincia);
            ds.getFornecedores().add(f);
            return f;
        }
    }

    public List<Fornecedor> listar() {
        return DataStore.copia(ds().getFornecedores());
    }

    /** Listagem ordenada (nome ou id). */
    public List<Fornecedor> listar(String ordem) {
        List<Fornecedor> l = listar();
        if ("nome".equals(ordem)) l.sort(Comparator.comparing(Fornecedor::getNome, String.CASE_INSENSITIVE_ORDER));
        else l.sort(Comparator.comparingInt(Fornecedor::getId));
        return l;
    }

    public Fornecedor procurar(int id) {
        for (Fornecedor f : listar()) {
            if (f.getId() == id) return f;
        }
        return null;
    }

    public void remover(int id) {
        DataStore ds = ds();
        synchronized (ds) {
            Fornecedor f = procurar(id);
            if (f == null) throw new NegocioException("Fornecedor não encontrado.");
            boolean temProdutos = DataStore.copia(ds.getProdutos()).stream().anyMatch(p -> p.getFornecedorId() == id);
            if (temProdutos) {
                throw new NegocioException("Não é possível remover: existem produtos associados a este fornecedor.");
            }
            ds.getFornecedores().remove(f);
        }
    }
}
