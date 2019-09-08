#!/usr/bin/env python
#coding:utf-8

# send SSDP  Discovery on UDP
# 2029-08-01 K.OHWADA
# reference : http://telracsmoratori.blog.fc2.com/blog-entry-106.html

import socket
import sys


# UPnP Broadcast Address and Port
ADDRESS = "239.255.255.250"
PORT    = 1900

TIMEOUT = 100
GETMAX  = 65536
MYADDR  = socket.gethostbyname(socket.gethostname())
#TYPE    = "upnp:rootdevice"
TYPE    = "ssdp:all"

CRLF = "\r\n"
REQUEST_EACH_LINE = [                  \
"M-SEARCH * HTTP/1.1"                 ,\
"HOST: %s"%(ADDRESS + ":" + str(PORT)),\
"MAN: \"ssdp:discover\""              ,\
"MX: %s"%(TIMEOUT)                    ,\
"ST: %s"%(TYPE)                       ,\
]

# Main
print TYPE

# create Request
request = ""
for eachline in REQUEST_EACH_LINE :
	request += eachline + CRLF
request += CRLF

# create socket
sock = socket.socket(socket.AF_INET , socket.SOCK_DGRAM)
sock.settimeout(TIMEOUT)
sock.bind((MYADDR ,PORT))

# send Request
sock.sendto( request , (ADDRESS , PORT))

# recive Response
while (True):
	try:
		data , remoteaddress = sock.recvfrom(GETMAX)
		print data
		# break

	except socket.timeout:
		print "timeouted"
		break

sock.close()
sys.exit()
