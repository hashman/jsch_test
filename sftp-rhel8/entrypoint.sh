#!/bin/bash

# Create mount point if not exists
mkdir -p /sftp_root/ftphome2

# Bind mount /mnt/data/ftphome2 to /sftp_root/ftphome2
# This allows chrooted users to access /ftphome2 which points to /mnt/data/ftphome2
mount --bind /mnt/data/ftphome2 /sftp_root/ftphome2

# Start SSH server
exec /usr/sbin/sshd -D
