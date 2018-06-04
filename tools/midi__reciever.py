# -*- coding: utf-8 -*-

# MIDI Reciever
# reciever MIDI Message
# 2018-05-01 K.OHWADA
# reference : http://np2lkoo.hatenablog.com/entry/2016/08/31/024827

import pygame.midi

# quit, when receive this number
MAX_COUNT = 14

pygame.init()
pygame.midi.init()
input_id = pygame.midi.get_default_input_id()
print("input MIDI:%d" % input_id)
i = pygame.midi.Input(input_id)

print ("starting")
print ("full midi_events:[[[status,data1,data2,data3],timestamp],...]")

going = True
count = 0
while going:
    if i.poll():
        midi_events = i.read(10)
        print "full midi_events:" + str(midi_events)
        count += 1
    if count >= MAX_COUNT:
        going = False

i.close()
pygame.midi.quit()
pygame.quit()
exit()

