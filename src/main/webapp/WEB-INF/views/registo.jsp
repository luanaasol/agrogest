<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Registo" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<div class="auth">
    <aside class="auth-arte">
        <h2>Crie a sua conta de cliente</h2>
        <p>Depois do registo pode consultar o catálogo, encher o carrinho e receber a factura de cada compra.</p>
        <ul>
            <li>O email é único e serve para entrar</li>
            <li>A palavra-passe é guardada de forma segura (hash)</li>
            <li>O endereço e o telefone aparecem na factura</li>
        </ul>
    </aside>
    <div class="cartao auth-form">
        <h1>Criar conta</h1>
        <form method="post" action="${ctx}/registo" class="formulario">
            <label>Nome
                <input type="text" name="nome" value="<c:out value='${param.nome}'/>" required>
            </label>
            <label>Email
                <input type="email" name="email" value="<c:out value='${param.email}'/>" placeholder="nome@exemplo.mz" required>
            </label>
            <label>Palavra-passe
                <input type="password" name="pass" required>
            </label>
            <label>Telefone
                <input type="tel" name="telefone" value="<c:out value='${param.telefone}'/>" placeholder="+258 84 000 0000" required>
            </label>
            <label>Endereço
                <input type="text" name="endereco" value="<c:out value='${param.endereco}'/>" required>
            </label>
            <button type="submit" class="botao primario largo">Registar</button>
        </form>
        <p class="nota">Já tem conta? <a href="${ctx}/login">Entrar</a>.</p>
    </div>
</div>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
