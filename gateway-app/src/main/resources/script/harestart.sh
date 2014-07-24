#!/bin/sh

bin=$1
pid=$(cat haproxy.pid)

echo $pid

$1 -f haproxy.cfg -p haproxy.pid -sf $pid