import numpy as np
import matplotlib.pyplot as plt
import csv

x = []
y = []
z = []
Xpop = np.arange(4)
with open('BVHTraversalnumber.csv','r') as csvfile:
    plots = csv.reader(csvfile, delimiter = ',')

    for row in plots:
        x.append(int(row[0]))
        y.append(int(row[1]))
        z.append(int(row[2]))

# Create bars and choose color
plt.bar(Xpop, y, color = "b", width = 0.25, label = "Sequential")
plt.bar(Xpop + 0.25, z, color = "g", width = 0.25, label = "BVH")

bars = (x)

# Add title and axis names
plt.title('Intersections In A Render')
plt.xlabel('Number Of Objects i scene')
plt.ylabel('Values')
plt.yscale("log")
#plt.grid(1, which = "both")
plt.legend()
 
# Create names on the x axis
plt.xticks(Xpop, bars)
 
# Show graph
plt.show()
