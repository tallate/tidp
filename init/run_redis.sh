#!/usr/bin/env bash
# 本地运行 redis，需要事先运行 docker 服务
docker run -it --name redis -p 6379:6379 -d redis