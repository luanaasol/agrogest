<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Entrar" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<div class="auth">
    <aside class="auth-arte">
        <h2>Gestão agrícola, simples e organizada</h2>
        <p>Fornecedores, stock, campanhas, colheitas e vendas com factura, num só lugar.</p>
        <ul>
            <li>Catálogo de sementes, fertilizantes e ferramentas</li>
            <li>Compras com factura e IVA calculado</li>
            <li>Relatórios de clientes e vendas</li>
        </ul>
    </aside>
    <div class="cartao auth-form">
        <h1>Entrar no AgroGest</h1>
        <p class="sub">Use o seu email e palavra-passe.</p>
        <form method="post" action="${ctx}/login" class="formulario">
            <label>Email
                <input type="email" name="email" value="<c:out value='${email}'/>" placeholder="nome@exemplo.mz" required autofocus>
            </label>
            <label>Palavra-passe
                <input type="password" name="pass" required>
            </label>
            <button type="submit" class="botao primario largo">Entrar</button>
        </form>
        <p class="nota">Ainda não tem conta? <a href="${ctx}/registo">Registe-se aqui</a>.</p>
    </div>
</div>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
