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
RUN useradd -m -g sftp_users -s /sbin/nologin sftpuser

# Set password for the SFTP user (replace with a secure password in production)
RUN echo 'sftpuser:password' | chpasswd

# Create a directory for uploads and set permissions
RUN mkdir -p /home/sftpuser/upload
RUN chown root:root /home/sftpuser
RUN chmod 755 /home/sftpuser
RUN chown sftpuser:sftp_users /home/sftpuser/upload
RUN chmod 755 /home/sftpuser/upload

# Create test files
RUN echo "This is a test file from RHEL7 server." > /home/sftpuser/upload/testfile_rhel7.txt
RUN echo "Another file for testing downloads." > /home/sftpuser/upload/anothertest_rhel7.txt

# Configure SSH for SFTP
COPY sshd_config /etc/ssh/sshd_config
RUN ssh-keygen -A

# Expose port 22
EXPOSE 22

# Start SSH server
CMD ["/usr/sbin/sshd", "-D"]
