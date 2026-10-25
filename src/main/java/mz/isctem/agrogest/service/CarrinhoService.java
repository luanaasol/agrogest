package mz.isctem.agrogest.service;

import mz.isctem.agrogest.model.Carrinho;
import mz.isctem.agrogest.model.ItemCarrinho;
import mz.isctem.agrogest.model.Produto;
import mz.isctem.agrogest.util.Validacao;

/** Operacoes sobre o carrinho guardado na sessao: adicionar, atualizar, remover e esvaziar. */
public class CarrinhoService {

    private final ProdutoService produtoService = new ProdutoService();

    public void adicionar(Carrinho carrinho, int produtoId, String quantidade) {
        int q = Validacao.inteiroPositivo(quantidade, "Quantidade");
        Produto p = produtoService.procurar(produtoId);
        if (p == null) throw new NegocioException("O produto não existe.");
        ItemCarrinho item = carrinho.procurar(produtoId);
        int total = (item == null ? 0 : item.getQuantidade()) + q;
        // RN01: aviso antecipado; a verificacao definitiva e feita no VendaService
        if (total > p.getQuantidade()) {
            throw new NegocioException("Stock insuficiente para \"" + p.getNome() + "\". Disponível: "
                    + p.getQuantidade() + " " + p.getUnidade() + ".");
        }
        if (item == null) {
            carrinho.getItens().add(new ItemCarrinho(p.getId(), p.getNome(), p.getUnidade(), p.getPreco(), q));
        } else {
            item.setQuantidade(total);
        }
    }

    public void atualizar(Carrinho carrinho, int produtoId, String quantidade) {
        int q = Validacao.inteiroPositivo(quantidade, "Quantidade");
        ItemCarrinho item = carrinho.procurar(produtoId);
        if (item == null) throw new NegocioException("O item não está no carrinho.");
        Produto p = produtoService.procurar(produtoId);
        if (p == null) throw new NegocioException("O produto \"" + item.getProdutoNome() + "\" já não existe.");
        if (q > p.getQuantidade()) {
            throw new NegocioException("Stock insuficiente para \"" + p.getNome() + "\". Disponível: "
                    + p.getQuantidade() + " " + p.getUnidade() + ".");
        }
        item.setQuantidade(q);
    }

    public void remover(Carrinho carrinho, int produtoId) {
        carrinho.remover(produtoId);
    }

    public void esvaziar(Carrinho carrinho) {
        carrinho.esvaziar();
    }
}
