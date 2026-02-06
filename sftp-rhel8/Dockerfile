FROM registry.access.redhat.com/ubi8/ubi:8.10

# Install OpenSSH server
RUN dnf update -y && dnf install -y openssh-server sudo python3 && dnf clean all

# Create a dedicated group and user for SFTP
RUN groupadd sftp_users
RUN useradd -M -g sftp_users -s /bin/bash sftpuser

# Set password for the SFTP user
RUN echo 'sftpuser:password' | chpasswd

# Create directory structure (no chroot, user sees full filesystem)
RUN mkdir -p /ftphome2/upload \
    && chown root:root /ftphome2 \
    && chmod 755 /ftphome2 \
    && chown sftpuser:sftp_users /ftphome2/upload \
    && chmod 755 /ftphome2/upload

# Create test files with BIG5 encoding
# Create test files with BIG5 encoding using Python
RUN python3 -c "open('/ftphome2/upload/testfile_rhel8.txt','wb').write('This is a test file from RHEL8 server.\n'.encode('big5'))" \
    && python3 -c "open('/ftphome2/upload/anothertest_rhel8.txt','wb').write('Another file for testing downloads.\n'.encode('big5'))" \
    && chown sftpuser:sftp_users /ftphome2/upload/*.txt

# Configure SSH for SFTP
COPY sshd_config /etc/ssh/sshd_config
RUN ssh-keygen -A

# Copy entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Expose port 22
EXPOSE 22

# Start SSH server via entrypoint script
CMD ["/entrypoint.sh"]
