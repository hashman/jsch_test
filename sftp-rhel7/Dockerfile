FROM centos:7

# Overwrite repo file to point to the altarch vault for aarch64, then install packages
RUN echo '[base]' > /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'name=CentOS-7 - Base - AltArch' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'baseurl=http://vault.centos.org/altarch/7/os/aarch64/' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'gpgcheck=1' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo '' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo '[updates]' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'name=CentOS-7 - Updates - AltArch' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'baseurl=http://vault.centos.org/altarch/7/updates/aarch64/' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'gpgcheck=1' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo '' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo '[extras]' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'name=CentOS-7 - Extras - AltArch' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'baseurl=http://vault.centos.org/altarch/7/extras/aarch64/' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'gpgcheck=1' >> /etc/yum.repos.d/CentOS-Base.repo && \
    echo 'gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7' >> /etc/yum.repos.d/CentOS-Base.repo && \
    yum clean all && \
    yum update -y --nogpgcheck && yum install -y openssh-server sudo --nogpgcheck && yum clean all

# Create a dedicated group and user for SFTP
RUN groupadd sftp_users
RUN useradd -M -g sftp_users -s /sbin/nologin sftpuser

# Set password for the SFTP user (replace with a secure password in production)
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
RUN echo "This is a test file from RHEL7 server." > /mnt/data/ftphome2/upload/testfile_rhel7.txt
RUN echo "Another file for testing downloads." > /mnt/data/ftphome2/upload/anothertest_rhel7.txt
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
