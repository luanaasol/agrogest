<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Confirmar compra" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Confirmar compra</h1>

<div class="cartao">
    <h2>Dados do cliente</h2>
    <p><strong><c:out value="${u.nome}" /></strong><br>
        <c:out value="${u.email}" /> &middot; <c:out value="${u.telefone}" /><br>
        <c:out value="${u.endereco}" /></p>
</div>

<div class="tabela-wrap">
    <table class="tabela">
        <thead><tr><th>Produto</th><th class="num">Quantidade</th><th class="num">Preço unit.</th><th class="num">Subtotal</th></tr></thead>
        <tbody>
        <c:forEach var="i" items="${carrinho.itens}">
            <tr>
                <td><c:out value="${i.produtoNome}" /></td>
                <td class="num">${i.quantidade} <c:out value="${i.unidade}" /></td>
                <td class="num"><fmt:formatNumber value="${i.precoUnit}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                <td class="num"><fmt:formatNumber value="${i.subtotal}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr><th colspan="3">Subtotal</th><th class="num"><fmt:formatNumber value="${subtotal}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
        <tr><th colspan="3">IVA (16%)</th><th class="num"><fmt:formatNumber value="${iva}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
        <tr class="total"><th colspan="3">Total</th><th class="num"><fmt:formatNumber value="${total}" minFractionDigits="2" maxFractionDigits="2" /> MT</th></tr>
        </tfoot>
    </table>
</div>

<div class="acoes">
    <a class="botao" href="${ctx}/carrinho">Voltar ao carrinho</a>
    <form method="post" action="${ctx}/compra">
        <button type="submit" class="botao primario">Confirmar compra</button>
    </form>
</div>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
