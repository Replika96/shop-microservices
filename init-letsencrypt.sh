#!/bin/bash
# Запустить ОДИН РАЗ на VPS для получения SSL сертификатов.
# Использование: bash init-letsencrypt.sh your@email.com

set -e

EMAIL="${1}"
if [ -z "$EMAIL" ]; then
  echo "Укажи email: bash init-letsencrypt.sh your@email.com"
  exit 1
fi

DOMAINS=("api.dragonflex.ru" "admin.dragonflex.ru")

echo "▶ Создаём временные самоподписанные сертификаты..."
for domain in "${DOMAINS[@]}"; do
  docker run --rm -v shop_certbot-etc:/etc/letsencrypt alpine \
    sh -c "apk add --no-cache openssl -q && \
           mkdir -p /etc/letsencrypt/live/$domain && \
           openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
           -keyout /etc/letsencrypt/live/$domain/privkey.pem \
           -out /etc/letsencrypt/live/$domain/fullchain.pem \
           -subj /CN=localhost 2>/dev/null"
done

echo "▶ Запускаем nginx с временными сертификатами..."
docker compose up -d nginx
sleep 5

echo "▶ Получаем сертификат для api.dragonflex.ru..."
docker compose run --rm certbot certonly \
  --webroot -w /var/www/certbot \
  --email "$EMAIL" --agree-tos --no-eff-email --force-renewal \
  -d api.dragonflex.ru

echo "▶ Получаем сертификат для admin.dragonflex.ru..."
docker compose run --rm certbot certonly \
  --webroot -w /var/www/certbot \
  --email "$EMAIL" --agree-tos --no-eff-email --force-renewal \
  -d admin.dragonflex.ru

echo "▶ Перезагружаем nginx с настоящими сертификатами..."
docker compose exec nginx nginx -s reload

echo ""
echo "✅ Готово!"
echo "   https://api.dragonflex.ru   — бэкенд для мобилки"
echo "   https://admin.dragonflex.ru — админ панель"
