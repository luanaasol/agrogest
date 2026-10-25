<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Persistência" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<h1>Persistência em ficheiro</h1>

<div class="cartao">
    <p>O estado do sistema é gravado automaticamente quando o Tomcat é encerrado e restaurado no arranque seguinte.
        A gravação é atómica: o estado é escrito num ficheiro temporário e só depois substitui o ficheiro anterior.</p>
    <dl class="detalhes">
        <dt>Ficheiro</dt><dd><code><c:out value="${ficheiro}" /></code></dd>
        <dt>Estado</dt><dd>${existe ? 'Existe um estado gravado' : 'Ainda não existe ficheiro gravado'}</dd>
    </dl>
    <div class="acoes">
        <form method="post" action="${ctx}/admin/persistencia">
            <input type="hidden" name="acao" value="gravar">
            <button type="submit" class="botao primario">Gravar agora</button>
        </form>
        <%-- Sem JavaScript: o GET mostra a pagina de confirmacao e so o POST elimina --%>
        <a class="botao perigo" href="${ctx}/admin/persistencia?confirmar=eliminar">Eliminar ficheiro</a>
    </div>
    <p class="nota">Eliminar o ficheiro não apaga os dados em memória; apenas remove a cópia gravada em disco.
        Ao encerrar o Tomcat, o estado volta a ser gravado.</p>
</div>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
