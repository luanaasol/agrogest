<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Listagens" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Listagens</h1>

<nav class="separadores">
    <a href="${ctx}/admin/listagens?tipo=produtos" class="${tipo == 'produtos' ? 'ativo' : ''}">Produtos</a>
    <a href="${ctx}/admin/listagens?tipo=fornecedores" class="${tipo == 'fornecedores' ? 'ativo' : ''}">Fornecedores</a>
    <a href="${ctx}/admin/listagens?tipo=clientes" class="${tipo == 'clientes' ? 'ativo' : ''}">Clientes</a>
    <a href="${ctx}/admin/listagens?tipo=armazem" class="${tipo == 'armazem' ? 'ativo' : ''}">Armazém</a>
    <a href="${ctx}/admin/listagens?tipo=compras" class="${tipo == 'compras' ? 'ativo' : ''}">Compras detalhadas</a>
</nav>

<%-- Opcoes de ordenacao disponiveis para cada listagem --%>
<div class="ordenacao">
    <span>Ordenar por:</span>
    <c:set var="base" value="${ctx}/admin/listagens?tipo=${tipo}&ordem=" />
    <c:choose>
        <c:when test="${tipo == 'produtos' or tipo == 'armazem'}">
            <a href="${base}nome" class="${ordem == 'nome' ? 'ativo' : ''}">Nome</a>
            <a href="${base}preco" class="${ordem == 'preco' ? 'ativo' : ''}">Preço</a>
            <a href="${base}stock" class="${ordem == 'stock' ? 'ativo' : ''}">Stock</a>
            <a href="${base}data" class="${ordem == 'data' ? 'ativo' : ''}">Validade</a>
        </c:when>
        <c:when test="${tipo == 'compras'}">
            <a href="${base}data" class="${ordem != 'valor' ? 'ativo' : ''}">Data</a>
            <a href="${base}valor" class="${ordem == 'valor' ? 'ativo' : ''}">Valor</a>
        </c:when>
        <c:otherwise>
            <a href="${base}" class="${ordem != 'nome' ? 'ativo' : ''}">Registo (ID)</a>
            <a href="${base}nome" class="${ordem == 'nome' ? 'ativo' : ''}">Nome</a>
        </c:otherwise>
    </c:choose>
</div>

<c:choose>
    <%-- Produtos e produtos em armazem (stock > 0) --%>
    <c:when test="${tipo == 'produtos' or tipo == 'armazem'}">
        <c:if test="${tipo == 'armazem'}"><p class="nota">Produtos com stock maior que zero.</p></c:if>
        <c:choose>
            <c:when test="${empty produtos}"><p class="vazio">Sem produtos para mostrar.</p></c:when>
            <c:otherwise>
                <div class="tabela-wrap">
                    <table class="tabela">
                        <thead><tr><th>ID</th><th>Nome</th><th>Categoria</th><th>Fornecedor</th><th class="num">Preço</th><th class="num">Stock</th><th>Validade</th></tr></thead>
                        <tbody>
                        <c:forEach var="p" items="${produtos}">
                            <tr>
                                <td>${p.id}</td>
                                <td><c:out value="${p.nome}" /></td>
                                <td>${p.categoria}</td>
                                <td><c:out value="${p.fornecedorNome}" /></td>
                                <td class="num"><fmt:formatNumber value="${p.preco}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                                <td class="num">${p.quantidade} <c:out value="${p.unidade}" /></td>
                                <td>${p.validadeFmt}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </c:when>

    <c:when test="${tipo == 'fornecedores'}">
        <c:choose>
            <c:when test="${empty fornecedores}"><p class="vazio">Sem fornecedores para mostrar.</p></c:when>
            <c:otherwise>
                <div class="tabela-wrap">
                    <table class="tabela">
                        <thead><tr><th>ID</th><th>Nome</th><th>Tipo</th><th>Contacto</th><th>Província</th></tr></thead>
                        <tbody>
                        <c:forEach var="f" items="${fornecedores}">
                            <tr><td>${f.id}</td><td><c:out value="${f.nome}" /></td><td>${f.tipo}</td>
                                <td><c:out value="${f.contacto}" /></td><td><c:out value="${f.provincia}" /></td></tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </c:when>

    <c:when test="${tipo == 'clientes'}">
        <c:choose>
            <c:when test="${empty clientes}"><p class="vazio">Ainda não há clientes registados.</p></c:when>
            <c:otherwise>
                <div class="tabela-wrap">
                    <table class="tabela">
                        <thead><tr><th>ID</th><th>Nome</th><th>Email</th><th>Telefone</th><th>Endereço</th><th>Estado</th><th></th></tr></thead>
                        <tbody>
                        <c:forEach var="cl" items="${clientes}">
                            <tr>
                                <td>${cl.id}</td>
                                <td><c:out value="${cl.nome}" /></td>
                                <td><c:out value="${cl.email}" /></td>
                                <td><c:out value="${cl.telefone}" /></td>
                                <td><c:out value="${cl.endereco}" /></td>
                                <td><span class="estado ${cl.ativo ? 'aberta' : 'fechada'}">${cl.ativo ? 'Ativo' : 'Inativo'}</span></td>
                                <td>
                                    <form method="post" action="${ctx}/admin/listagens">
                                        <input type="hidden" name="acao" value="alternarCliente">
                                        <input type="hidden" name="id" value="${cl.id}">
                                        <button type="submit" class="botao pequeno">${cl.ativo ? 'Desativar' : 'Ativar'}</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </c:when>

    <%-- Compras detalhadas: cliente, endereco, telefone, produtos, quantidades, valor unitario, subtotal, total e data --%>
    <c:when test="${tipo == 'compras'}">
        <c:if test="${empty vendas}"><p class="vazio">Ainda não há compras efetuadas.</p></c:if>
        <c:forEach var="v" items="${vendas}">
            <div class="cartao compra">
                <div class="compra-topo">
                    <div>
                        <h3>${v.numeroFactura}</h3>
                        <p class="nota">${v.dataFmt}</p>
                    </div>
                    <a class="botao pequeno" href="${ctx}/factura?id=${v.id}">Ver factura</a>
                </div>
                <p><strong><c:out value="${v.clienteNome}" /></strong> &middot; <c:out value="${v.clienteEmail}" /><br>
                    Endereço: <c:out value="${v.clienteEndereco}" /> &middot; Telefone: <c:out value="${v.clienteTelefone}" /></p>
                <div class="tabela-wrap">
                    <table class="tabela">
                        <thead><tr><th>Produto</th><th class="num">Quantidade</th><th class="num">Valor unit.</th><th class="num">Subtotal</th></tr></thead>
                        <tbody>
                        <c:forEach var="i" items="${v.itens}">
                            <tr>
                                <td><c:out value="${i.produtoNome}" /></td>
                                <td class="num">${i.quantidade} <c:out value="${i.unidade}" /></td>
                                <td class="num"><fmt:formatNumber value="${i.precoUnit}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                                <td class="num"><fmt:formatNumber value="${i.subtotal}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                        <tfoot>
                        <tr><th colspan="3">Subtotal</th><th class="num"><fmt:formatNumber value="${v.subtotal}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
                        <tr><th colspan="3">IVA (16%)</th><th class="num"><fmt:formatNumber value="${v.iva}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
                        <tr class="total"><th colspan="3">Total</th><th class="num"><fmt:formatNumber value="${v.total}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </c:forEach>
    </c:when>
</c:choose>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
