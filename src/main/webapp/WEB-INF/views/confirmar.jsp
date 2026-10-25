<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Pagina de confirmacao generica (substitui a caixa de confirmacao do JavaScript).
     O Servlet mostra esta pagina no GET; so o POST do botao Confirmar executa a operacao. --%>
<c:set var="titulo" value="Confirmar" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<div class="cartao estreito">
    <h1>Tem a certeza?</h1>
    <p><c:out value="${pergunta}" /></p>
    <c:if test="${not empty aviso}"><p class="nota"><c:out value="${aviso}" /></p></c:if>
    <div class="acoes">
        <form method="post" action="${ctx}${destino}">
            <input type="hidden" name="acao" value="${acaoConfirmar}">
            <c:if test="${idConfirmar > 0}"><input type="hidden" name="id" value="${idConfirmar}"></c:if>
            <button type="submit" class="botao perigo">Confirmar</button>
        </form>
        <a class="botao" href="${ctx}${destino}">Cancelar</a>
    </div>
</div>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
