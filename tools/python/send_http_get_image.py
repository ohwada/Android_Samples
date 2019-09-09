# send HTTP Get Rquestto imsge url
# and save Response to local file
# 2019-08-01 K.OHWADA

import os
import pprint
import time
import urllib.error
import urllib.request

URL = 'https://www.python.org/static/img/python-logo.png'
FILE_PATH = 'python-logo.png'

# reference : https://note.nkmk.me/python-download-web-images/
def download_file(url, dst_path):
    try:
        with urllib.request.urlopen(url) as web_file:
            data = web_file.read()
            with open(dst_path, mode='wb') as local_file:
                local_file.write(data)
    except urllib.error.URLError as e:
        print(e)

download_file(URL, FILE_PATH)

