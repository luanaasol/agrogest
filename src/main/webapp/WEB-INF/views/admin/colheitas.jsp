<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Colheitas" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Gestão de colheitas</h1>

<div class="cartao">
    <h2>Registar colheita</h2>
    <c:choose>
        <c:when test="${empty cultivos}">
            <p class="vazio">Registe primeiro um cultivo em <a href="${ctx}/admin/cultivos">Cultivos</a>.</p>
        </c:when>
        <c:otherwise>
            <form method="post" action="${ctx}/admin/colheitas" class="formulario grelha-form">
                <input type="hidden" name="acao" value="criar">
                <label>Cultivo
                    <select name="cultivoId" required>
                        <c:forEach var="cu" items="${cultivos}">
                            <option value="${cu.id}"><c:out value="${nomesCultivos[cu.id]}" /></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Data <input type="date" name="data" required></label>
                <label>Quantidade (kg) <input type="number" name="kg" min="0.01" step="0.01" required></label>
                <label>Preço por kg (MT) <input type="number" name="precoKg" min="0.01" step="0.01" required></label>
                <div class="fim-form"><button type="submit" class="botao primario">Registar colheita</button></div>
            </form>
            <p class="nota">O valor total é calculado automaticamente (quantidade x preço por kg).</p>
        </c:otherwise>
    </c:choose>
</div>

<h2>Colheitas registadas</h2>
<c:choose>
    <c:when test="${empty colheitas}"><p class="vazio">Não há colheitas registadas.</p></c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table class="tabela">
                <thead><tr><th>ID</th><th>Cultivo</th><th>Data</th><th class="num">Quantidade</th><th class="num">Preço/kg</th><th class="num">Valor total</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="co" items="${colheitas}">
                    <tr>
                        <td>${co.id}</td>
                        <td><c:out value="${nomesCultivos[co.cultivoId]}" /></td>
                        <td>${co.dataFmt}</td>
                        <td class="num"><fmt:formatNumber value="${co.kg}" maxFractionDigits="2" /> kg</td>
                        <td class="num"><fmt:formatNumber value="${co.precoKg}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                        <td class="num"><strong><fmt:formatNumber value="${co.valorTotal}" minFractionDigits="2" maxFractionDigits="2" /> MT</strong></td>
                        <td>
                            <%-- Sem JavaScript: o GET mostra uma pagina de confirmacao e so o POST remove --%>
                            <a class="botao pequeno perigo" href="${ctx}/admin/colheitas?confirmar=remover&amp;id=${co.id}">Remover</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
