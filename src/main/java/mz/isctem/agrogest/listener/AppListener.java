package mz.isctem.agrogest.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mz.isctem.agrogest.model.Campanha;
import mz.isctem.agrogest.model.Carrinho;
import mz.isctem.agrogest.model.Categoria;
import mz.isctem.agrogest.model.Cultivo;
import mz.isctem.agrogest.model.Fornecedor;
import mz.isctem.agrogest.model.Produto;
import mz.isctem.agrogest.model.TipoFornecedor;
import mz.isctem.agrogest.model.Utilizador;
import mz.isctem.agrogest.model.Venda;
import mz.isctem.agrogest.repo.DataStore;
import mz.isctem.agrogest.repo.Persistencia;
import mz.isctem.agrogest.service.CampanhaService;
import mz.isctem.agrogest.service.CarrinhoService;
import mz.isctem.agrogest.service.ColheitaService;
import mz.isctem.agrogest.service.CultivoService;
import mz.isctem.agrogest.service.FornecedorService;
import mz.isctem.agrogest.service.ProdutoService;
import mz.isctem.agrogest.service.UtilizadorService;
import mz.isctem.agrogest.service.VendaService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ciclo de vida da aplicacao:
 * - arranque: restaura o estado do ficheiro (se existir) e garante a conta fixa de administrador;
 * - encerramento do Tomcat: grava automaticamente o estado no ficheiro.
 */
@WebListener
public class AppListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppListener.class.getName());

    /** Palavra-passe dos clientes de exemplo. */
    private static final String PASS_EXEMPLO = "1234";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataStore restaurado = Persistencia.carregar();
        if (restaurado != null) {
            DataStore.substituir(restaurado);
            new UtilizadorService().garantirAdmin();
        } else {
            DataStore.substituir(new DataStore());
            // Admin primeiro, para ficar com o ID 1
            new UtilizadorService().garantirAdmin();
            dadosIniciais();
        }
        LOG.info("AgroGest iniciado. Ficheiro de estado: " + Persistencia.ficheiro());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Persistencia.gravar(DataStore.get());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "AgroGest: falha ao gravar o estado no encerramento", e);
        }
    }

    /**
     * Dados de demonstracao (apenas quando nao existe ficheiro gravado), iguais ao exemplo do enunciado:
     * 3 fornecedores | 3 produtos | 5 clientes | 4 vendas | 1 campanha.
     * Tudo e criado pelos mesmos servicos da aplicacao, para respeitar as validacoes e descontar o stock.
     */
    private void dadosIniciais() {
        FornecedorService fs = new FornecedorService();
        ProdutoService ps = new ProdutoService();

        // ----- 3 fornecedores -----
        Fornecedor f1 = fs.criar("AgroSementes Nampula", TipoFornecedor.SEMENTES.name(), "+258 84 111 2233", "Nampula");
        Fornecedor f2 = fs.criar("Fertimoc, Lda", TipoFornecedor.FERTILIZANTES.name(), "+258 82 444 5566", "Maputo");
        Fornecedor f3 = fs.criar("Ferragens da Beira, Lda", TipoFornecedor.FERRAMENTAS.name(), "+258 86 777 8899", "Sofala (Beira)");

        // ----- 3 produtos (fotos locais em /img: funcionam sem internet) -----
        String val = LocalDate.now().plusMonths(12).toString();
        Produto milho = ps.criar("Milho", Categoria.SEMENTE.name(), String.valueOf(f1.getId()), "60", "500", "kg",
                "Semente de milho de polinização aberta, adaptada ao clima do norte.", "img/milho.jpg", val);
        Produto adubo = ps.criar("Adubo NPK 12-24-12", Categoria.FERTILIZANTE.name(), String.valueOf(f2.getId()), "2500", "80", "saco",
                "Saco de 50 kg de adubo composto para adubação de fundo.", "img/adubo.jpg", val);
        Produto enxada = ps.criar("Enxada", Categoria.FERRAMENTA.name(), String.valueOf(f3.getId()), "450", "40", "unidade",
                "Enxada com cabo de madeira para preparação do solo.", "img/enxada.jpg",
                LocalDate.now().plusYears(5).toString());

        // ----- 5 clientes (palavra-passe 1234) -----
        UtilizadorService us = new UtilizadorService();
        Utilizador ana = us.registarCliente("Ana Macuácua", "ana.macuacua@exemplo.mz", PASS_EXEMPLO,
                "+258 84 120 3344", "Bairro de Muahivire, Nampula");
        Utilizador carlos = us.registarCliente("Carlos Nhantumbo", "carlos.nhantumbo@exemplo.mz", PASS_EXEMPLO,
                "+258 82 233 4455", "Av. 25 de Setembro, Maputo");
        Utilizador fatima = us.registarCliente("Fátima Abdula", "fatima.abdula@exemplo.mz", PASS_EXEMPLO,
                "+258 86 344 5566", "Rua dos Continuadores, Nacala");
        us.registarCliente("Joaquim Mabunda", "joaquim.mabunda@exemplo.mz", PASS_EXEMPLO,
                "+258 84 455 6677", "Bairro do Chamanculo, Maputo");
        us.registarCliente("Rosa Chissano", "rosa.chissano@exemplo.mz", PASS_EXEMPLO,
                "+258 87 566 7788", "Av. Samora Machel, Beira");
        // Joaquim e Rosa ficam sem compras (relatorio extra "clientes sem compras")

        // ----- 4 vendas, pelo VendaService (desconta stock e emite factura FT-ano-n) -----
        // A Ana faz 2 compras, para os relatorios de volume e de assiduidade terem diferencas.
        venda(ana, 12, milho.getId(), 10, adubo.getId(), 2);
        venda(carlos, 9, adubo.getId(), 1, enxada.getId(), 2);
        venda(ana, 5, enxada.getId(), 5, 0, 0);
        venda(fatima, 2, milho.getId(), 20, 0, 0);

        // ----- 1 campanha (ja terminada) com um cultivo e uma colheita -----
        CampanhaService cs = new CampanhaService();
        Campanha camp = cs.criar("Campanha 2025/2026", "2025-10-01", "2026-06-30");
        Cultivo cultivo = new CultivoService().criar(String.valueOf(camp.getId()), "Milho", "Matuba", "5", "2025-11-20");
        new ColheitaService().criar(String.valueOf(cultivo.getId()), "2026-04-15", "10000", "18");
        cs.fechar(camp.getId());
    }

    /** Cria uma venda de exemplo com 1 ou 2 itens e coloca a data "diasAtras" dias no passado. */
    private void venda(Utilizador cliente, int diasAtras, int prod1, int qtd1, int prod2, int qtd2) {
        CarrinhoService carrinhoService = new CarrinhoService();
        Carrinho carrinho = new Carrinho();
        carrinhoService.adicionar(carrinho, prod1, String.valueOf(qtd1));
        if (prod2 > 0) carrinhoService.adicionar(carrinho, prod2, String.valueOf(qtd2));
        Venda v = new VendaService().finalizarCompra(cliente, carrinho);
        // Datas diferentes para a ordenacao por data das compras fazer sentido na demonstracao
        v.setData(LocalDateTime.now().minusDays(diasAtras).withHour(10).withMinute(30).withSecond(0).withNano(0));
    }
}
