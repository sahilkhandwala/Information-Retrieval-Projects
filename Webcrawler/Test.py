import urllib2
from bs4 import BeautifulSoup
import time
import requests
import re
from urlparse import urlparse, urljoin

prefix = "/wiki/"
forbiddenURL = "/wiki/Main_Page"
total_crawled_set = set()

def validURL(url):
    if url.startswith(prefix) and not url.startswith(forbiddenURL):
        #if url.find(':') == -1:
        if ':' not in url:
            return True
    return False

def check_phrase(url,keyPhrase):
    html2 = requests.get(url)
    return re.search(keyPhrase, html2.content,re.IGNORECASE) is not None
    
def sub_crawler(seed_url_list, total_crawled_set, keyPhrase):
    to_crawl_set = set()
    for url in seed_url_list:
        try:
            response = urllib2.urlopen(url)
            html = response.read()
            soup = BeautifulSoup(html)
            link = soup.find('link', rel ='canonical')
            link1 = link['href']
            if link1 not in total_crawled_set and check_phrase(link1, keyPhrase):
                total_crawled_set.add(link1)
                findAllLinks = soup.find_all('a', href=True)
                for l in findAllLinks:
                    if validURL(l.get('href')):
                        u1 = urljoin('http://en.wikipedia.org/',l.get('href'))
                        splitted = u1.split("#")
                        to_crawl_set.add(splitted[0])           
            else:
                continue   
        except Exception as e:
            print('Error : %s' % e)
            pass
        print len(total_crawled_set)
    return to_crawl_set, total_crawled_set

def main_crawler(seed_url,keyPhrase):
    DEPTH = 0
    global total_crawled_set    
    to_crawl_set1 = set()
    to_crawl_set1.add(seed_url)
    while DEPTH <=2:
        to_crawl_set1, total_crawled_set = sub_crawler(to_crawl_set1, total_crawled_set, keyPhrase)
        DEPTH = DEPTH + 1

if __name__ == '__main__':
    main_crawler("http://en.wikipedia.org/wiki/Gerard_Salton", "")
f = open('output1.txt', 'w')
for item in total_crawled_set:
    f.write(str(item) + '\n')
f.close()
