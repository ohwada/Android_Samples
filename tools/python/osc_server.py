# recieve OSM Message for Android JavaOSM 
# 2018-05-01 K.OHWADA

import argparse
import math

from pythonosc import dispatcher
from pythonosc import osc_server

IP ="127.0.0.1"
PORT= 57110
OSC_ADDR1 = "/s_new"
OSC_ADDR2 = "/n_free"

osc_dispatcher = dispatcher.Dispatcher()
osc_dispatcher.map(OSC_ADDR1, print)
osc_dispatcher.map(OSC_ADDR2, print)

server = osc_server.ThreadingOSCUDPServer(
      (IP, PORT), osc_dispatcher)
print("Serving on {}".format(server.server_address))
server.serve_forever()
