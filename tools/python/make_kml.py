#-*- coding: utf-8 -*-

# python3 make_kml.py
# make kml file from csv file
# 2019-02-01 K.OHWADA

import csv
import simplekml

kml = simplekml.Kml()

# name, latitude, longitude
with open('sample.csv', 'r') as f:
    reader = csv.reader(f)
    for row in reader:
        name = str(row[0].strip())
        lat = float(row[1].strip())
        lon =float(row[2].strip())
        print(name + ", ",  end="")
        print( lat,  end="")
        print(", ",  end="")
        print( lon)
        kml.newpoint( name=name, coords=[( lon, lat )] )

kml.save('sample.kml')
