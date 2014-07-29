#!/bin/sh

bin=$1
pid=$(cat haproxy.pid)

echo $pid

kill -9 $pid