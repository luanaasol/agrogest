<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Factura ${venda.numeroFactura}" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<div class="acoes nao-imprimir">
    <c:choose>
        <c:when test="${u.admin}"><a class="botao" href="${ctx}/admin/listagens?tipo=compras">Voltar às compras</a></c:when>
        <c:otherwise><a class="botao" href="${ctx}/minhas-compras">Minhas compras</a></c:otherwise>
    </c:choose>
</div>
<%-- Sem JavaScript: a impressao usa o comando do navegador; a folha @media print esconde menus e botoes --%>
<p class="dica-imprimir nao-imprimir">Para imprimir a factura, use <kbd>Ctrl</kbd>+<kbd>P</kbd> (ou Imprimir no menu do navegador).</p>

<section class="factura">
    <div class="factura-cabecalho">
        <div>
            <h1>AgroGest</h1>
            <p>Sistema de Gestão Agrícola<br>Província de Nampula, Moçambique</p>
        </div>
        <div class="factura-num">
            <h2>FACTURA</h2>
            <p><strong>N.º ${venda.numeroFactura}</strong><br>Data: ${venda.dataFmt}</p>
        </div>
    </div>

    <div class="factura-cliente">
        <h3>Cliente</h3>
        <p><strong><c:out value="${venda.clienteNome}" /></strong><br>
            Email: <c:out value="${venda.clienteEmail}" /><br>
            Telefone: <c:out value="${venda.clienteTelefone}" /><br>
            Endereço: <c:out value="${venda.clienteEndereco}" /></p>
    </div>

    <table class="tabela">
        <thead><tr><th>Produto</th><th class="num">Quantidade</th><th class="num">Preço unit.</th><th class="num">Subtotal</th></tr></thead>
        <tbody>
        <c:forEach var="i" items="${venda.itens}">
            <tr>
                <td><c:out value="${i.produtoNome}" /></td>
                <td class="num">${i.quantidade} <c:out value="${i.unidade}" /></td>
                <td class="num"><fmt:formatNumber value="${i.precoUnit}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                <td class="num"><fmt:formatNumber value="${i.subtotal}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr><th colspan="3">Subtotal</th><th class="num"><fmt:formatNumber value="${venda.subtotal}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
        <tr><th colspan="3">IVA (${taxaIva}%)</th><th class="num"><fmt:formatNumber value="${venda.iva}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
        <tr class="total"><th colspan="3">Total</th><th class="num"><fmt:formatNumber value="${venda.total}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
        </tfoot>
    </table>
    <p class="nota">Obrigado pela sua compra.</p>
</section>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
