import matplotlib.pyplot as plt
import numpy as np
import subprocess
import sys

mainfolder = '/home/qumquat/uni/E24/2015/'

for i in range(21):
    filename = "test"+str(i)+".txt"

    subprocess.run('truncate -s 0 ' + mainfolder + 'tp1BuiltIn/res/times'+str(i)+'.txt', shell=True, executable="/bin/bash")

    command = "java -cp /home/qumquat/uni/E24/2015/tp1BuiltIn/out/production/tp1BuiltIn Main /home/qumquat/uni/E24/2015/tp1BuiltIn/testFiles/"+filename+" /home/qumquat/uni/E24/2015/tp1BuiltIn/res/res"+str(i)+".txt >> " + mainfolder + "tp1BuiltIn/res/times"+str(i)+".txt"

    for j in range(int(sys.argv[1])):
        subprocess.run(command, shell = True, executable="/bin/sh")

xSize = []
yTime = []

for i in range(21):
    filename = mainfolder + "tp1BuiltIn/res/times"+str(i)+".txt"
    
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

plt.title("Built-in sort")
plt.xlabel("Nombre d'entrepôts")
plt.ylabel("Temps d'éxécution (ms)")

plt.savefig('builtin.png')
#plt.show()
