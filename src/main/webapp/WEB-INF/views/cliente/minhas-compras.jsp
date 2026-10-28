<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Minhas compras" />
<c:set var="imagemLado" value="lado-compras.jpg" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Minhas compras</h1>

<c:choose>
    <c:when test="${empty vendas}">
        <p class="vazio">Ainda não efetuou nenhuma compra. <a href="${ctx}/catalogo">Ver catálogo</a></p>
    </c:when>
    <c:otherwise>
        <%-- Totais do cliente (calculados a partir das suas vendas) --%>
        <c:set var="totalGasto" value="${0}" />
        <c:set var="totalArtigos" value="${0}" />
        <c:forEach var="v" items="${vendas}">
            <c:set var="totalGasto" value="${totalGasto + v.total}" />
            <c:set var="totalArtigos" value="${totalArtigos + fn:length(v.itens)}" />
        </c:forEach>
        <div class="indicadores">
            <div class="indicador i1" style="--n:7"><span>Compras</span><strong>${fn:length(vendas)}</strong></div>
            <div class="indicador i3" style="--n:7"><span>Artigos</span><strong>${totalArtigos}</strong></div>
            <div class="indicador i4 largo" style="--n:11"><span>Total gasto</span><strong><fmt:formatNumber value="${totalGasto}" minFractionDigits="2" maxFractionDigits="2" /> <small>MT</small></strong></div>
        </div>
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
