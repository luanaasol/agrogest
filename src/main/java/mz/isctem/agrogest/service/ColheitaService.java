package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Colheita;
import mz.isctem.agrogest.model.Cultivo;
import mz.isctem.agrogest.repo.DataStore;
import mz.isctem.agrogest.util.Validacao;

import java.util.Comparator;
import java.util.List;

/** Gestao de colheitas por cultivo. O valor total e calculado no modelo (kg x preco/kg). */
public class ColheitaService {

    private final CultivoService cultivoService = new CultivoService();

    private DataStore ds() {
        return DataStore.get();
    }

    public Colheita criar(String cultivoId, String data, String kg, String precoKg) {
        Cultivo cu = cultivoService.procurar(Validacao.inteiro(cultivoId, "Cultivo"));
        if (cu == null) throw new NegocioException("Cultivo inválido.");
        Colheita c = new Colheita(0, cu.getId(),
                Validacao.data(data, "Data"),
                Validacao.decimalPositivo(kg, "Quantidade (kg)"),
                Validacao.dinheiro(precoKg, "Preço por kg"));
        DataStore ds = ds();
        synchronized (ds) {
            c.setId(ds.novoIdColheita());
            ds.getColheitas().add(c);
        }
        return c;
    }

    public List<Colheita> listar() {
        List<Colheita> l = DataStore.copia(ds().getColheitas());
        l.sort(Comparator.comparingInt(Colheita::getId));
        return l;
    }

    public void remover(int id) {
        DataStore ds = ds();
        synchronized (ds) {
            boolean removido = ds.getColheitas().removeIf(c -> c.getId() == id);
            if (!removido) throw new NegocioException("Colheita não encontrada.");
        }
    }
}
