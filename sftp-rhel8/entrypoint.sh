#!/bin/bash

mkdir -p /sftp_jail/dev
[ -e /dev/null ] && cp -a /dev/null /sftp_jail/dev/null
[ -e /dev/zero ] && cp -a /dev/zero /sftp_jail/dev/zero
[ -e /dev/urandom ] && cp -a /dev/urandom /sftp_jail/dev/urandom

chmod 666 /sftp_jail/dev/*

ssh-keygen -A

exec /usr/sbin/sshd -D -e