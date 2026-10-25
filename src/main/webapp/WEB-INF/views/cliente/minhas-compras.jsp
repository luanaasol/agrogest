<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Minhas compras" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Minhas compras</h1>

<c:choose>
    <c:when test="${empty vendas}">
        <p class="vazio">Ainda não efetuou nenhuma compra. <a href="${ctx}/catalogo">Ver catálogo</a></p>
    </c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table class="tabela">
                <thead><tr><th>Factura</th><th>Data</th><th class="num">Itens</th><th class="num">Total</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="v" items="${vendas}">
                    <tr>
                        <td>${v.numeroFactura}</td>
                        <td>${v.dataFmt}</td>
                        <td class="num">${fn:length(v.itens)}</td>
                        <td class="num"><fmt:formatNumber value="${v.total}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                        <td><a class="botao pequeno" href="${ctx}/factura?id=${v.id}">Ver factura</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
