package mz.isctem.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Produto em stock (semente, fertilizante, maquinaria ou ferramenta). */
public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private Categoria categoria;
    private int fornecedorId;
    private String fornecedorNome;
    private BigDecimal preco;
    private int quantidade;      // stock disponivel
    private String unidade;      // kg, saco, unidade, ...
    private String descricao;
    private String foto;         // URL da imagem (ou caminho local, ex.: img/milho.jpg)
    private byte[] fotoDados;    // imagem carregada pelo admin: fica em memoria e vai para o .ser
    private String fotoTipo;     // tipo MIME da imagem carregada (image/jpeg, image/png, ...)
    private LocalDate validade;

    public Produto() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }

    public String getFornecedorNome() { return fornecedorNome; }
    public void setFornecedorNome(String fornecedorNome) { this.fornecedorNome = fornecedorNome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public byte[] getFotoDados() { return fotoDados; }
    public void setFotoDados(byte[] fotoDados) { this.fotoDados = fotoDados; }

    public String getFotoTipo() { return fotoTipo; }
    public void setFotoTipo(String fotoTipo) { this.fotoTipo = fotoTipo; }

    /** true se o admin carregou um ficheiro de imagem (tem prioridade sobre o URL). */
    public boolean isFotoCarregada() { return fotoDados != null && fotoDados.length > 0; }

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    /** Validade formatada (dd/MM/yyyy) para as vistas JSP. */
    public String getValidadeFmt() {
        return validade == null ? "-" : validade.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
