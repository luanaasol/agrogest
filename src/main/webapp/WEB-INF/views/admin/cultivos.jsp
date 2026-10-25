<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Cultivos" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Gestão de cultivos</h1>

<div class="cartao">
    <h2>Registar cultivo</h2>
    <c:choose>
        <c:when test="${empty campanhas}">
            <p class="vazio">Crie primeiro uma campanha em <a href="${ctx}/admin/campanhas">Campanhas</a>.</p>
        </c:when>
        <c:otherwise>
            <form method="post" action="${ctx}/admin/cultivos" class="formulario grelha-form">
                <input type="hidden" name="acao" value="criar">
                <label>Campanha
                    <select name="campanhaId" required>
                        <c:forEach var="cp" items="${campanhas}">
                            <option value="${cp.id}"><c:out value="${cp.nome}" />${cp.aberta ? '' : ' (fechada)'}</option>
                        </c:forEach>
                    </select>
                </label>
                <label>Cultura <input type="text" name="cultura" placeholder="Feijão nhemba" required></label>
                <label>Variedade <input type="text" name="variedade" placeholder="Macia" required></label>
                <label>Hectares <input type="number" name="hectares" min="0.01" step="0.01" required></label>
                <label>Data de plantio <input type="date" name="plantio" required></label>
                <div class="fim-form"><button type="submit" class="botao primario">Registar cultivo</button></div>
            </form>
        </c:otherwise>
    </c:choose>
</div>

<h2>Cultivos registados</h2>
<c:choose>
    <c:when test="${empty cultivos}"><p class="vazio">Não há cultivos registados.</p></c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table class="tabela">
                <thead><tr><th>ID</th><th>Campanha</th><th>Cultura</th><th>Variedade</th><th class="num">Hectares</th><th>Plantio</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="cu" items="${cultivos}">
                    <tr>
                        <td>${cu.id}</td>
                        <td><c:out value="${nomesCampanhas[cu.campanhaId]}" /></td>
                        <td><c:out value="${cu.cultura}" /></td>
                        <td><c:out value="${cu.variedade}" /></td>
                        <td class="num"><fmt:formatNumber value="${cu.hectares}" maxFractionDigits="2" /> ha</td>
                        <td>${cu.plantioFmt}</td>
                        <td>
                            <%-- Sem JavaScript: o GET mostra uma pagina de confirmacao e so o POST remove --%>
                            <a class="botao pequeno perigo" href="${ctx}/admin/cultivos?confirmar=remover&amp;id=${cu.id}">Remover</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
