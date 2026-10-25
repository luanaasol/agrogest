#!/usr/bin/env bash
# Publica o agrogest.war no Tomcat (/opt/tomcat) e reinicia o servico.
# Uso:  sudo bash deploy-war.sh [caminho/para/agrogest.war]
# Os dados nao se perdem: ao parar, o AgroGest grava /opt/tomcat/data/agrogest.ser
# e ao arrancar volta a le-lo.
set -euo pipefail

WAR="${1:-agrogest.war}"
WEBAPPS=/opt/tomcat/webapps

if [ "$(id -u)" -ne 0 ]; then
    echo "Execute com sudo:  sudo bash deploy-war.sh $WAR"
    exit 1
fi
if [ ! -f "$WAR" ]; then
    echo "Ficheiro nao encontrado: $WAR"
    exit 1
fi

echo "==> A parar o Tomcat (o estado e gravado em ficheiro)"
systemctl stop tomcat

echo "==> A publicar $WAR"
rm -rf "$WEBAPPS/agrogest"            # pasta extraida da versao anterior
cp "$WAR" "$WEBAPPS/agrogest.war"
chown tomcat:tomcat "$WEBAPPS/agrogest.war"

echo "==> A arrancar o Tomcat"
systemctl start tomcat
sleep 5
systemctl --no-pager --lines=0 status tomcat || true

IP="$(hostname -I | awk '{print $1}')"
echo
echo "Publicado. Abrir: http://$IP:8081/agrogest"
echo "Registos:  sudo journalctl -u tomcat -f   ou   sudo tail -f /opt/tomcat/logs/catalina.out"
