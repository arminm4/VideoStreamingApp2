import os, re

j = 1
for f in os.walk("./server8/person-of-interest-trailer"):
    for i in f[2]:
        os.rename(str(f[0])+'/'+str(i), str(f[0])+'/'+str(j)+'.jpg')
        j += 1
