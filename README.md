# AgroGest - Sistema de Gestão Agrícola

Aplicação web em **Java 17 (Jakarta Servlets 6.0 + JSP)** publicada no **Apache Tomcat 10.1**
(porta **8081**), desenvolvida para a cadeira de **Sistemas Distribuídos** (Engenharia Informática,
ISCTEM, Maputo, 2026).

O sistema ajuda um agricultor da província de Nampula a gerir fornecedores, produtos em stock,
campanhas agrícolas, cultivos e colheitas, e a vender produtos a clientes com emissão de factura.
Não há base de dados: os dados vivem em **listas em memória (RAM)** e são **serializados para um
ficheiro `.ser`**.

## 1. Grupo

| Nome | Número |
|---|---|
| Aashir Omar | 20240143 |
| Hussná Zulficar | 20240755 |
| Luana Machel | 20220329 |
| Nanila Badrú | 20240916 |

## 2. Tecnologias

| Componente | Versão / Nota |
|---|---|
| Java (JDK) | 17 |
| Apache Tomcat | 10.1 (porta 8081) |
| Jakarta Servlet / JSP / JSTL | 6.0 / 3.1 / 3.0 (pacotes `jakarta.*`) |
| Apache Maven | 3.8 ou superior (compilação e geração do WAR) |
| Interface | JSP + JSTL + EL + HTML + CSS |
| Sistema de deploy | Ubuntu Server 24.04 LTS (VirtualBox) com systemd |

Não são usados frameworks (nem Spring, nem outros), nem base de dados.

**A interface não usa JavaScript.** Tudo é feito com formulários HTML, links e CSS:

- confirmações (remover, eliminar o ficheiro de estado) numa página própria, `confirmar.jsp`;
- filtro do catálogo com um botão "Filtrar";
- menu para telemóvel feito só com CSS (checkbox escondida);
- impressão da factura com Ctrl+P (o CSS de impressão esconde o menu e os botões).

## 3. Credenciais de teste

| Perfil | Email | Palavra-passe |
|---|---|---|
| Administrador (conta fixa) | `admin@agro.mz` | `admin234` |
| Cliente (2 compras) | `ana.macuacua@exemplo.mz` | `1234` |
| Cliente (1 compra) | `carlos.nhantumbo@exemplo.mz` | `1234` |
| Cliente (1 compra) | `fatima.abdula@exemplo.mz` | `1234` |
| Cliente (sem compras) | `joaquim.mabunda@exemplo.mz` | `1234` |
| Cliente (sem compras) | `rosa.chissano@exemplo.mz` | `1234` |

Novos clientes podem registar-se no ecrã **Criar conta**.

### Dados de exemplo

Criados apenas no **primeiro arranque** (quando ainda não existe o ficheiro `.ser`), em
`AppListener.dadosIniciais()`:

- **Fornecedores:** AgroSementes Nampula (sementes), Fertimoc, Lda (fertilizantes),
  Ferragens da Beira, Lda (ferramentas).
- **Produtos (com foto):** Milho (60 MT/kg), Adubo NPK 12-24-12 (2 500 MT/saco),
  Enxada (450 MT/unidade).
- **Vendas:** 4 compras feitas pelos clientes acima, com facturas FT-2026-1 a FT-2026-4.
- **Campanha 2025/2026** (fechada): cultivo de milho (variedade Matuba, 5 ha) e uma colheita
  de 10 000 kg a 18 MT/kg.

## 4. Arquitetura

Padrão **MVC** em camadas:

```
Navegador --HTTP :8081--> Apache Tomcat 10.1
                            |- FILTER    AuthFilter (sessão + perfil)
                            |- SERVLETS  Controladores (Login, Registo, Catálogo, Carrinho,
                            |            Compra, Factura, Foto, Admin*)
                            |- SERVICES  Regras de negócio (VendaService com synchronized)
                            |- REPO      DataStore (listas em memória) + Persistencia (.ser)
                            |- VIEWS     JSP + JSTL + EL + CSS
```

### Pacotes (`src/main/java/mz/isctem/agrogest`)

| Pacote | Responsabilidade |
|---|---|
| `model` | Classes serializáveis: Utilizador, Fornecedor, Produto, Campanha, Cultivo, Colheita, Venda, VendaItem, Carrinho, ItemCarrinho e enumerações |
| `repo` | `DataStore` (listas `Collections.synchronizedList`, IDs com `AtomicInteger`) e `Persistencia` (ObjectOutputStream / ObjectInputStream, escrita atómica) |
| `service` | Regras de negócio e validações (um serviço por entidade, `VendaService`, `RelatorioService`) |
| `servlet` | Controladores anotados com `@WebServlet` (`BaseServlet` tem o código comum) |
| `filter` | `AuthFilter` (`@WebFilter`): sessão obrigatória e acesso exclusivo do admin a `/admin/*` |
| `listener` | `AppListener` (`@WebListener`): restaura o estado no arranque, grava no encerramento e cria os dados de exemplo |
| `util` | `PasswordUtil` (hash SHA-256 com salt aleatório) e `Validacao` |

As vistas JSP estão em `src/main/webapp/WEB-INF/views` (não são acessíveis diretamente pelo
navegador). O CSS está em `src/main/webapp/css/estilo.css` e as fotos dos produtos iniciais em
`src/main/webapp/img`.

### Modelo de domínio

```
Utilizador 1 --- N Venda 1 --- N VendaItem N --- 1 Produto N --- 1 Fornecedor
Campanha 1 --- N Cultivo 1 --- N Colheita
```

## 5. Regras de negócio (RN01 a RN06)

As regras do enunciado (alíneas a) a f)) estão marcadas no código com comentários `RN01` a `RN06`.

| Regra | Onde está |
|---|---|
| RN01: não vender mais do que o stock | `CarrinhoService` avisa ao adicionar; `VendaService.finalizarCompra` volta a verificar no momento da compra |
| RN02: venda atómica (tudo ou nada) | `VendaService.finalizarCompra`: valida todos os itens dentro de `synchronized` e só depois desconta o stock de todos |
| RN03: o cliente só vê as suas facturas | `FacturaServlet` devolve HTTP 403 se a factura for de outro cliente; o admin vê todas |
| RN04: `/admin/*` exclusivo do admin | `AuthFilter` devolve HTTP 403 |
| RN05: rotas de cliente exigem sessão | `AuthFilter` redireciona para `/login` em `/catalogo`, `/carrinho/*`, `/compra`, `/factura`, `/minhas-compras` |
| RN06: IDs sequenciais e únicos | `AtomicInteger` por entidade no `DataStore` (os contadores também são gravados no `.ser`) |

Outras validações: não remover campanhas com cultivos, cultivos com colheitas nem fornecedores com
produtos; email único no registo; datas e valores numéricos validados; só registar cultivos em
campanhas abertas.

## 6. Compilar (gerar o WAR)

```bash
mvn clean package
```

O ficheiro gerado é `target/agrogest.war`.

## 7. Executar no Windows

Com o JDK 17 em `C:\tools\jdk17`, o Maven em `C:\tools\maven` e o Tomcat 10.1 em `C:\tools\tomcat10`
(porta já mudada para 8081), basta correr na pasta do projeto:

```bat
run-local.bat
```

O script compila, para o Tomcat (o que grava o estado), publica o WAR e volta a arrancar.

Passos manuais equivalentes:

1. Mudar a porta HTTP para **8081** em `conf/server.xml` do Tomcat
   (`<Connector port="8081" protocol="HTTP/1.1" ...`).
2. Copiar `target/agrogest.war` para a pasta `webapps/` do Tomcat.
3. Iniciar: `bin\startup.bat`.
4. Abrir: **http://localhost:8081/agrogest**
5. Parar: `bin\shutdown.bat` (o estado é gravado automaticamente).

## 8. Deploy em Ubuntu (VirtualBox, systemd)

O guia completo está em [`deploy/DEPLOY.md`](deploy/DEPLOY.md). Resumo:

```bash
sudo bash install-tomcat.sh          # Java 17 + Tomcat 10.1 em /opt/tomcat, porta 8081, serviço systemd
sudo bash deploy-war.sh agrogest.war # publica o WAR e reinicia o serviço
sudo systemctl status tomcat         # estado do serviço
sudo systemctl restart tomcat        # reinicia (grava e restaura o estado)
journalctl -u tomcat -f              # registos
```

Abrir em qualquer dispositivo da mesma rede: `http://IP-DA-VM:8081/agrogest` (IP visto com `ip a`).

## 9. Persistência em ficheiro

- Ficheiro: `CATALINA_BASE/data/agrogest.ser` (Windows: `C:\tools\tomcat10\data\agrogest.ser`;
  Ubuntu: `/opt/tomcat/data/agrogest.ser`).
- **Gravação automática** quando o Tomcat é parado e **restauro automático** no arranque.
- **Manual**: menu *Persistência* ("Gravar agora" e "Eliminar ficheiro", com página de confirmação).
- **Escrita atómica**: o estado é escrito num ficheiro temporário que só depois substitui o anterior.
  Se o ficheiro estiver corrompido, é renomeado para `.corrompido` e o sistema arranca com estado limpo.
- Ficam gravados os dados, os contadores de IDs e a sequência das facturas.
- Outro local: arrancar o Tomcat com `-Dagrogest.data=/caminho/agrogest.ser`.

## 10. Fotos dos produtos

No ecrã **Produtos** (admin) a foto pode ser indicada de duas formas:

- **Carregar um ficheiro** (JPEG, PNG, WEBP ou GIF, até 2 MB). O formulário usa
  `multipart/form-data` e o servlet usa `@MultipartConfig`. A imagem fica em memória dentro do
  produto e é gravada no `.ser` juntamente com o resto. É servida por `FotoServlet` em `/foto?id=`.
- **Endereço (URL)** de uma imagem.

## 11. Rotas principais

| Rota | Acesso | Descrição |
|---|---|---|
| `/login`, `/registo`, `/logout` | público | Autenticação e registo de clientes |
| `/catalogo` | cliente | Catálogo com filtro por categoria |
| `/carrinho` (+ `/adicionar`, `/atualizar`, `/remover`, `/esvaziar`) | cliente | Carrinho em sessão |
| `/compra` | cliente | Confirmar compra e emitir factura |
| `/factura?id=` | cliente / admin | Ver e imprimir factura |
| `/minhas-compras` | cliente | Histórico de compras |
| `/foto?id=` | público | Foto carregada de um produto |
| `/admin/dashboard` | admin | Painel com totais |
| `/admin/fornecedores`, `/admin/produtos`, `/admin/campanhas`, `/admin/cultivos`, `/admin/colheitas` | admin | Gestão |
| `/admin/listagens?tipo=&ordem=` | admin | Produtos, fornecedores, clientes, armazém, compras detalhadas |
| `/admin/relatorios` | admin | Relatórios de clientes |
| `/admin/persistencia` | admin | Gravar / eliminar o ficheiro de estado |

## 12. Factura

Número sequencial no formato `FT-AAAA-N` (ex.: `FT-2026-5`), dados do cliente, itens, subtotal,
**IVA de 16%** e total. Para imprimir usa-se Ctrl+P: o CSS de impressão mostra só a factura,
a preto e branco, em A4.
