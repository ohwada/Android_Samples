# send OSM Message for Android OSM reciever
# 2018-05-01 K.OHWADA

import argparse
import random
import time

from pythonosc import osc_message_builder
from pythonosc import udp_client

IP = "192.168.1.3"
PORT= 57110
OSC_ADDR = "/s_new"

client = udp_client.SimpleUDPClient(IP, PORT)

msg = osc_message_builder.OscMessageBuilder(address=OSC_ADDR)
msg.add_arg("node")
msg.add_arg(1000)
msg.add_arg(1)
msg.add_arg(0)
msg.add_arg("freq")
msg.add_arg(440.0)
msg = msg.build()
client.send(msg)
