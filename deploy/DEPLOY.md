# Deploy do AgroGest numa VM Ubuntu (VirtualBox) com systemd

Guia passo a passo para publicar o `agrogest.war` no Apache Tomcat 10.1 (porta **8081**) numa
máquina virtual Ubuntu no VirtualBox, gerido como serviço do sistema (systemd).

Ficheiros desta pasta:

| Ficheiro | Para que serve |
|---|---|
| `install-tomcat.sh` | Instala o Java 17 e o Tomcat 10.1 em `/opt/tomcat`, porta 8081, cria o utilizador `tomcat`, a pasta de dados e o serviço |
| `tomcat.service` | Definição do serviço systemd (o script acima instala-o) |
| `deploy-war.sh` | Copia o WAR para o Tomcat e reinicia o serviço |

---

## 1. Preparar a VM

### 1.1 Verificar a versão do Ubuntu

Na VM, abrir um terminal (no Ubuntu Desktop: Ctrl+Alt+T; no Ubuntu Server o ecrã já é o terminal)
e escrever:

```bash
lsb_release -a
```

Funciona em Ubuntu **22.04** ou **24.04** (ambos têm o pacote `openjdk-17-jdk`). A VM usada pelo
grupo é o **Ubuntu Server 24.04.5 LTS** (sem ambiente gráfico, gasta menos memória).

### 1.2 Rede da VM

Com a VM **desligada**, no VirtualBox: selecionar a VM > **Definições** > **Rede** > **Placa 1**.

**Opção A (recomendada): Placa em ponte (Bridged Adapter)**

- "Ligada a": **Placa em ponte** (Bridged Adapter); "Nome": a placa Wi-Fi ou Ethernet do Windows.
- A VM recebe um IP da mesma rede do Windows (ex.: `192.168.1.50`), por isso **colegas e telemóveis
  na mesma rede Wi-Fi** conseguem abrir o AgroGest. É a opção ideal para a demonstração.
- Nota: algumas redes (ex.: redes de universidade com isolamento de clientes) não deixam os
  dispositivos falar entre si. Nesse caso usar um hotspot do telemóvel ou a opção B.

**Opção B: NAT com reencaminhamento de portas**

- "Ligada a": **NAT** > **Avançado** > **Reencaminhamento de portas** > botão **+**:

  | Nome | Protocolo | Porta anfitrião | Porta convidado |
  |---|---|---|---|
  | agrogest | TCP | 8081 | 8081 |
  | ssh | TCP | 2222 | 22 |

- No Windows o sistema abre em `http://localhost:8081/agrogest`. Para outros dispositivos usarem,
  é o IP do **Windows** que se indica (e a Firewall do Windows tem de permitir a porta 8081).
- Atenção: se o Tomcat local do Windows estiver ligado, também usa a porta 8081. Desligue-o
  (`C:\tools\tomcat10\bin\shutdown.bat`) antes de usar esta opção.

Ligar a VM.

### 1.3 Atualizar o sistema

```bash
sudo apt update && sudo apt upgrade -y
```

---

## 2. Passar os ficheiros do Windows para a VM

É preciso levar para a VM a pasta `deploy/` e o ficheiro `target/agrogest.war`.
No Windows, gerar primeiro o WAR na pasta do projeto: `mvn clean package`.

### Opção 1: `scp` (recomendada, o Windows 10/11 já traz o comando)

Na VM, instalar o servidor SSH e ver o IP:

```bash
sudo apt install -y openssh-server
ip a          # procurar "inet 192.168.x.x" na placa enp0s3 (ou semelhante)
```

No **PowerShell do Windows**, dentro da pasta do projeto (trocar `utilizador` e o IP):

```powershell
# Rede em ponte (Bridged):
scp -r deploy target\agrogest.war utilizador@192.168.1.50:~/

# Rede NAT (com a regra ssh 2222 -> 22):
scp -P 2222 -r deploy target\agrogest.war utilizador@localhost:~/
```

Responder `yes` à primeira pergunta e escrever a palavra-passe do utilizador da VM.

### Opção 2: Pasta partilhada do VirtualBox

1. Na VM, menu **Dispositivos** > **Inserir imagem de CD das Adições de Convidado** e instalar
   (`sudo apt install -y build-essential dkms linux-headers-$(uname -r)` antes, se pedir).
2. Com a VM desligada: **Definições** > **Pastas partilhadas** > **+** > escolher a pasta do projeto
   no Windows, marcar **Montar automaticamente**.
3. Na VM: `sudo usermod -aG vboxsf $USER`, reiniciar a VM. A pasta aparece em `/media/sf_<nome>`.
4. Copiar: `cp -r /media/sf_agrogest/deploy /media/sf_agrogest/target/agrogest.war ~/`

### Opção 3: Descarregar do Windows por HTTP

1. No **PowerShell do Windows**, partilhar temporariamente a pasta com os ficheiros (Python instalado):
   `python -m http.server 8000 --directory C:\caminho\para\pasta`
   (a Firewall do Windows pode pedir autorização na primeira vez).
2. Na VM (trocar pelo IP do Windows, visto com `ipconfig`):
   `wget http://192.168.1.53:8000/agrogest.war http://192.168.1.53:8000/install-tomcat.sh ...`
3. No Windows, Ctrl+C para fechar a partilha.

> Se o bash der erros como `$'\r': command not found`, os scripts ficaram com fim de linha do
> Windows. Corrigir com: `sudo apt install -y dos2unix && dos2unix ~/deploy/*.sh ~/deploy/tomcat.service`

---

## 3. Instalar o Tomcat 10.1 como serviço

```bash
cd ~/deploy
sudo bash install-tomcat.sh
```

O script:

1. instala o `openjdk-17-jdk-headless` (o JDK 17 sem componentes gráficos) e deteta o `JAVA_HOME` (normalmente `/usr/lib/jvm/java-17-openjdk-amd64`);
2. cria o utilizador de sistema `tomcat` (sem login);
3. descarrega o Tomcat 10.1 e instala-o em `/opt/tomcat`;
4. muda a porta HTTP para **8081** em `/opt/tomcat/conf/server.xml`;
5. cria `/opt/tomcat/data` (onde fica o `agrogest.ser`) com dono `tomcat`;
6. instala o `tomcat.service` em `/etc/systemd/system/`, ativa-o no arranque e inicia-o;
7. abre a porta 8081 na firewall se o `ufw` estiver ativo.

---

## 4. Publicar o AgroGest

```bash
cd ~
sudo bash deploy/deploy-war.sh agrogest.war
```

Para publicar uma versão nova mais tarde, basta copiar o WAR novo para a VM e repetir este comando.
Os dados mantêm-se (o estado é gravado ao parar e lido ao arrancar).

---

## 5. Gerir o serviço com systemd

| Comando | O que faz |
|---|---|
| `sudo systemctl daemon-reload` | Relê os ficheiros de serviço (após alterar o `tomcat.service`) |
| `sudo systemctl enable tomcat` | Arranca o Tomcat automaticamente quando a VM liga |
| `sudo systemctl start tomcat` | Inicia |
| `sudo systemctl stop tomcat` | Para de forma limpa (o AgroGest **grava o estado** em `/opt/tomcat/data/agrogest.ser`) |
| `sudo systemctl restart tomcat` | Reinicia (para + inicia) |
| `systemctl status tomcat` | Mostra se está `active (running)` e as últimas linhas do registo |
| `journalctl -u tomcat` | Registos do serviço (arranque, paragem, erros) |
| `journalctl -u tomcat -f` | Acompanhar os registos em tempo real (Ctrl+C para sair) |
| `sudo tail -f /opt/tomcat/logs/catalina.out` | Registo do próprio Tomcat (inclui as mensagens do AgroGest) |

Ao parar, o `catalina.out` mostra `AgroGest: estado gravado em /opt/tomcat/data/agrogest.ser`;
ao arrancar, `AgroGest: estado restaurado de ...`.

---

## 6. Firewall e acesso pela rede

```bash
sudo ufw allow 8081/tcp     # abrir a porta (se o ufw estiver ativo)
sudo ufw status             # confirmar
ip a                        # descobrir o IP da VM (linha "inet" da placa enp0s3)
```

Abrir no navegador (no Windows, noutro computador ou no telemóvel, na mesma rede):

```
http://IP-DA-VM:8081/agrogest
```

Exemplo: `http://192.168.1.50:8081/agrogest`. Com NAT: `http://localhost:8081/agrogest` no Windows.

Credenciais: `admin@agro.mz` / `admin234` (clientes de exemplo no README).

---

## 7. Demonstração da persistência

1. Entrar como admin e criar algo novo (ex.: o fornecedor "Sementes do Sul, Lda").
2. Na VM: `sudo systemctl restart tomcat`
3. Ver o registo: `sudo tail -n 20 /opt/tomcat/logs/catalina.out` (linhas "estado gravado" e "estado restaurado").
4. Recarregar a página (entrar de novo, a sessão termina com o reinício): o fornecedor continua lá.
5. Mostrar o ficheiro: `ls -l /opt/tomcat/data/`

Extra: no menu **Persistência** clicar em "Gravar agora" e repetir o `ls -l` (a data do ficheiro muda).

---

## 8. Demonstração da concorrência (RN02, venda atómica)

Objetivo: dois clientes, em computadores (ou telemóveis) diferentes, tentam comprar ao mesmo tempo
a última unidade de um produto. Só um consegue.

1. Como admin, criar um produto com **Quantidade 1** (ex.: "Pulverizador manual", FERRAMENTA).
2. No **computador A** entrar como `ana.macuacua@exemplo.mz` / `1234`; no **computador B** como
   `carlos.nhantumbo@exemplo.mz` / `1234`.
3. Os dois adicionam **1 unidade** do produto ao carrinho (ambos conseguem: o stock ainda é 1)
   e abrem **Finalizar compra**.
4. Contar "3, 2, 1" e os dois clicam em **Confirmar compra** ao mesmo tempo.
5. Resultado: um recebe a factura; o outro volta ao carrinho com a mensagem
   "Indisponível: stock insuficiente para ... (pedido: 1, disponível: 0)".
6. Na listagem de produtos o stock fica em **0** (nunca negativo).

Explicação para a defesa: `VendaService.finalizarCompra` executa dentro de
`synchronized (ds)`. Só um pedido de cada vez entra na secção crítica, verifica o stock de **todos**
os itens e só depois desconta (tudo ou nada, RN01 e RN02). O segundo pedido entra a seguir, já vê o
stock a 0 e é recusado sem alterar nada.

---

## 9. Problemas comuns

| Sintoma | Causa provável e solução |
|---|---|
| `systemctl status tomcat` mostra `failed` | Ver `journalctl -u tomcat -n 50`. Confirmar o `JAVA_HOME` com `ls /usr/lib/jvm/` e em `/etc/systemd/system/tomcat.service`; depois `sudo systemctl daemon-reload && sudo systemctl restart tomcat` |
| A página não abre noutro dispositivo | Rede da VM não está em ponte, `ufw` sem a regra 8081, ou rede Wi-Fi com isolamento de clientes |
| `Address already in use` no `catalina.out` | Outro programa usa a porta 8081: `sudo ss -tlnp \| grep 8081` |
| 404 em `/agrogest` | O WAR ainda está a ser extraído (esperar uns segundos) ou não foi copiado: `ls /opt/tomcat/webapps/` |
| Os dados não ficaram gravados | O Tomcat foi morto em vez de parado (ex.: desligar a VM à força). Usar sempre `systemctl stop`/`restart` |
| `Permission denied` ao gravar | `sudo chown -R tomcat:tomcat /opt/tomcat/data` |
