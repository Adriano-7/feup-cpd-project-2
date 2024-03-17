import pandas as pd
import matplotlib.pyplot as plt

multJ = pd.read_csv('Results(Julia)/resultsMultJulia.csv', sep=',', index_col=False)
multCpp = pd.read_csv('Results(Cpp)/resultsMultCpp.csv', sep=',', index_col=False)
multLineJ = pd.read_csv('Results(Julia)/resultsMultLineJulia.csv', sep=',', index_col=False)
multLineCpp = pd.read_csv('Results(Cpp)/resultsMultLineCpp.csv', sep=',', index_col=False)

multJ = multJ.groupby(['Dimension']).mean().reset_index()
multCpp = multCpp.groupby(['Dimension']).mean().reset_index()
multLineJ = multLineJ.groupby(['Dimension']).mean().reset_index()
multLineCpp = multLineCpp.groupby(['Dimension']).mean().reset_index()

multLineCpp = multLineCpp[multLineCpp['Dimension'] < 4000].dropna()

x_values = multJ['Dimension'].unique()

plt.plot(multJ['Dimension'], multJ['Time'], marker='o', label='Julia')
plt.plot(multCpp['Dimension'], multCpp['Time'], marker='o', label='CPP')

plt.title('Julia VS Cpp - Mult Exec. Time')
plt.xlabel('Dimension')
plt.ylabel('Execution Time(s)')

plt.xticks(x_values)
plt.grid(axis='y')
plt.legend(loc='best')

plt.show()

plt.plot(multLineJ['Dimension'], multLineJ['Time'], marker='o', label='Julia')
plt.plot(multLineCpp['Dimension'], multLineCpp['Time'], marker='o', label='CPP')

plt.title('Julia VS Cpp - MultLine Exec. Time')
plt.xlabel('Dimension')
plt.ylabel('Execution Time(s)')

plt.xticks(x_values)
plt.grid(axis='y')
plt.legend(loc='best')

plt.show()