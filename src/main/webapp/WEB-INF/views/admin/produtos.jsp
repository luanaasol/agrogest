<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Produtos" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Gestão de produtos</h1>

<div class="cartao">
    <h2>Novo produto</h2>
    <c:choose>
        <c:when test="${empty fornecedores}">
            <p class="vazio">Crie primeiro um fornecedor em <a href="${ctx}/admin/fornecedores">Fornecedores</a>.</p>
        </c:when>
        <c:otherwise>
            <%-- multipart/form-data: necessario para enviar o ficheiro de imagem --%>
            <form method="post" action="${ctx}/admin/produtos" class="formulario grelha-form" enctype="multipart/form-data">
                <input type="hidden" name="acao" value="criar">
                <label>Nome <input type="text" name="nome" required></label>
                <label>Categoria
                    <select name="categoria" required>
                        <c:forEach var="cat" items="${categorias}"><option value="${cat}">${cat}</option></c:forEach>
                    </select>
                </label>
                <label>Fornecedor
                    <select name="fornecedorId" required>
                        <c:forEach var="f" items="${fornecedores}"><option value="${f.id}"><c:out value="${f.nome}" /></option></c:forEach>
                    </select>
                </label>
                <label>Preço (MT) <input type="number" name="preco" min="0.01" step="0.01" required></label>
                <label>Quantidade <input type="number" name="quantidade" min="0" step="1" required></label>
                <label>Unidade <input type="text" name="unidade" placeholder="kg, saco, unidade" required></label>
                <label>Validade <input type="date" name="validade" required></label>
                <label>Imagem (ficheiro) <input type="file" name="imagem" accept="image/jpeg,image/png,image/webp,image/gif"></label>
                <label>Ou foto por URL <input type="text" name="foto" placeholder="https://..."></label>
                <label class="largo">Descrição <textarea name="descricao" rows="2"></textarea></label>
                <p class="nota fim-form">Imagem opcional: JPG, PNG, WebP ou GIF até 2 MB. Se carregar um ficheiro, ele tem prioridade sobre o URL.</p>
                <div class="fim-form"><button type="submit" class="botao primario">Criar produto</button></div>
            </form>
        </c:otherwise>
    </c:choose>
</div>

<h2>Produtos registados</h2>
<c:choose>
    <c:when test="${empty produtos}"><p class="vazio">Não há produtos registados.</p></c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table class="tabela">
                <thead><tr><th>ID</th><th>Foto</th><th>Nome</th><th>Categoria</th><th>Fornecedor</th><th class="num">Preço</th><th class="num">Stock</th><th>Validade</th><th>Repor stock</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${produtos}">
                    <tr>
                        <td>${p.id}</td>
                        <td>
                            <c:choose>
                                <c:when test="${p.fotoCarregada}"><img class="miniatura" src="${ctx}/foto?id=${p.id}" alt=""></c:when>
                                <c:when test="${empty p.foto}"><span class="miniatura sem-foto"></span></c:when>
                                <c:when test="${fn:startsWith(p.foto, 'http')}"><img class="miniatura" src="<c:out value='${p.foto}'/>" alt=""></c:when>
                                <c:otherwise><img class="miniatura" src="${ctx}/<c:out value='${p.foto}'/>" alt=""></c:otherwise>
                            </c:choose>
                        </td>
                        <td><c:out value="${p.nome}" /></td>
                        <td>${p.categoria}</td>
                        <td><c:out value="${p.fornecedorNome}" /></td>
                        <td class="num"><fmt:formatNumber value="${p.preco}" minFractionDigits="2" maxFractionDigits="2" /> MT</td>
                        <td class="num">${p.quantidade} <c:out value="${p.unidade}" /></td>
                        <td>${p.validadeFmt}</td>
                        <td>
                            <form method="post" action="${ctx}/admin/produtos" class="em-linha">
                                <input type="hidden" name="acao" value="repor">
                                <input type="hidden" name="id" value="${p.id}">
                                <input type="number" name="quantidade" min="1" step="1" required aria-label="Quantidade a repor">
                                <button type="submit" class="botao pequeno">Repor</button>
                            </form>
                        </td>
                        <td>
                            <%-- Sem JavaScript: o GET mostra uma pagina de confirmacao e so o POST remove --%>
                            <a class="botao pequeno perigo" href="${ctx}/admin/produtos?confirmar=remover&amp;id=${p.id}">Remover</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
