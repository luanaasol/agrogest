package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Carrinho;
import mz.isctem.agrogest.model.ItemCarrinho;
import mz.isctem.agrogest.model.Produto;
import mz.isctem.agrogest.model.Utilizador;
import mz.isctem.agrogest.model.Venda;
import mz.isctem.agrogest.model.VendaItem;
import mz.isctem.agrogest.repo.DataStore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RN01 + RN02: venda transacional (synchronized): verifica o stock de TODOS os itens e so depois desconta,
 * de forma atomica (tudo ou nada). Emite a factura com numero sequencial FT-AAAA-N e IVA de 16%.
 */
public class VendaService {

    public static final BigDecimal TAXA_IVA = new BigDecimal("0.16");

    private DataStore ds() {
        return DataStore.get();
    }

    public Venda finalizarCompra(Utilizador cliente, Carrinho carrinho) {
        if (carrinho == null || carrinho.isVazio()) throw new NegocioException("O carrinho está vazio.");
        DataStore ds = ds();

        // Seccao critica: nenhuma outra escrita acontece enquanto a venda e processada
        synchronized (ds) {
            // 1) RN01: validar todos os itens (o produto existe e ha stock suficiente)
            List<Produto> produtos = new ArrayList<>();
            for (ItemCarrinho item : carrinho.getItens()) {
                Produto p = procurarProduto(ds, item.getProdutoId());
                if (p == null) {
                    throw new NegocioException("Indisponível: o produto \"" + item.getProdutoNome() + "\" já não existe.");
                }
                if (item.getQuantidade() > p.getQuantidade()) {
                    throw new NegocioException("Indisponível: stock insuficiente para \"" + p.getNome()
                            + "\" (pedido: " + item.getQuantidade() + ", disponível: " + p.getQuantidade() + ").");
                }
                produtos.add(p);
            }

            // 2) RN02: so com todos validos se desconta o stock (tudo ou nada); o lock impede
            //    que dois clientes comprem ao mesmo tempo a ultima unidade. Todos validos: descontar o stock de todos os itens e montar a venda
            Venda v = new Venda();
            BigDecimal subtotal = BigDecimal.ZERO;
            for (int i = 0; i < produtos.size(); i++) {
                Produto p = produtos.get(i);
                ItemCarrinho item = carrinho.getItens().get(i);
                p.setQuantidade(p.getQuantidade() - item.getQuantidade());
                VendaItem vi = new VendaItem(p.getId(), p.getNome(), p.getUnidade(), item.getQuantidade(), p.getPreco());
                v.getItens().add(vi);
                subtotal = subtotal.add(vi.getSubtotal());
            }

            subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
            BigDecimal iva = subtotal.multiply(TAXA_IVA).setScale(2, RoundingMode.HALF_UP);
            LocalDateTime agora = LocalDateTime.now();

            v.setId(ds.novoIdVenda());
            v.setNumeroFactura("FT-" + agora.getYear() + "-" + ds.novoNumeroFactura());
            v.setData(agora);
            v.setClienteId(cliente.getId());
            v.setClienteNome(cliente.getNome());
            v.setClienteEmail(cliente.getEmail());
            v.setClienteTelefone(cliente.getTelefone());
            v.setClienteEndereco(cliente.getEndereco());
            v.setSubtotal(subtotal);
            v.setIva(iva);
            v.setTotal(subtotal.add(iva));
            ds.getVendas().add(v);

            carrinho.esvaziar();
            return v;
        }
    }

    private Produto procurarProduto(DataStore ds, int id) {
        synchronized (ds.getProdutos()) {
            for (Produto p : ds.getProdutos()) {
                if (p.getId() == id) return p;
            }
        }
        return null;
    }

    public List<Venda> listar() {
        return DataStore.copia(ds().getVendas());
    }

    /** Compras efetuadas, ordenadas por data ou valor (por omissao, mais recentes primeiro). */
    public List<Venda> listar(String ordem) {
        List<Venda> l = listar();
        if ("valor".equals(ordem)) l.sort(Comparator.comparing(Venda::getTotal).reversed());
        else l.sort(Comparator.comparing(Venda::getData).reversed());
        return l;
    }

    public List<Venda> listarPorCliente(int clienteId) {
        return listar().stream()
                .filter(v -> v.getClienteId() == clienteId)
                .sorted(Comparator.comparing(Venda::getData).reversed())
                .collect(Collectors.toList());
    }

    public Venda procurar(int id) {
        for (Venda v : listar()) {
            if (v.getId() == id) return v;
        }
        return null;
    }
}
