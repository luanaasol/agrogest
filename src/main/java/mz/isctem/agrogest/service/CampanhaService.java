package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Campanha;
import mz.isctem.agrogest.repo.DataStore;
import mz.isctem.agrogest.util.Validacao;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/** Gestao de campanhas: criar, fechar, reabrir e remover (so sem cultivos). */
public class CampanhaService {

    private DataStore ds() {
        return DataStore.get();
    }

    public Campanha criar(String nome, String inicio, String fim) {
        nome = Validacao.obrigatorio(nome, "Nome");
        LocalDate di = Validacao.data(inicio, "Data de início");
        LocalDate df = Validacao.data(fim, "Data de fim");
        if (df.isBefore(di)) throw new NegocioException("A data de fim não pode ser anterior à data de início.");
        DataStore ds = ds();
        synchronized (ds) {
            Campanha c = new Campanha(ds.novoIdCampanha(), nome, di, df);
            ds.getCampanhas().add(c);
            return c;
        }
    }

    public List<Campanha> listar() {
        List<Campanha> l = DataStore.copia(ds().getCampanhas());
        l.sort(Comparator.comparingInt(Campanha::getId));
        return l;
    }

    public Campanha procurar(int id) {
        for (Campanha c : listar()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public void fechar(int id) {
        alterarEstado(id, false);
    }

    public void reabrir(int id) {
        alterarEstado(id, true);
    }

    private void alterarEstado(int id, boolean aberta) {
        DataStore ds = ds();
        synchronized (ds) {
            Campanha c = procurar(id);
            if (c == null) throw new NegocioException("Campanha não encontrada.");
            c.setAberta(aberta);
        }
    }

    /** Nao permite remover campanhas com cultivos associados. */
    public void remover(int id) {
        DataStore ds = ds();
        synchronized (ds) {
            Campanha c = procurar(id);
            if (c == null) throw new NegocioException("Campanha não encontrada.");
            boolean temCultivos = DataStore.copia(ds.getCultivos()).stream().anyMatch(cu -> cu.getCampanhaId() == id);
            if (temCultivos) {
                throw new NegocioException("Não é possível remover: a campanha tem cultivos associados.");
            }
            ds.getCampanhas().remove(c);
        }
    }
}
