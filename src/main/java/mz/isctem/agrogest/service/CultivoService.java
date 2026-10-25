package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Campanha;
import mz.isctem.agrogest.model.Cultivo;
import mz.isctem.agrogest.repo.DataStore;
import mz.isctem.agrogest.util.Validacao;

import java.util.Comparator;
import java.util.List;

/** Gestao de cultivos por campanha. Nao permite remover cultivos com colheitas. */
public class CultivoService {

    private final CampanhaService campanhaService = new CampanhaService();

    private DataStore ds() {
        return DataStore.get();
    }

    public Cultivo criar(String campanhaId, String cultura, String variedade, String hectares, String plantio) {
        Campanha c = campanhaService.procurar(Validacao.inteiro(campanhaId, "Campanha"));
        if (c == null) throw new NegocioException("Campanha inválida.");
        if (!c.isAberta()) throw new NegocioException("A campanha está fechada. Reabra-a para registar cultivos.");
        Cultivo cu = new Cultivo(0, c.getId(),
                Validacao.obrigatorio(cultura, "Cultura"),
                Validacao.obrigatorio(variedade, "Variedade"),
                Validacao.decimalPositivo(hectares, "Hectares"),
                Validacao.data(plantio, "Data de plantio"));
        DataStore ds = ds();
        synchronized (ds) {
            cu.setId(ds.novoIdCultivo());
            ds.getCultivos().add(cu);
        }
        return cu;
    }

    public List<Cultivo> listar() {
        List<Cultivo> l = DataStore.copia(ds().getCultivos());
        l.sort(Comparator.comparingInt(Cultivo::getId));
        return l;
    }

    public Cultivo procurar(int id) {
        for (Cultivo c : listar()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public void remover(int id) {
        DataStore ds = ds();
        synchronized (ds) {
            Cultivo c = procurar(id);
            if (c == null) throw new NegocioException("Cultivo não encontrado.");
            boolean temColheitas = DataStore.copia(ds.getColheitas()).stream().anyMatch(co -> co.getCultivoId() == id);
            if (temColheitas) {
                throw new NegocioException("Não é possível remover: o cultivo tem colheitas associadas.");
            }
            ds.getCultivos().remove(c);
        }
    }
}
