import pandas as pd
import matplotlib.pyplot as plt

def noBlock(df): 
    loop = True
    
    if 'L1_DCM' in df.columns and 'L2_DCM' in df.columns:
        groupedL1 = df.groupby('Dimension')['L1_DCM'].mean()
        groupedL2 = df.groupby('Dimension')['L2_DCM'].mean()

        print(groupedL1)
        print(groupedL2)
    
    while(loop):
        dim = input('Dimension (a for all, q to quit): ')
        
        try:
            dim = int(dim)
        except:
            if dim == 'q':
                loop = False
            elif dim == 'a':
                df.boxplot(by='Dimension', column = 'Time')
                plt.title('Time(min) per Dimension')
            else:
                print('invalid value')
        else:
            if dim in df['Dimension'].values:
                df.loc[df['Dimension'] == dim].boxplot(by='Dimension', column = 'Time')
                plt.title('Time(min) for Dimension = ' + str(dim))
            else:
                print('invalid value')
        
        plt.show()
        
def withBlock(df):
    loop = True
    
    if 'L1_DCM' in df.columns and 'L2_DCM' in df.columns:
        groupedL1 = df.groupby(['Dimension', 'BlockSize'])['L1_DCM'].mean()
        groupedL2 = df.groupby(['Dimension', 'BlockSize'])['L2_DCM'].mean()

        print(groupedL1)
        print(groupedL2)
    
    while(loop):
        blk = input('Block Size (q to quit): ')
        dim = input('Dimension (letter for all): ')
        
        try:
            blk = int(blk)
        except:
            if blk == 'q':
                loop = False
            else:
                print('invalid value')
        else:
            try:
                dim = int(dim)
            except:
                if blk in df['BlockSize'].values:
                    df.loc[df['BlockSize'] == blk].boxplot(by='Dimension', column = 'Time')
                    plt.title('Time(min) per Dimension and Box Size =' + str(blk))
            else:
                if blk in df['BlockSize'].values and dim in df['Dimension'].values:
                    df.loc[(df['BlockSize'] == blk) & (df['Dimension'] == dim)].boxplot(by='Dimension', column='Time')
                    plt.title('Time(min) for Dimension = ' + str(dim) + ' and Box Size =' + str(blk))
                else:
                    print('invalid value')

        plt.show()
            
    

def main():
    filename = input('File to parse:')

    df = pd.read_csv(filename, sep=',', index_col=False)

    check = True

    if 'BlockSize' in df.columns:
        check = False

    df['Time'] = df['Time'] / 60
    
    if check:
        noBlock(df)
    else:
        withBlock(df)
    
if __name__ == '__main__':
    main()