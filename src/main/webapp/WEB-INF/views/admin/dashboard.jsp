<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Painel" />
<c:set var="imagemLado" value="lado-painel.jpg" />
<%@ include file="/WEB-INF/views/fragmentos/topo.jspf" %>
<div class="cabecalho-pagina">
    <h1>Painel do administrador</h1>
    <p class="sub">Resumo do sistema. Clique num total para abrir a página correspondente.</p>
</div>

<div class="indicadores cinco">
    <a class="indicador i1" href="${ctx}/admin/fornecedores" style="--n:12"><span>Fornecedores</span><strong>${numFornecedores}</strong></a>
    <a class="indicador i2" href="${ctx}/admin/produtos" style="--n:8"><span>Produtos</span><strong>${numProdutos}</strong></a>
    <a class="indicador i3" href="${ctx}/admin/listagens?tipo=clientes" style="--n:8"><span>Clientes</span><strong>${numClientes}</strong></a>
    <a class="indicador i4" href="${ctx}/admin/listagens?tipo=compras" style="--n:6"><span>Vendas</span><strong>${numVendas}</strong></a>
    <a class="indicador i5" href="${ctx}/admin/campanhas" style="--n:9"><span>Campanhas</span><strong>${numCampanhas}</strong></a>
</div>

<div class="cartao">
    <h2>Atalhos</h2>
    <div class="atalhos">
        <a class="botao" href="${ctx}/admin/fornecedores">Fornecedores</a>
        <a class="botao" href="${ctx}/admin/produtos">Produtos</a>
        <a class="botao" href="${ctx}/admin/campanhas">Campanhas</a>
        <a class="botao" href="${ctx}/admin/cultivos">Cultivos</a>
        <a class="botao" href="${ctx}/admin/colheitas">Colheitas</a>
        <a class="botao" href="${ctx}/admin/listagens">Listagens</a>
        <a class="botao" href="${ctx}/admin/relatorios">Relatórios</a>
        <a class="botao" href="${ctx}/admin/persistencia">Persistência</a>
    </div>
</div>
<%@ include file="/WEB-INF/views/fragmentos/rodape.jspf" %>
