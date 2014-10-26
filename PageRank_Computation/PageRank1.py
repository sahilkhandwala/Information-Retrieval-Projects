import math, sys
d= 0.85

global pageRank

# Pagerank algorithm as in the question

def calcPageRank(allPages, inLinks, outLinks, count):
    val_list = []
    noOutLinks = []
    k = 1
    oldPerplexity = 0
    for page in allPages:
        if page not in outLinks:
            noOutLinks.append(page)        
    pagerank = {}
    
    for key in allPages:
        pagerank[key] = 1.0/count
        
    newPerplexity = perplexity(pagerank)
        
    while testConvergence(oldPerplexity, newPerplexity, val_list):
        print k, newPerplexity
        sinkPR = 0
        newPageRank = {}
        
        for page in noOutLinks:
            sinkPR += pagerank[page]
        for key in pagerank.keys():
            newPageRank[key] = (1-d)/count
            newPageRank[key] += d * (sinkPR/count)
            for inlink in inLinks[key]:
                    newPageRank[key]+= d*(pagerank[inlink]/outLinks[inlink])
         
        pagerank = newPageRank 
        oldPerplexity = newPerplexity
        newPerplexity = perplexity(pagerank) 
        k = k+1       
              
    return pagerank

# testing for convergence , when we get 4 consecutive values of change in perplexity less than 1, we stop

def testConvergence(oldPerplexity, newPerplexity, val_list):
    value = abs(newPerplexity - oldPerplexity)
    if value < 1:
        val_list.append(value)
        if len(val_list) > 4:
            return False
        else:
            return True
    else:
        return True

# Calculating the perplexity which is shanon entropy raised to the power of 2
    
def perplexity(pagerank):
    entropy = 0
    for page in pagerank.keys():
        p = pagerank[page]
        entropy+=p*math.log(p,2)
    entropy = entropy * -1    
    return 2**entropy

# Sorting the Pagerank dictionary in descending order
    
def sortDict(dictin):
    items  = [(v,k) for k, v in dictin.items()]
    items.sort()
    items.reverse()
    items = [(k,v) for v, k in items]
    
    for i in range(50):
        print items[i]
        
# Sorting the inLinks dictionary to get the count of nodes having the highest inlinks        
        
def sortInLinks(inLinks):
    sorted_inLinks = sorted(inLinks,key = lambda x : len(inLinks[x]), reverse = True)
    x = 1
    print('TOP 50 PAGES ACCORDING TO PAGE INLINKS')
    while(x < 51):
        print(str(x) + ' : ' + str([sorted_inLinks[(x-1)]]) + "   InLinks Count     : " + str(len(inLinks[sorted_inLinks[(x-1)]])))
        x += 1    
        
# Reading the input of the graph        
    
def readInputGraph():
    count = 0;
    outLinks = {}
    inLinks = {}
    totalPages = []
    
    f = open("sys.argv[1]", "r")
    content = f.readlines()
    
    for lines in content:
        lines = lines.strip()
        line = lines.split(" ")
        
# The first item is the root node         
        nodes = line[0]
        line.remove(nodes)
        
# After removing the root node, the remaining are the incoming links to the root node        
        noDuplicates = set()
        for edges in line:
            noDuplicates.add(edges)
        
        inLinks[nodes] = noDuplicates
        totalPages.append(nodes)
        count = count+1
# Calculating the outlinks for a node        
    
        noDuplicates2 = set()
        for edges in line:
            noDuplicates2.add(edges)
            
        
        for edges in noDuplicates2:
            if edges in outLinks:
                outLinks[edges]+=1
            else:
                outLinks[edges]=1
        
        
    return inLinks, outLinks, totalPages, count
    
inLinks, outLinks, totalPages, count = readInputGraph()

pageRank = calcPageRank(totalPages, inLinks, outLinks, count)

#for key in pageRank:
#    print key, pageRank[key] 

sortInLinks(inLinks)    
sortDict(pageRank)    
