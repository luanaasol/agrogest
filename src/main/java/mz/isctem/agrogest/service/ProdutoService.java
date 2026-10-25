package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Categoria;
import mz.isctem.agrogest.model.Fornecedor;
import mz.isctem.agrogest.model.Produto;
import mz.isctem.agrogest.repo.DataStore;
import mz.isctem.agrogest.util.Validacao;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Gestao de produtos: criar, listar, repor stock e remover. */
public class ProdutoService {

    private final FornecedorService fornecedorService = new FornecedorService();

    private DataStore ds() {
        return DataStore.get();
    }

    /** Tamanho maximo de uma imagem carregada (fica em memoria e no ficheiro .ser). */
    public static final int FOTO_MAX_BYTES = 2 * 1024 * 1024;

    public Produto criar(String nome, String categoria, String fornecedorId, String preco, String quantidade,
                         String unidade, String descricao, String foto, String validade) {
        return criar(nome, categoria, fornecedorId, preco, quantidade, unidade, descricao, foto, validade, null, null);
    }

    /** Criar produto com imagem carregada (opcional). Aceita apenas JPEG, PNG, WebP ou GIF ate 2 MB. */
    public Produto criar(String nome, String categoria, String fornecedorId, String preco, String quantidade,
                         String unidade, String descricao, String foto, String validade,
                         byte[] fotoDados, String fotoTipo) {
        Produto p = new Produto();
        p.setNome(Validacao.obrigatorio(nome, "Nome"));
        p.setCategoria(Validacao.enumeracao(Categoria.class, categoria, "Categoria"));
        Fornecedor f = fornecedorService.procurar(Validacao.inteiro(fornecedorId, "Fornecedor"));
        if (f == null) throw new NegocioException("Fornecedor inválido.");
        p.setFornecedorId(f.getId());
        p.setFornecedorNome(f.getNome());
        p.setPreco(Validacao.dinheiro(preco, "Preço"));
        p.setQuantidade(Validacao.inteiroNaoNegativo(quantidade, "Quantidade"));
        p.setUnidade(Validacao.obrigatorio(unidade, "Unidade"));
        p.setDescricao(Validacao.opcional(descricao));
        p.setFoto(Validacao.opcional(foto));
        LocalDate val = Validacao.data(validade, "Validade");
        p.setValidade(val);
        if (fotoDados != null && fotoDados.length > 0) {
            if (fotoDados.length > FOTO_MAX_BYTES) throw new NegocioException("A imagem não pode ter mais de 2 MB.");
            if (fotoTipo == null || !fotoTipo.matches("image/(jpeg|png|webp|gif)")) {
                throw new NegocioException("A imagem deve ser JPG, PNG, WebP ou GIF.");
            }
            p.setFotoDados(fotoDados);
            p.setFotoTipo(fotoTipo);
        }

        DataStore ds = ds();
        synchronized (ds) {
            p.setId(ds.novoIdProduto());
            ds.getProdutos().add(p);
        }
        return p;
    }

    public List<Produto> listar() {
        return DataStore.copia(ds().getProdutos());
    }

    public Produto procurar(int id) {
        for (Produto p : listar()) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    /** Catalogo do cliente, com filtro opcional por categoria. */
    public List<Produto> catalogo(String categoria) {
        List<Produto> l = listar();
        if (categoria != null && !categoria.isBlank()) {
            l = l.stream().filter(p -> p.getCategoria().name().equals(categoria)).collect(Collectors.toList());
        }
        l.sort(Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER));
        return l;
    }

    /** Produtos em armazem: stock maior que zero. */
    public List<Produto> emArmazem(String ordem) {
        List<Produto> l = listar().stream().filter(p -> p.getQuantidade() > 0).collect(Collectors.toList());
        ordenar(l, ordem);
        return l;
    }

    public List<Produto> listar(String ordem) {
        List<Produto> l = listar();
        ordenar(l, ordem);
        return l;
    }

    /** Ordenacao por nome, preco, stock ou validade (data). */
    private void ordenar(List<Produto> l, String ordem) {
        if (ordem == null) ordem = "";
        switch (ordem) {
            case "nome" -> l.sort(Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER));
            case "preco" -> l.sort(Comparator.comparing(Produto::getPreco));
            case "stock" -> l.sort(Comparator.comparingInt(Produto::getQuantidade));
            case "data" -> l.sort(Comparator.comparing(Produto::getValidade));
            default -> l.sort(Comparator.comparingInt(Produto::getId));
        }
    }

    /** Repor stock: soma a quantidade indicada ao stock atual. */
    public void reporStock(int id, String quantidade) {
        int q = Validacao.inteiroPositivo(quantidade, "Quantidade a repor");
        DataStore ds = ds();
        synchronized (ds) {
            Produto p = procurar(id);
            if (p == null) throw new NegocioException("Produto não encontrado.");
            p.setQuantidade(p.getQuantidade() + q);
        }
    }

    public void remover(int id) {
        DataStore ds = ds();
        synchronized (ds) {
            Produto p = procurar(id);
            if (p == null) throw new NegocioException("Produto não encontrado.");
            ds.getProdutos().remove(p);
        }
    }
}
