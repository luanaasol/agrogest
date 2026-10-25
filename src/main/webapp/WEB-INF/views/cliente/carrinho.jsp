<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Carrinho" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Carrinho de compras</h1>

<c:choose>
    <c:when test="${carrinho.vazio}">
        <p class="vazio">O carrinho está vazio. <a href="${ctx}/catalogo">Ver catálogo</a></p>
    </c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table class="tabela">
                <thead>
                <tr><th>Produto</th><th class="num">Preço unit.</th><th>Quantidade</th><th class="num">Subtotal</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="i" items="${carrinho.itens}">
                    <tr>
                        <td><c:out value="${i.produtoNome}" /></td>
                        <td class="num"><fmt:formatNumber value="${i.precoUnit}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                        <td>
                            <form method="post" action="${ctx}/carrinho/atualizar" class="em-linha">
                                <input type="hidden" name="produtoId" value="${i.produtoId}">
                                <input type="number" name="quantidade" value="${i.quantidade}" min="1" required aria-label="Quantidade">
                                <span class="unidade"><c:out value="${i.unidade}" /></span>
                                <button type="submit" class="botao pequeno">Atualizar</button>
                            </form>
                        </td>
                        <td class="num"><fmt:formatNumber value="${i.subtotal}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                        <td>
                            <form method="post" action="${ctx}/carrinho/remover">
                                <input type="hidden" name="produtoId" value="${i.produtoId}">
                                <button type="submit" class="botao pequeno perigo">Remover</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
                <tfoot>
                <tr><th colspan="3">Total (sem IVA)</th><th class="num"><fmt:formatNumber value="${carrinho.total}" minFractionDigits="2" maxFractionDigits="2" /> MT</th><th></th></tr>
                </tfoot>
            </table>
        </div>
        <div class="acoes">
            <form method="post" action="${ctx}/carrinho/esvaziar">
                <button type="submit" class="botao perigo">Esvaziar carrinho</button>
            </form>
            <a class="botao" href="${ctx}/catalogo">Continuar a comprar</a>
            <a class="botao primario" href="${ctx}/compra">Finalizar compra</a>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
