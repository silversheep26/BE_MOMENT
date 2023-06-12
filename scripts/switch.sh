# switch.sh

#!/bin/bash

# service_url.inc 에서 현재 서비스를 하고 있는 WAS의 포트 번호 가져오기
CURRENT_PORT=$(cat /home/ubuntu/service-url.inc  | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Nginx currently proxies to ${CURRENT_PORT}."

if [ ${CURRENT_PORT} -eq 8080 ]; then
    TARGET_PORT=8081
elif [ ${CURRENT_PORT} -eq 8081 ]; then
    TARGET_PORT=8080
else
    echo "> No WAS is connected to nginx"
    exit 1
fi

# 위 커맨드들을 통해 현재 타겟포트 가져오기

# $ service_url.inc 파일을 현재 바뀐 서버의 포트로 변경
echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee /home/ubuntu/service-url.inc

echo "> Now Nginx proxies to ${TARGET_PORT}."

# nginx를 reload 해준다.
sudo service nginx reload
sudo iptables -t nat -D PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port ${CURRENT_PORT}
sudo iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port ${TARGET_PORT}
echo "> Nginx reloaded."