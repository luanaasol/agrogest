<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Catálogo" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Catálogo de produtos</h1>

<form method="get" action="${ctx}/catalogo" class="filtros">
    <label>Categoria
        <select name="categoria">
            <option value="">Todas</option>
            <c:forEach var="cat" items="${categorias}">
                <option value="${cat}" ${cat == categoriaAtual ? 'selected' : ''}>${cat}</option>
            </c:forEach>
        </select>
    </label>
    <button type="submit" class="botao">Filtrar</button>
</form>

<c:if test="${empty produtos}">
    <p class="vazio">Não há produtos nesta categoria.</p>
</c:if>

<div class="grelha">
    <c:forEach var="p" items="${produtos}">
        <article class="produto">
            <c:choose>
                <c:when test="${p.fotoCarregada}"><img class="foto" src="${ctx}/foto?id=${p.id}" alt="<c:out value='${p.nome}'/>"></c:when>
                <c:when test="${empty p.foto}"><div class="foto sem-foto">Sem foto</div></c:when>
                <c:when test="${fn:startsWith(p.foto, 'http')}"><img class="foto" src="<c:out value='${p.foto}'/>" alt="<c:out value='${p.nome}'/>"></c:when>
                <c:otherwise><img class="foto" src="${ctx}/<c:out value='${p.foto}'/>" alt="<c:out value='${p.nome}'/>"></c:otherwise>
            </c:choose>
            <div class="produto-corpo">
                <span class="etiqueta">${p.categoria}</span>
                <h2><c:out value="${p.nome}" /></h2>
                <p class="descricao"><c:out value="${p.descricao}" /></p>
                <dl class="detalhes">
                    <dt>Preço</dt><dd><fmt:formatNumber value="${p.preco}" minFractionDigits="2" maxFractionDigits="2" /> MT / <c:out value="${p.unidade}" /></dd>
                    <dt>Disponível</dt><dd>${p.quantidade} <c:out value="${p.unidade}" /></dd>
                    <dt>Validade</dt><dd>${p.validadeFmt}</dd>
                    <dt>Fornecedor</dt><dd><c:out value="${p.fornecedorNome}" /></dd>
                </dl>
                <c:choose>
                    <c:when test="${p.quantidade > 0}">
                        <form method="post" action="${ctx}/carrinho/adicionar" class="em-linha">
                            <input type="hidden" name="produtoId" value="${p.id}">
                            <input type="number" name="quantidade" value="1" min="1" max="${p.quantidade}" required aria-label="Quantidade">
                            <button type="submit" class="botao primario">Adicionar</button>
                        </form>
                    </c:when>
                    <c:otherwise><p class="esgotado">Indisponível (sem stock)</p></c:otherwise>
                </c:choose>
            </div>
        </article>
    </c:forEach>
</div>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
