#!/usr/bin/env bash
# 本地运行 mysql ，需要事先运行 docker 服务
docker run -it --name mysql -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 -d mysql:5.6