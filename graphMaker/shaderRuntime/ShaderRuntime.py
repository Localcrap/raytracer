import matplotlib.pyplot as plt
import csv

# Set up the data for the line graph
shader_implementations = ['lambertian', 'bounce + shadow']
times = [39550, 17793]

# Create the line graph
plt.bar(shader_implementations, times)

# Add axis labels and a title
plt.xlabel('Shader Implementation')
plt.ylabel('Time (ms)')
plt.title('Time Improvements Between Shader Implementations')

# Show the plot
plt.show()
