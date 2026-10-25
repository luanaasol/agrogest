<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Relatórios" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Relatórios</h1>

<section class="cartao">
    <h2>Clientes ordenados por nome (crescente)</h2>
    <c:choose>
        <c:when test="${empty porNome}"><p class="vazio">Sem clientes registados.</p></c:when>
        <c:otherwise>
            <div class="tabela-wrap"><table class="tabela">
                <thead><tr><th>Nome</th><th>Email</th><th>Telefone</th><th>Endereço</th></tr></thead>
                <tbody>
                <c:forEach var="l" items="${porNome}">
                    <tr><td><c:out value="${l.cliente.nome}" /></td><td><c:out value="${l.cliente.email}" /></td>
                        <td><c:out value="${l.cliente.telefone}" /></td><td><c:out value="${l.cliente.endereco}" /></td></tr>
                </c:forEach>
                </tbody>
            </table></div>
        </c:otherwise>
    </c:choose>
</section>

<section class="cartao">
    <h2>Clientes que compraram (ordenados por volume)</h2>
    <c:choose>
        <c:when test="${empty porVolume}"><p class="vazio">Ainda não há compras.</p></c:when>
        <c:otherwise>
            <div class="tabela-wrap"><table class="tabela">
                <thead><tr><th>Nome</th><th class="num">N.º de compras</th><th class="num">Volume (total gasto)</th></tr></thead>
                <tbody>
                <c:forEach var="l" items="${porVolume}">
                    <tr><td><c:out value="${l.cliente.nome}" /></td><td class="num">${l.numCompras}</td>
                        <td class="num"><fmt:formatNumber value="${l.volume}" minFractionDigits="2" maxFractionDigits="2" /> MT</td></tr>
                </c:forEach>
                </tbody>
            </table></div>
        </c:otherwise>
    </c:choose>
</section>

<section class="cartao">
    <h2>Clientes mais assíduos (ranking por número de compras)</h2>
    <c:choose>
        <c:when test="${empty assiduos}"><p class="vazio">Ainda não há compras.</p></c:when>
        <c:otherwise>
            <div class="tabela-wrap"><table class="tabela">
                <thead><tr><th class="num">Posição</th><th>Nome</th><th class="num">N.º de compras</th><th class="num">Volume</th></tr></thead>
                <tbody>
                <c:forEach var="l" items="${assiduos}" varStatus="st">
                    <tr><td class="num">${st.count}.º</td><td><c:out value="${l.cliente.nome}" /></td><td class="num">${l.numCompras}</td>
                        <td class="num"><fmt:formatNumber value="${l.volume}" minFractionDigits="2" maxFractionDigits="2" /> MT</td></tr>
                </c:forEach>
                </tbody>
            </table></div>
        </c:otherwise>
    </c:choose>
</section>

<section class="cartao">
    <h2>Clientes sem compras (extra)</h2>
    <c:choose>
        <c:when test="${empty semCompras}"><p class="vazio">Todos os clientes já efetuaram compras.</p></c:when>
        <c:otherwise>
            <div class="tabela-wrap"><table class="tabela">
                <thead><tr><th>Nome</th><th>Email</th><th>Telefone</th></tr></thead>
                <tbody>
                <c:forEach var="l" items="${semCompras}">
                    <tr><td><c:out value="${l.cliente.nome}" /></td><td><c:out value="${l.cliente.email}" /></td>
                        <td><c:out value="${l.cliente.telefone}" /></td></tr>
                </c:forEach>
                </tbody>
            </table></div>
        </c:otherwise>
    </c:choose>
</section>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
