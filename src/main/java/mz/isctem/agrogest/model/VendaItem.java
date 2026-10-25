package mz.isctem.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;

/** Linha de uma venda. Guarda o nome e o preco do produto no momento da compra. */
public class VendaItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int produtoId;
    private String produtoNome;
    private String unidade;
    private int quantidade;
    private BigDecimal precoUnit;

    public VendaItem() {
    }

    public VendaItem(int produtoId, String produtoNome, String unidade, int quantidade, BigDecimal precoUnit) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.unidade = unidade;
        this.quantidade = quantidade;
        this.precoUnit = precoUnit;
    }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnit() { return precoUnit; }
    public void setPrecoUnit(BigDecimal precoUnit) { this.precoUnit = precoUnit; }

    /** Subtotal da linha: quantidade x preco unitario. */
    public BigDecimal getSubtotal() {
        return precoUnit.multiply(BigDecimal.valueOf(quantidade));
    }
}
