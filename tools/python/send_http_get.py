# send HTTP Get Rquest
# 2019-08-01 K.OHWADA

import urllib.request
import json

# echo back Server
URL = 'https://httpbin.org/get'

GET_PARAMS = {
    'foo': 123,
}

URL_FORMAT = '{}?{}'

params = urllib.parse.urlencode(GET_PARAMS);
url = URL_FORMAT.format(URL, params)

#req = urllib.request.Request(URL)
req = urllib.request.Request(url)
with urllib.request.urlopen(req) as res:
    body = res.read()
    response = json.loads(body)
    print (response)
