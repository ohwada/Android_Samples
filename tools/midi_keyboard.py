# -*- coding: utf-8 -*-

# MIDI Keyboard
# send MIDI Message when touch key
# 2018-05-01 K.OHWADA
# oroginal : http://d.hatena.ne.jp/kadotanimitsuru/20100112/midi

'''MIDI keyboard.

マウス左ボタンで発音、右ボタンで完全5度を同時に発音。
スペースバーを押しながらだとサステイン。
鍵盤の下の方を押すほど大音量。
'''

import pygame
import pygame.midi
from pygame.locals import *

OUTPUT_ID = 0

INSTRUMENT = 0  # 楽器の種類 (0-127)
#   0:Piano, 19:Organ, 56:Trumpet, 91:Voice 等
# https://ja.wikipedia.org/wiki/General_MIDI

KEY_WIDTH = 20  # 鍵盤の幅
WIDTH, HEIGHT = 800, 128  # 画面の大きさ

FPS = 60
NOTE_CENTER = 60  # 中央の音。C(ド)の音を指定
COLOR = 0, 255, 200  # 色
WHITE_COLOR = 255, 255, 255  # 白鍵の色
BLACK_COLOR = 0, 0, 50  # 黒鍵の色
BG_COLOR = 100, 0, 50  # 背景色

KEY_COLOR = 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0  # 0=白鍵, 1=黒鍵
NOTE_NAME = ('C', 'C#', 'D', 'D#', 'E',
             'F', 'F#', 'G', 'G#', 'A', 'A#', 'B')


def main():
    pygame.init()
    pygame.midi.init()
    screen = pygame.display.set_mode((WIDTH, HEIGHT))
    #midiout = pygame.midi.Output(pygame.midi.get_default_output_id())
    midiout = pygame.midi.Output(OUTPUT_ID)
    midiout.set_instrument(INSTRUMENT)
    clock = pygame.time.Clock()
    clock.tick(FPS)
    keys = WIDTH // KEY_WIDTH + 1
    keylist = [False] * (keys + 7)
    note_start = NOTE_CENTER - keys // 2
    note_no = None
    vel = 0
    sustain = False
    while True:
        for e in pygame.event.get():
            if e.type is QUIT:
                return
            elif e.type is KEYDOWN and e.key is K_ESCAPE:
                return
            elif e.type is KEYDOWN and e.key is K_SPACE:
                sustain = True
            elif e.type is KEYUP and e.key is K_SPACE:
                sustain = False
                note_no = None
                for key, b in enumerate(keylist):
                    if b:
                        midiout.note_off(note_start + key, 0)
                        keylist[key] = False
            elif e.type is MOUSEBUTTONDOWN and (
                            e.button == 1 or e.button == 3):
                x, y = e.pos
                vel = 128 * y // HEIGHT
                key = x // KEY_WIDTH
                keylist[key] = True
                note_no = note_start + key
                midiout.note_on(note_no, vel)
                if e.button == 3:
                    keylist[key + 7] = True
                    midiout.note_on(note_no + 7, vel)
            elif e.type is MOUSEBUTTONUP and (
                            e.button == 1 or e.button == 3):
                if not sustain:
                    note_no = None
                    for key, b in enumerate(keylist):
                        if b:
                            midiout.note_off(note_start + key, 0)
                            keylist[key] = False
        screen.fill(BG_COLOR)
        for key in range(keys):
            x = key * KEY_WIDTH
            pygame.draw.rect(
                screen,
                (WHITE_COLOR, BLACK_COLOR)[
                    KEY_COLOR[(note_start + key) % 12]],
                (x + 1, 0, KEY_WIDTH - 2, HEIGHT))
            if keylist[key]:
                pygame.draw.rect(
                    screen, COLOR, (x, 0, KEY_WIDTH, HEIGHT), 3)
        clock.tick(FPS)
        pygame.display.flip()
        notes = []
        for key, b in enumerate(keylist):
            if b:
                nn = note_start + key
                notes.append('{0}:{1}'.format(NOTE_NAME[nn % 12], nn))
        pygame.display.set_caption(', '.join(notes))

try:
    main()
finally:
    pygame.quit()

# Public Domain. 好きに流用してください。