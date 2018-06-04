# scan MIDI Device
# 2018-05-01 K.OHWADA
# reference : http://np2lkoo.hatenablog.com/entry/2016/08/31/024827

import pygame
import pygame.midi

pygame.init()
pygame.midi.init()
count = pygame.midi.get_count()
print("get_default_input_id:%d" % pygame.midi.get_default_input_id())
print("get_default_output_id:%d" % pygame.midi.get_default_output_id())
print("No:(interf, name, input, output, opened)")
#for i in range(count):
#    print("%d:%s" % (i, pygame.midi.get_device_info(i))