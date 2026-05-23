#!/bin/bash
set -e

DOMAIN="shop-replika.duckdns.org"
EMAIL="${ACME_EMAIL}"

if [ -z "$EMAIL" ]; then
  echo "Укажи email: ACME_EMAIL=you@example.com ./init-letsencrypt.sh"
  exit 1
fi

echo "==> Создаём временный самоподписанный сертификат чтобы Nginx смог стартовать..."
mkdir -p /var/lib/docker/volumes/shop_certbot-etc/_data/live/$DOMAIN
docker run --rm -v shop_certbot-etc:/etc/letsencrypt alpine \
  sh -c "mkdir -p /etc/letsencrypt/live/$DOMAIN && \
         apk add --no-cache openssl -q && \
         openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
           -keyout /etc/letsencrypt/live/$DOMAIN/privkey.pem \
           -out    /etc/letsencrypt/live/$DOMAIN/fullchain.pem \
           -subj '/CN=localhost' 2>/dev/null"

echo "==> Запускаем Nginx..."
docker compose up -d nginx

echo "==> Получаем настоящий сертификат Let's Encrypt..."
docker compose run --rm certbot certonly \
  --webroot -w /var/www/certbot \
  -d "$DOMAIN" \
  --email "$EMAIL" \
  --agree-tos --no-eff-email --force-renewal

echo "==> Перезагружаем Nginx с настоящим сертификатом..."
docker compose exec nginx nginx -s reload

echo ""
echo "Готово! Сайт доступен на https://$DOMAIN"
echo ""
echo "Добавь в crontab автообновление nginx после продления сертификата:"
echo "  0 3 * * * cd ~/shop && docker compose exec nginx nginx -s reload"
