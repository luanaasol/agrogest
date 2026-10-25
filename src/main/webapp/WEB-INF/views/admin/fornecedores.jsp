<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Fornecedores" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Gestão de fornecedores</h1>

<div class="cartao">
    <h2>Novo fornecedor</h2>
    <form method="post" action="${ctx}/admin/fornecedores" class="formulario grelha-form">
        <input type="hidden" name="acao" value="criar">
        <label>Nome <input type="text" name="nome" required></label>
        <label>Tipo
            <select name="tipo" required>
                <c:forEach var="t" items="${tipos}"><option value="${t}">${t}</option></c:forEach>
            </select>
        </label>
        <label>Contacto <input type="text" name="contacto" required></label>
        <label>Província <input type="text" name="provincia" required></label>
        <div class="fim-form"><button type="submit" class="botao primario">Criar fornecedor</button></div>
    </form>
</div>

<h2>Fornecedores registados</h2>
<c:choose>
    <c:when test="${empty fornecedores}"><p class="vazio">Não há fornecedores registados.</p></c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table class="tabela">
                <thead><tr><th>ID</th><th>Nome</th><th>Tipo</th><th>Contacto</th><th>Província</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="f" items="${fornecedores}">
                    <tr>
                        <td>${f.id}</td>
                        <td><c:out value="${f.nome}" /></td>
                        <td>${f.tipo}</td>
                        <td><c:out value="${f.contacto}" /></td>
                        <td><c:out value="${f.provincia}" /></td>
                        <td>
                            <%-- Sem JavaScript: o GET mostra uma pagina de confirmacao e so o POST remove --%>
                            <a class="botao pequeno perigo" href="${ctx}/admin/fornecedores?confirmar=remover&amp;id=${f.id}">Remover</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
