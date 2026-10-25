<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page isErrorPage="true" %>
<c:set var="titulo" value="Erro" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}" />
<div class="cartao estreito">
    <c:choose>
        <c:when test="${codigo == 403}">
            <h1>403 - Acesso negado</h1>
            <p>Não tem permissão para aceder a esta página.</p>
        </c:when>
        <c:when test="${codigo == 404}">
            <h1>404 - Não encontrado</h1>
            <p>A página ou o recurso pedido não existe.</p>
        </c:when>
        <c:otherwise>
            <h1>Erro <c:out value="${codigo}" /></h1>
            <p>Ocorreu um erro inesperado.</p>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty requestScope['jakarta.servlet.error.message']}">
        <p class="nota"><c:out value="${requestScope['jakarta.servlet.error.message']}" /></p>
    </c:if>
    <p><a class="botao" href="${ctx}/">Voltar ao início</a></p>
</div>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
