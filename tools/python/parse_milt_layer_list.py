# -*- coding: utf-8 -*-

 # web scraping for MILT wms layer list 
 # http://nrb-www.mlit.go.jp/webmapc/LayerList.html
# python3 parse_milt_layer_list.py > list.csv
 # 2019-02-01 K.OHWADA

from bs4 import BeautifulSoup
import re
import json

COMMA = ", "
UNDER_BAR  = "_"
EMPTY = ""

def conv_year(str):
    str = str.replace("平成", "H")
    str = str.replace("昭和", "S")
    str = str.replace("大正", "T")
    str = str.replace("年度", EMPTY)
    str = str.replace("年", EMPTY)
    str = str.replace("行政区域", EMPTY)
    return str
# ---

def print_csv(name, year, kind, kind2):
    year = conv_year(year)
    if len(name) > 0:
        print(name, end="")
        print(COMMA, end="")
        print(year, end="")
        print(UNDER_BAR, end="")
        print(kind, end="" )
        if len(kind2) >0:
            print(UNDER_BAR, end="")
            print(kind2 )
        else:
            print()
# ---


def parse_content(content):
    name = ""
    year = ""
    kind = ""
    kind2 = ""
    tr_all =  content.find_all("tr")
    if not  tr_all:
        return;
    for tr in tr_all:
        td_all = td_all =  tr.find_all("td")
        if len(td_all) == 5:
            str0 = td_all[0].text
            year = td_all[1].text
            name = td_all[4].text
            kind = str0
            kind2 = ""
        elif len(td_all) == 4:
            str0 = td_all[0].text
            str1 = td_all[1].text
            str2 = td_all[1].text
            name = td_all[3].text
            if str0.find("年") > 0:
                year = str0
                int_rowspan0 = int( td_all[0].get("rowspan") )
                rowspan3 = td_all[3].get("rowspan")
                if rowspan3 == "4":
                # S05-a-10S
                    pass
                elif int_rowspan0 == 4:
                # N03-180101_100
                    kind = str1
                    kind2 = ""
                elif int_rowspan0 >= 5:
                # N02-16_100
                    pass
                else:
                # C02-06_100N
                    pass
            elif str1.find("年") > 0:
                year = str1
                str_rowspan0 = str( td_all[0].get("rowspan") )
                str_colspan0 = td_all[0].get("colspan")
                str_rowspan1 = td_all[1].get("rowspan")
                str_colspan1 = td_all[1].get("colspan")
                if str_colspan0 == "2":
                # N06-16_100
                    pass
                elif str_rowspan1 == "2":
                # N03-180101
                    kind = str0
                    kind2 = ""
                elif str_rowspan0 == "4":
                # S05-a-10S
                    kind2 = ""
                elif str_rowspan0 == "14":
                # N06-17_600
                    kind2 = str0
                elif str_colspan1 == "2":
                # N07-11_100
                    kind = str0
                    kind2 = ""
                else:
                    kind2 = str0
        elif len(td_all) == 3:
            str0 = td_all[0].text
            name = td_all[2].text
            if str0.find("年") > 0:
                year = str0
            else:
                kind = str0
                kind2 = ""
        else:
            continue
        print_csv(name, year, kind, kind2)
# ---        
     
FILE_PATH = "milt_layer_list.html"

text = open( FILE_PATH ).read()
soup = BeautifulSoup(text, "html.parser")
content_all = soup.find_all("div", class_="content")
for content in content_all:
    parse_content(content)



