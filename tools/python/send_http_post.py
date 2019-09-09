# send HTTP Post Rquest
# 2019-08-01 K.OHWADA

import json
import urllib.request

# echo back Server
URL = 'https://httpbin.org/post'

POST_PARAMS = {
    'foo': 123,
}

HEADERS = {
    'Content-Type': 'application/json',
}

data = json.dumps(POST_PARAMS).encode()
req = urllib.request.Request(URL, data, HEADERS)
with urllib.request.urlopen(req) as res:
    body = res.read()
    response = json.loads(body)
    print (response)
