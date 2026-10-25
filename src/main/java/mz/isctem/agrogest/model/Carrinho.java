package mz.isctem.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Carrinho de compras do cliente. Vive na sessao HTTP. */
public class Carrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<ItemCarrinho> itens = new ArrayList<>();

    public List<ItemCarrinho> getItens() { return itens; }

    public ItemCarrinho procurar(int produtoId) {
        for (ItemCarrinho i : itens) {
            if (i.getProdutoId() == produtoId) return i;
        }
        return null;
    }

    public void remover(int produtoId) {
        itens.removeIf(i -> i.getProdutoId() == produtoId);
    }

    public void esvaziar() {
        itens.clear();
    }

    public boolean isVazio() {
        return itens.isEmpty();
    }

    public int getNumeroItens() {
        return itens.size();
    }

    public BigDecimal getTotal() {
        BigDecimal t = BigDecimal.ZERO;
        for (ItemCarrinho i : itens) t = t.add(i.getSubtotal());
        return t;
    }
}
