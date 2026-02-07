FROM registry.access.redhat.com/ubi8/ubi:8.10

# Install OpenSSH server
RUN dnf update -y && dnf install -y openssh-server sudo python3 && dnf clean all

# Create a dedicated group and user for SFTP
RUN groupadd sftp_users
RUN useradd -M -g sftp_users -s /bin/bash sftpuser

# Set password for the SFTP user
RUN echo 'sftpuser:password' | chpasswd

# 1. 建立真實路徑 (模擬 Root 看到的)
RUN mkdir -p /sftp_jail/bin
RUN cp /bin/bash /sftp_jail/bin/bash

# 2. 建立 Chroot 監獄結構 (這是 User 被關進去的地方)
#    我們把監獄設在 /sftp_jail
RUN mkdir -p /sftp_jail/ftphome2/upload

RUN mkdir -p /sftp_jail/home/sftpuser

# 3. 設定權限
#    Chroot 的根目錄 (/sftp_jail) 必須屬於 root 且不可寫入
RUN chown root:root /sftp_jail
RUN chmod 755 /sftp_jail

#    設定內部的 ftphome2
RUN chown root:root /sftp_jail/ftphome2
RUN chmod 755 /sftp_jail/ftphome2

#    設定 upload 資料夾給 sftpuser
RUN chown sftpuser:sftp_users /sftp_jail/ftphome2/upload
RUN chmod 755 /sftp_jail/ftphome2/upload

RUN chown sftpuser:sftp_users /sftp_jail/home/sftpuser
RUN chmod 755 /sftp_jail/home/sftpuser

# Create test files with BIG5 encoding
# Create test files with BIG5 encoding using Python
RUN python3 -c "open('/sftp_jail/ftphome2/upload/testfile_rhel8.txt','wb').write('This is a test file from RHEL8 server.\n'.encode('big5'))" \
    && python3 -c "open('/sftp_jail/ftphome2/upload/anothertest_rhel8.txt','wb').write('Another file for testing downloads.\n'.encode('big5'))" \
    && chown sftpuser:sftp_users /sftp_jail/ftphome2/upload/*.txt

# --- 關鍵修改：建立一個「能活著」的 Chroot 監獄 ---

# 1. 基礎目錄
RUN mkdir -p /sftp_jail/usr/libexec/openssh
RUN mkdir -p /sftp_jail/lib64
RUN mkdir -p /sftp_jail/etc
RUN mkdir -p /sftp_jail/dev

# 2. 複製 sftp-server
RUN cp /usr/libexec/openssh/sftp-server /sftp_jail/usr/libexec/openssh/

RUN ln -s lib64 /sftp_jail/lib

# 複製腳本進去
COPY install_libs.sh /usr/local/bin/install_libs.sh

# 賦予執行權限並執行
RUN chmod +x /usr/local/bin/install_libs.sh && /usr/local/bin/install_libs.sh

# 4. [關鍵] 複製 NSS 模組 (解決 unknown user 問題)
RUN cp -v -L /lib64/libnss_files.so.2 /sftp_jail/lib64/ || true
RUN cp -v -L /lib64/libnss_compat.so.2 /sftp_jail/lib64/ || true
# RHEL 8 某些版本可能需要這個
RUN cp -v -L /lib64/libnss_systemd.so.2 /sftp_jail/lib64/ || true
RUN ln -s lib64 /sftp_jail/lib 2>/dev/null || true

# 5. [關鍵] 複製設定檔
#    沒有 nsswitch.conf，程式可能不知道要去讀 passwd
RUN echo "passwd: files" > /sftp_jail/etc/nsswitch.conf
RUN echo "group: files" >> /sftp_jail/etc/nsswitch.conf

#    複製使用者資訊
RUN grep "sftp_users" /etc/group > /sftp_jail/etc/group
RUN grep "sftpuser" /etc/passwd > /sftp_jail/etc/passwd

# 6. [選用] 修正可能的時區與亂碼問題
RUN cp /etc/localtime /sftp_jail/etc/ || true

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
