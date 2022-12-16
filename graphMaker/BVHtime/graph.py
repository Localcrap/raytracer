import numpy as np
import matplotlib.pyplot as plt
import csv

x = []
y = []
z = []
Xpop = np.arange(3)
with open('BVHTraversalTime.csv','r') as csvfile:
    plots = csv.reader(csvfile, delimiter = ',')

    for row in plots:
        x.append(int(row[0]))
        y.append(int(row[1]))
        z.append(int(row[2]))

# Create bars and choose color
plt.bar(Xpop, y, color = "b", width = 0.25, label = "BVH")
plt.bar(Xpop + 0.25, z, color = "g", width = 0.25, label = "Sekvensiell")
 
bars = (x)

# Add title and axis names
plt.title('Time Improvement Between BVH And Sequential Intersection Times ')
plt.xlabel('Number Of Object')
plt.ylabel('Values')
plt.yscale("log")
#plt.grid(1, which = "both")
plt.legend()
 
# Create names on the x axis
plt.xticks(Xpop, bars)
 
# Show graph
plt.show()
