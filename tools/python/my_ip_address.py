# print my IP address
# 2018-05-01 K.OHWADA

import socket

ip = socket.gethostbyname(socket.gethostname())
print(ip)
