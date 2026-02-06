#!/bin/bash

# Remove nologin flag (prevents non-root login via pam_nologin)
rm -f /var/run/nologin /run/nologin

# Start SSH server
exec /usr/sbin/sshd -D
