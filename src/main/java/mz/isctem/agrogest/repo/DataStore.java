package mz.isctem.agrogest.repo;

import mz.isctem.agrogest.model.Campanha;
import mz.isctem.agrogest.model.Colheita;
import mz.isctem.agrogest.model.Cultivo;
import mz.isctem.agrogest.model.Fornecedor;
import mz.isctem.agrogest.model.Produto;
import mz.isctem.agrogest.model.Utilizador;
import mz.isctem.agrogest.model.Venda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Camada de persistencia em memoria: todas as listas da aplicacao.
 * As listas sao Collections.synchronizedList e os IDs sao gerados por AtomicInteger (RN06).
 * O objeto inteiro e serializavel para ser gravado em ficheiro (.ser).
 */
public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private static volatile DataStore instancia = new DataStore();

    public static DataStore get() {
        return instancia;
    }

    /** Substitui o estado em memoria (usado ao restaurar o ficheiro no arranque). */
    public static synchronized void substituir(DataStore novo) {
        instancia = novo;
    }

    // ----- Listas em memoria -----
    private final List<Utilizador> utilizadores = Collections.synchronizedList(new ArrayList<>());
    private final List<Fornecedor> fornecedores = Collections.synchronizedList(new ArrayList<>());
    private final List<Produto> produtos = Collections.synchronizedList(new ArrayList<>());
    private final List<Campanha> campanhas = Collections.synchronizedList(new ArrayList<>());
    private final List<Cultivo> cultivos = Collections.synchronizedList(new ArrayList<>());
    private final List<Colheita> colheitas = Collections.synchronizedList(new ArrayList<>());
    private final List<Venda> vendas = Collections.synchronizedList(new ArrayList<>());

    // ----- Geradores de IDs sequenciais e unicos -----
    private final AtomicInteger seqUtilizador = new AtomicInteger(0);
    private final AtomicInteger seqFornecedor = new AtomicInteger(0);
    private final AtomicInteger seqProduto = new AtomicInteger(0);
    private final AtomicInteger seqCampanha = new AtomicInteger(0);
    private final AtomicInteger seqCultivo = new AtomicInteger(0);
    private final AtomicInteger seqColheita = new AtomicInteger(0);
    private final AtomicInteger seqVenda = new AtomicInteger(0);
    private final AtomicInteger seqFactura = new AtomicInteger(0);

    public List<Utilizador> getUtilizadores() { return utilizadores; }
    public List<Fornecedor> getFornecedores() { return fornecedores; }
    public List<Produto> getProdutos() { return produtos; }
    public List<Campanha> getCampanhas() { return campanhas; }
    public List<Cultivo> getCultivos() { return cultivos; }
    public List<Colheita> getColheitas() { return colheitas; }
    public List<Venda> getVendas() { return vendas; }

    public int novoIdUtilizador() { return seqUtilizador.incrementAndGet(); }
    public int novoIdFornecedor() { return seqFornecedor.incrementAndGet(); }
    public int novoIdProduto() { return seqProduto.incrementAndGet(); }
    public int novoIdCampanha() { return seqCampanha.incrementAndGet(); }
    public int novoIdCultivo() { return seqCultivo.incrementAndGet(); }
    public int novoIdColheita() { return seqColheita.incrementAndGet(); }
    public int novoIdVenda() { return seqVenda.incrementAndGet(); }
    public int novoNumeroFactura() { return seqFactura.incrementAndGet(); }

    /** Copia segura de uma lista sincronizada (para leitura/ordenacao sem bloquear). */
    public static <T> List<T> copia(List<T> lista) {
        synchronized (lista) {
            return new ArrayList<>(lista);
        }
    }
}
