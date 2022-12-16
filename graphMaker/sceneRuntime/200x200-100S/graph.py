import numpy as np
import matplotlib.pyplot as plt
import csv
v = []
w = []
x = []
y = []
z = []
Xpop = np.arange(4)
with open('sceneRuntime.csv','r') as csvfile:
    plots = csv.reader(csvfile, delimiter = ',')

    for row in plots:
        v.append(int(row[0]))
        w.append(int(row[1]))
        x.append(int(row[2]))
        y.append(int(row[3]))
        z.append(int(row[4]))

# Create bars and choose color
plt.bar(Xpop, w, color = "b", width = 0.25, label = "10")
plt.bar(Xpop + 0.25, x, color = "g", width = 0.25, label = "20")
plt.bar(Xpop + 0.50, y, color = "y", width = 0.25, label = "40")
plt.bar(Xpop + 0.70, z, color = "r", width = 0.25, label = "100")
 
bars = (x)

# Add title and axis names
plt.title('My title')
plt.xlabel('categories')
plt.ylabel('values')
#plt.yscale("log")
plt.grid(1, which = "both")
plt.legend()
 
# Create names on the x axis
plt.xticks(Xpop, bars)
 
# Show graph
plt.show()