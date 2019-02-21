#-*- coding: utf-8 -*-

# python3 parse_osm_json.py
# parse OAM Json
# output KML file
# 2019-02-01 K.OHWADA

import json

LF = "\n".encode('utf-8')

# parse Json
f = open('sample.json', 'r')
json_dict = json.load(f)

elements = json_dict["elements"]
nodes = []
for element in elements:
    node = {}
    lon = element["lon"]
    lat = element["lat"]
    tags = element["tags"]
    name =  ""
    info =  "".encode('utf-8')
    node["lon"] = lon
    node["lat"] = lat
    if "name" in  tags:
        name = tags["name"].encode('utf-8')
    else:
        name =  tags["name:ja"].encode('utf-8')
    if "branch" in  tags:
        branch = tags["branch"].encode('utf-8')
        info += branch + LF
    if "opening_hours" in  tags:
        opening_hours = tags["opening_hours"].encode('utf-8')
        info += "営業時間: ".encode('utf-8') + opening_hours + LF
    if "phone" in  tags:
        phone = tags["phone"].encode('utf-8')
        info += "電話番号: ".encode('utf-8') + phone + LF
    if "wheelchair" in  tags:
        wheelchair = tags["wheelchair"].encode('utf-8')
        info += "車いす: ".encode('utf-8') + wheelchair + LF
    if "wheelchair:description" in  tags:
        wheelchair_description = tags["wheelchair:description"].encode('utf-8')
        info += wheelchair_description + LF
    node["name"] = name
    node["info"] = info
    nodes.append(node)
    print( name.decode('utf-8'),  end="" )
    print(", ",  end="")
    print( lat,  end="")
    print(", ",  end="")
    print(lon)
    print( info.decode('utf-8') )
# for end

# make KML 
import simplekml

kml = simplekml.Kml()
for node in nodes:
    name = node["name"].decode('utf-8')
    lat = float( node["lat"] )
    lon =  float( node["lon"] )
    info = node["info"].decode('utf-8')
    kml.newpoint( name=name, description=info, coords=[( lon, lat )] )
# for end

kml.save('sample.kml')
