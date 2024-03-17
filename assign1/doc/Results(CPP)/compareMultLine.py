import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt

mL0 = pd.read_csv("mean_results.csv", nrows=11)
mLP1 = pd.read_csv("mean_results.csv", skiprows=13, nrows=4)
mLP2 = pd.read_csv("mean_results.csv", skiprows=19)

#change to minutes
#mL0['Time'] /= 60
#mLP1['Time'] /= 60
#mLP2['Time'] /= 60

mL0_cut = mL0[mL0['Dimension'] > 4000].dropna()

x_values_cut = mLP1['Dimension'].unique()
x_values = mL0['Dimension'].unique()

plt.plot(mL0_cut['Dimension'], mL0_cut['Time'], marker='o', label='ML0')
plt.plot(mLP1['Dimension'], mLP1['Time'], marker='o', label='MLP1')
plt.plot(mLP2['Dimension'], mLP2['Time'], marker='o', label='MLP2')

plt.title('Mean execution Time per Dimension')
plt.xlabel('Dimension')
plt.ylabel('Execution Time(s)')

plt.xticks(x_values_cut)
plt.grid(axis='y')
plt.legend(loc='best')

plt.show()

plt.plot(mLP1['Dimension'], mLP1['MFlops'], marker='o', label='MLP1')
plt.plot(mLP2['Dimension'], mLP2['MFlops'], marker='o', label='MLP2')

plt.title('MFlops per Dimension')
plt.xlabel('Dimension')
plt.ylabel('MFlops')

plt.xticks(x_values_cut)
plt.grid(axis='y')
plt.legend(loc='best')

plt.show()

plt.plot(mLP1['Dimension'], mLP1['Speedup'], marker='o', label='MLP1')
plt.plot(mLP2['Dimension'], mLP2['Speedup'], marker='o', label='MLP2')

plt.title('Speedup per Dimension')
plt.xlabel('Dimension')
plt.ylabel('Speedup')

plt.xticks(x_values_cut)
plt.grid(axis='y')
plt.legend(loc='best')

plt.show()

plt.plot(mLP1['Dimension'], mLP1['Efficiency'], marker='o', label='MLP1')
plt.plot(mLP2['Dimension'], mLP2['Efficiency'], marker='o', label='MLP2')

plt.title('Efficiency per Dimension')
plt.xlabel('Dimension')
plt.ylabel('Efficiency')

plt.xticks(x_values_cut)
plt.grid(axis='y')
plt.legend(loc='best')

plt.show()

plt.plot(mL0['Dimension'], mL0['L1_DCM'], marker='o', label='L1_DCM')
plt.plot(mL0['Dimension'], mL0['L2_DCM'], marker='o', label='L2_DCM')

plt.title('Cache Misses')
plt.xlabel('Dimension')
plt.ylabel('Cache Misses')

plt.xticks(x_values)
plt.grid(axis='y')
plt.legend(loc='best')

plt.show()
