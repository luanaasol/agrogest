<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Campanhas" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Gestão de campanhas</h1>

<div class="cartao">
    <h2>Nova campanha</h2>
    <form method="post" action="${ctx}/admin/campanhas" class="formulario grelha-form">
        <input type="hidden" name="acao" value="criar">
        <label>Nome <input type="text" name="nome" placeholder="Campanha 2026/2027" required></label>
        <label>Data de início <input type="date" name="inicio" required></label>
        <label>Data de fim <input type="date" name="fim" required></label>
        <div class="fim-form"><button type="submit" class="botao primario">Criar campanha</button></div>
    </form>
</div>

<h2>Campanhas registadas</h2>
<c:choose>
    <c:when test="${empty campanhas}"><p class="vazio">Não há campanhas registadas.</p></c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table class="tabela">
                <thead><tr><th>ID</th><th>Nome</th><th>Início</th><th>Fim</th><th>Estado</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="cp" items="${campanhas}">
                    <tr>
                        <td>${cp.id}</td>
                        <td><c:out value="${cp.nome}" /></td>
                        <td>${cp.inicioFmt}</td>
                        <td>${cp.fimFmt}</td>
                        <td><span class="estado ${cp.aberta ? 'aberta' : 'fechada'}">${cp.aberta ? 'Aberta' : 'Fechada'}</span></td>
                        <td class="botoes">
                            <form method="post" action="${ctx}/admin/campanhas">
                                <input type="hidden" name="id" value="${cp.id}">
                                <input type="hidden" name="acao" value="${cp.aberta ? 'fechar' : 'reabrir'}">
                                <button type="submit" class="botao pequeno">${cp.aberta ? 'Fechar' : 'Reabrir'}</button>
                            </form>
                            <%-- Sem JavaScript: o GET mostra uma pagina de confirmacao e so o POST remove --%>
                            <a class="botao pequeno perigo" href="${ctx}/admin/campanhas?confirmar=remover&amp;id=${cp.id}">Remover</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
