package mz.isctem.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;

/** Linha do carrinho de compras (guardado na sessao HTTP). */
public class ItemCarrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    private int produtoId;
    private String produtoNome;
    private String unidade;
    private BigDecimal precoUnit;
    private int quantidade;

    public ItemCarrinho() {
    }

    public ItemCarrinho(int produtoId, String produtoNome, String unidade, BigDecimal precoUnit, int quantidade) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.unidade = unidade;
        this.precoUnit = precoUnit;
        this.quantidade = quantidade;
    }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public BigDecimal getPrecoUnit() { return precoUnit; }
    public void setPrecoUnit(BigDecimal precoUnit) { this.precoUnit = precoUnit; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getSubtotal() {
        return precoUnit.multiply(BigDecimal.valueOf(quantidade));
    }
}
