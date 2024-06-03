import matplotlib.pyplot as plt
import numpy as np
import subprocess
import sys

mainfolder = '/home/qumquat/uni/E24/2015/'

for i in range(21):
    filename = "test"+str(i)+".txt"

    subprocess.run('truncate -s 0 ' + mainfolder + 'tp12015/res/times'+str(i)+'.txt', shell=True, executable="/bin/bash")

    command = "java -cp /home/qumquat/uni/E24/2015/tp12015/out/production/tp12015/ Main /home/qumquat/uni/E24/2015/tp12015/testFiles/"+filename+" /home/qumquat/uni/E24/2015/tp12015/res/res"+str(i)+".txt >> " + mainfolder + "tp12015/res/times"+str(i)+".txt"

    for j in range(int(sys.argv[1])):
        subprocess.run(command, shell = True, executable="/bin/sh")

xSize = []
yTime = []

for i in range(21):
    filename = mainfolder + "tp12015/res/times"+str(i)+".txt"
    
    with open(filename, "r") as file:
        timeSum = 0

        for line in file:
            n = int(line.split(",")[0])
            timeSum += float(line.split(",")[1])
        
        yTime.append( timeSum / sum(1 for _ in open(filename)) )
        
        xSize.append(n)

xSize = np.array(xSize)
yTime = np.array(yTime)

plt.plot(xSize, yTime)

plt.title("Selection sort")
plt.xlabel("Nombre d'entrepôts")
plt.ylabel("Temps d'éxécution (ms)")
plt.vlines(xSize, 0, yTime, linestyle="dashed")


plt.savefig('selection.png')
#plt.show()
