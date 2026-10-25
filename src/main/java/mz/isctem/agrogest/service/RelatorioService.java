package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Utilizador;
import mz.isctem.agrogest.model.Venda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Relatorios de clientes e vendas pedidos no enunciado. */
public class RelatorioService {

    private final UtilizadorService utilizadorService = new UtilizadorService();
    private final VendaService vendaService = new VendaService();

    /** Linha de relatorio: cliente, numero de compras e volume (total gasto). */
    public static class LinhaCliente {
        private final Utilizador cliente;
        private final int numCompras;
        private final BigDecimal volume;

        public LinhaCliente(Utilizador cliente, int numCompras, BigDecimal volume) {
            this.cliente = cliente;
            this.numCompras = numCompras;
            this.volume = volume;
        }

        public Utilizador getCliente() { return cliente; }
        public int getNumCompras() { return numCompras; }
        public BigDecimal getVolume() { return volume; }
    }

    private List<LinhaCliente> linhas() {
        Map<Integer, Integer> contagem = new HashMap<>();
        Map<Integer, BigDecimal> volume = new HashMap<>();
        for (Venda v : vendaService.listar()) {
            contagem.merge(v.getClienteId(), 1, Integer::sum);
            volume.merge(v.getClienteId(), v.getTotal(), BigDecimal::add);
        }
        List<LinhaCliente> l = new ArrayList<>();
        for (Utilizador c : utilizadorService.listarClientes()) {
            l.add(new LinhaCliente(c, contagem.getOrDefault(c.getId(), 0),
                    volume.getOrDefault(c.getId(), BigDecimal.ZERO)));
        }
        return l;
    }

    /** Clientes ordenados por nome (crescente). */
    public List<LinhaCliente> clientesPorNome() {
        List<LinhaCliente> l = linhas();
        l.sort(Comparator.comparing(x -> x.getCliente().getNome(), String.CASE_INSENSITIVE_ORDER));
        return l;
    }

    /** Clientes que compraram, ordenados por volume (total gasto, decrescente). */
    public List<LinhaCliente> clientesPorVolume() {
        List<LinhaCliente> l = new ArrayList<>(linhas());
        l.removeIf(x -> x.getNumCompras() == 0);
        l.sort(Comparator.comparing(LinhaCliente::getVolume).reversed());
        return l;
    }

    /** Clientes mais assiduos: ranking por numero de compras (decrescente). */
    public List<LinhaCliente> clientesMaisAssiduos() {
        List<LinhaCliente> l = new ArrayList<>(linhas());
        l.removeIf(x -> x.getNumCompras() == 0);
        l.sort(Comparator.comparingInt(LinhaCliente::getNumCompras).reversed()
                .thenComparing(LinhaCliente::getVolume, Comparator.reverseOrder()));
        return l;
    }

    /** (extra) Clientes sem compras. */
    public List<LinhaCliente> clientesSemCompras() {
        List<LinhaCliente> l = new ArrayList<>(linhas());
        l.removeIf(x -> x.getNumCompras() > 0);
        l.sort(Comparator.comparing(x -> x.getCliente().getNome(), String.CASE_INSENSITIVE_ORDER));
        return l;
    }
}
