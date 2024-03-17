import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt

line = pd.read_csv('resultsMultCpp.csv', sep=',', index_col=False)
block = pd.read_csv('resultsMultBlockCpp.csv', sep=',', index_col=False)

line['Time'] /= 60
block['Time'] /= 60


line = line[line['Dimension'] > 4000].dropna()
block_128 = block.loc[block['BlockSize'] == 128]
block_256 = block.loc[block['BlockSize'] == 256]
block_512 = block.loc[block['BlockSize'] == 512]

# Combine DataFrames and add a 'Source' column to distinguish them
line['Source'] = 'line'
block_128['Source'] = 'block_128'
block_256['Source'] = 'block_256'
block_512['Source'] = 'block_512'

combined_df = pd.concat([line, block_128, block_256, block_512])

# Create boxplot
sns.boxplot(x='Dimension', y='Time', hue='Source', data=combined_df, palette=['skyblue', 'salmon', 'lightgreen', 'orange'])

# Set labels and title
plt.xlabel('Dimension')
plt.ylabel('Time (min)')
plt.title('Exec Time per Dimension')

# Show plot
plt.show()