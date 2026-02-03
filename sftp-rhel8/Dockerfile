FROM rockylinux:8

# Install OpenSSH server
RUN dnf update -y && dnf install -y openssh-server sudo && dnf clean all

# Create a dedicated group and user for SFTP
RUN groupadd sftp_users
RUN useradd -m -g sftp_users -s /sbin/nologin sftpuser

# Set password for the SFTP user
RUN echo 'sftpuser:password' | chpasswd

# Create a directory for uploads and set permissions
RUN mkdir -p /home/sftpuser/upload
RUN chown root:root /home/sftpuser
RUN chmod 755 /home/sftpuser
RUN chown sftpuser:sftp_users /home/sftpuser/upload
RUN chmod 755 /home/sftpuser/upload

# Create test files
RUN echo "This is a test file from RHEL8 server." > /home/sftpuser/upload/testfile_rhel8.txt
RUN echo "Another file for testing downloads." > /home/sftpuser/upload/anothertest_rhel8.txt

# Configure SSH for SFTP
COPY sshd_config /etc/ssh/sshd_config
RUN ssh-keygen -A

# Expose port 22
EXPOSE 22

# Start SSH server
CMD ["/usr/sbin/sshd", "-D"]
