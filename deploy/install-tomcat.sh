#!/usr/bin/env bash
# Instala o Java 17 e o Apache Tomcat 10.1 em /opt/tomcat (porta 8081) e regista o servico systemd.
# Uso (na VM Ubuntu, dentro da pasta deploy/):   sudo bash install-tomcat.sh
# Pode ser executado de novo sem perder dados: /opt/tomcat/data e /opt/tomcat/webapps sao mantidos.
set -euo pipefail

TOMCAT_DIR=/opt/tomcat
PORTA=8081
VERSAO_FIXA=10.1.61   # usada se nao for possivel descobrir a versao 10.1 mais recente
PASTA_SCRIPT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ "$(id -u)" -ne 0 ]; then
    echo "Execute com sudo:  sudo bash install-tomcat.sh"
    exit 1
fi

echo "==> 1/7 A instalar o OpenJDK 17 (versao headless: sem graficos, ocupa menos disco) e ferramentas"
if ! apt-get update -y; then
    # Versoes do Ubuntu sem suporte (ex.: 25.04) saem dos servidores normais e passam para old-releases
    echo "    apt update falhou: a usar o arquivo old-releases.ubuntu.com"
    for f in /etc/apt/sources.list /etc/apt/sources.list.d/*.list /etc/apt/sources.list.d/*.sources; do
        if [ -f "$f" ]; then
            sed -i -E 's#https?://([a-z]{2}\.)?(archive|security)\.ubuntu\.com/ubuntu#http://old-releases.ubuntu.com/ubuntu#g' "$f"
        fi
    done
    apt-get update -y
fi
apt-get install -y openjdk-17-jdk-headless curl tar

# JAVA_HOME detetado (no Ubuntu costuma ser /usr/lib/jvm/java-17-openjdk-amd64)
JAVA_HOME_DETETADO="$(ls -d /usr/lib/jvm/java-17-openjdk-* 2>/dev/null | head -n 1 || true)"
if [ -z "$JAVA_HOME_DETETADO" ]; then
    JAVA_HOME_DETETADO="$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")"
fi
echo "    JAVA_HOME = $JAVA_HOME_DETETADO"
"$JAVA_HOME_DETETADO/bin/java" -version

echo "==> 2/7 A criar o utilizador de sistema 'tomcat'"
if ! id tomcat >/dev/null 2>&1; then
    useradd -r -m -U -d "$TOMCAT_DIR" -s /bin/false tomcat
fi

echo "==> 3/7 A descarregar o Tomcat 10.1"
VERSAO="$(curl -fsSL https://dlcdn.apache.org/tomcat/tomcat-10/ 2>/dev/null \
          | grep -o 'v10\.1\.[0-9]*/' | sort -V | tail -n 1 | tr -d 'v/' || true)"
if [ -n "$VERSAO" ]; then
    URL="https://dlcdn.apache.org/tomcat/tomcat-10/v$VERSAO/bin/apache-tomcat-$VERSAO.tar.gz"
else
    VERSAO="$VERSAO_FIXA"
    URL="https://archive.apache.org/dist/tomcat/tomcat-10/v$VERSAO/bin/apache-tomcat-$VERSAO.tar.gz"
fi
echo "    Versao $VERSAO"
TMP="$(mktemp -d)"
curl -fSL -o "$TMP/tomcat.tar.gz" "$URL"

echo "==> 4/7 A instalar em $TOMCAT_DIR"
if systemctl is-active --quiet tomcat 2>/dev/null; then
    systemctl stop tomcat   # para limpo: o AgroGest grava o estado antes de sair
fi
mkdir -p "$TOMCAT_DIR"
# --strip-components=1 extrai o conteudo de apache-tomcat-X diretamente para /opt/tomcat
tar -xzf "$TMP/tomcat.tar.gz" -C "$TOMCAT_DIR" --strip-components=1
rm -rf "$TMP"

echo "==> 5/7 A configurar a porta HTTP $PORTA"
sed -i "s/<Connector port=\"8080\"/<Connector port=\"$PORTA\"/" "$TOMCAT_DIR/conf/server.xml"
grep -q "Connector port=\"$PORTA\"" "$TOMCAT_DIR/conf/server.xml" && echo "    Porta $PORTA configurada"

echo "==> 6/7 A criar a pasta de dados e a acertar permissoes"
# O AgroGest grava o estado em CATALINA_BASE/data/agrogest.ser
mkdir -p "$TOMCAT_DIR/data"
chown -R tomcat:tomcat "$TOMCAT_DIR"
chmod +x "$TOMCAT_DIR"/bin/*.sh

echo "==> 7/7 A registar o servico systemd"
sed "s#^Environment=\"JAVA_HOME=.*#Environment=\"JAVA_HOME=$JAVA_HOME_DETETADO\"#" \
    "$PASTA_SCRIPT/tomcat.service" > /etc/systemd/system/tomcat.service
systemctl daemon-reload
systemctl enable tomcat
systemctl start tomcat

# Abrir a porta na firewall (se o ufw estiver ativo)
if command -v ufw >/dev/null 2>&1 && ufw status | grep -q "Status: active"; then
    ufw allow "$PORTA"/tcp
fi

IP="$(hostname -I | awk '{print $1}')"
echo
echo "Tomcat instalado e a correr."
echo "  Estado:   sudo systemctl status tomcat"
echo "  Publicar: sudo bash deploy-war.sh agrogest.war"
echo "  Endereco: http://$IP:$PORTA/agrogest  (depois de publicar o WAR)"
