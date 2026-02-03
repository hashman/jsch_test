FROM rockylinux:8

# Install OpenSSH server
RUN dnf update -y && dnf install -y openssh-server sudo && dnf clean all

# Create a dedicated group and user for SFTP
RUN groupadd sftp_users
RUN useradd -M -g sftp_users -s /sbin/nologin sftpuser

# Set password for the SFTP user
RUN echo 'sftpuser:password' | chpasswd

# Create directory structure
# Actual storage: /mnt/data/ftphome2/upload (outside chroot)
# /sftp_root is the chroot base directory
# At runtime, /mnt/data/ftphome2 will be bind-mounted to /sftp_root/ftphome2
RUN mkdir -p /mnt/data/ftphome2/upload

# Set chroot base directory permissions (must be root:root with 755)
RUN mkdir -p /sftp_root
RUN chown root:root /sftp_root
RUN chmod 755 /sftp_root

# Set actual storage directory permissions
RUN chown root:root /mnt/data
RUN chmod 755 /mnt/data
RUN chown root:root /mnt/data/ftphome2
RUN chmod 755 /mnt/data/ftphome2
RUN chown sftpuser:sftp_users /mnt/data/ftphome2/upload
RUN chmod 755 /mnt/data/ftphome2/upload

# Create test files
RUN echo "This is a test file from RHEL8 server." > /mnt/data/ftphome2/upload/testfile_rhel8.txt
RUN echo "Another file for testing downloads." > /mnt/data/ftphome2/upload/anothertest_rhel8.txt
RUN chown sftpuser:sftp_users /mnt/data/ftphome2/upload/*.txt

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
