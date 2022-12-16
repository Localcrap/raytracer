import matplotlib.pyplot as plt
import csv

# Open the CSV file and read the data
with open('triangleIntersection.csv', 'r') as f:
    reader = csv.reader(f)
    data = list(reader)

# Extract the data for the bar chart
triangles = [int(row[0]) for row in data]
moller_times = [int(row[1]) for row in data]
generic_times = [int(row[2]) for row in data]
inside_outside_times = [int(row[3]) for row in data]

# Set the bar width
bar_width = 0.25

# Set the x-axis values
x_values = [i for i in range(len(triangles))]

# Create the bar chart
plt.bar(x_values, moller_times, bar_width, label='Moller Trombore')
plt.bar([x + bar_width for x in x_values], generic_times, bar_width, label='Generic Triangle Intersection')
plt.bar([x + 2*bar_width for x in x_values], inside_outside_times, bar_width, label='Inside Outside Test')

# Add labels and title
plt.xlabel('Number of Triangles')
plt.ylabel('Time (ms)')
plt.title('Algorithm Performance')

# Add a legend
plt.legend()

# Show the plot
plt.show()
