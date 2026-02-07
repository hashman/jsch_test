#!/bin/bash
# sftp-rhel8/install_libs.sh

echo "Analyzing dependencies..."
# 這裡不需要一堆反斜線跳脫，寫法跟一般 shell script 一樣
LIBS=$(ldd /usr/libexec/openssh/sftp-server | awk '{ if ($3 == "") print $1; else print $3 }' | grep "/" | sort | uniq)

echo "Found libs: $LIBS"

for LIB in $LIBS; do
    cp -v -L "$LIB" /sftp_jail/lib64/
done

LIBS=$(ldd /bin/bash | awk "{ if (\$3 == \"\") print \$1; else print \$3 }" | grep "/" | sort | uniq)

echo "Found libs: $LIBS"

for LIB in $LIBS; do
    cp -n -L "$LIB" /sftp_jail/lib64/;
done