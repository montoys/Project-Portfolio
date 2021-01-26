def selectsensor(data):
    sorteddata = data.sort_values('Avg Temp (°F)')
    print(sorteddata)
    plt.scatter(sorteddata['Avg Temp (°F)'], savgol_filter(sorteddata['Total'], len(sorteddata)-1, 2),label='Total', c='green')
    plt.scatter(sorteddata['Avg Temp (°F)'], savgol_filter(sorteddata['Brooklyn Bridge'], len(sorteddata) - 1, 2), label = 'Brooklyn Bridge', c='blue')
    plt.scatter(sorteddata['Avg Temp (°F)'], savgol_filter(sorteddata['Manhattan Bridge'], len(sorteddata)-1, 2), label = 'Manhattan Bridge', c='red')
    plt.scatter(sorteddata['Avg Temp (°F)'], savgol_filter(sorteddata['Williamsburg Bridge'], len(sorteddata)-1, 2), label = 'Williamsburg Bridge', c='purple')
    plt.scatter(sorteddata['Avg Temp (°F)'], savgol_filter(sorteddata['Queensboro Bridge'], len(sorteddata)-1, 2), label = ' Queensboro Bridge', c='black')
    plt.xlabel('Avg Temp (°F)')
    plt.ylabel('Number of cyclists')
    plt.title('Number of cyclist for day avg temperature')
    plt.legend(loc='best')
    plt.show()
    plt.clf()
    return sorteddata

def initdata(datapath):
    data = pd.read_csv(datapath)
    data.to_numpy()
    return data

def cleandata(data):
    mean = np.sum([float(i[1]) for i in data.items()]) / 214
    std = np.std([i[1] for i in data.items()])
    clean = []
    for i in data.items():
        clean.append((i[1]-mean) / std)
    return clean

def predicttraffictable(data, Final, metric):
    rtotal, probability = pearsonr(data[metric], data['Total'])
    rbrook, probability1 = pearsonr(data[metric], data['Brooklyn Bridge'])
    rman, probability2 = pearsonr(data[metric], data['Manhattan Bridge'])
    rwill, probability3 = pearsonr(data[metric], data['Williamsburg Bridge'])
    rqueen, probability4 = pearsonr(data[metric], data['Queensboro Bridge'])
    if metric == 'Avg Temp (°F)':
        rclean, probability5 = pearsonr(Final[0], Final[8])
    else:
        rclean, probability5 = pearsonr(Final[3], Final[8])
    
    row = ['Total', 'Clean total', 'Brooklyn Bridge', 'Manhattan Bridge', 'Williamsburg Bridge', 'Queensboro Bridge']
    cols = ['r', 'probability']
    data = [[rtotal, probability], [rclean, probability5], [rbrook, probability1], [rman, probability2], [rwill, probability3],[rqueen, probability4]]
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.axis('off')
    ax.set_aspect(.1)
    table = ax.table(cellText=data, colLabels=cols, rowLabels=row, loc='upper center',cellLoc='center')
    table.auto_set_font_size(True)
    table.scale(1.5,15.)
    plt.show()

def predicttrafficregression(sorteddata, Final):
    
    temp = savgol_filter([sorteddata['Avg Temp (°F)'][i] for i in range(len(sorteddata))], len(sorteddata)-1, 2)
    rain = savgol_filter([sorteddata['Precipitation'][i] for i in range(len(sorteddata))], len(sorteddata)-1, 2)
    target = savgol_filter([sorteddata['Total'][i] for i in range(len(sorteddata))], len(sorteddata)-1, 2)
    #tempparam, pcov = curve_fit(xdata=temp, ydata=target)
    #print(rainparam, pconv)
    tempparam = polyfit(x=temp, y=target, deg=2)
    rainparam = polyfit(x=rain, y=target, deg=2)
    print(rainparam, tempparam)
    temp = np.array(temp)
    rain = np.array(rain)
    y1 = np.multiply(tempparam[0], temp**2) + np.multiply(tempparam[1], temp) + tempparam[2]
    y2 = np.multiply(rainparam[0], rain**2) + np.multiply(rainparam[1], rain) + rainparam[2]
    
    plt.scatter(temp, target, label = 'actual', c = 'green')
    plt.plot(temp, y1, label = 'temperature')
    plt.xlabel('temp')
    plt.ylabel('bikers')
    plt.legend()
    plt.show()
    plt.scatter(rain, target, label = 'actual', c = 'green')
    plt.plot(rain, y2, label = 'rain')
    plt.xlabel('rain')
    plt.ylabel('bikers')
    plt.legend()
    plt.show()

def regression(data):
    X = data[['Avg Temp (°F)', 'Precipitation']]
    y = data[['Total']]
    X = X.to_numpy()
    [X_train, X_test, y_train, y_test] = train_test_split(X, y, test_size=0.25, random_state=101)
    [X_train, trn_mean, trn_std] = normalize_train(X_train)
    X_test = normalize_test(X_test, trn_mean, trn_std)
    lmbda = np.logspace(start= -1, stop=2, num=101)
    #print(lmbda)
    MODEL = []
    MSE = []
    for l in lmbda:
        #Train the regression model using a regularization parameter of l
        model = train_model(X_train,y_train,l)
        #Evaluate the MSE on the test set
        mse = error(X_test,y_test,model)
        #Store the model and mse in lists for further processing
        MODEL.append(model)
        MSE.append(mse)
    #Plot the MSE as a function of lmbda
    plt.scatter(lmbda, MSE, c = 'green')
    plt.xlabel('lambda')
    plt.ylabel('MSE')
    plt.title("MSE as a function of Lambda")
    plt.show()
    plt.savefig('problem2 mse vs lmbda')
    #Find best value of lmbda in terms of MSE
    temp = min(MSE)
    ind = MSE.index(temp)
    [lmda_best,MSE_best,model_best] = [lmbda[ind],MSE[ind],MODEL[ind]]
    print('Best lambda tested is ' + str(lmda_best) + ', which yields an MSE of ' + str(MSE_best))
    print(model_best)
    
def normalize_train(X_train):
    mean = []
    std = []
    X = X_train
    mean = np.mean(X_train,axis=0)
    std = np.std(X_train,axis=0)
    X = (X_train - mean) / std

    return X, mean, std

def normalize_test(X_test, trn_mean, trn_std):

    X = X_test

    X = (X_test - trn_mean) / trn_std
    
    return X
def train_model(X,y,l):

    model = linear_model.Ridge(alpha=l)
    model.fit(X, y)
    return model

def error(X,y,model):

    mse = mean_squared_error(y, model.predict(X))
    return mse

def dayrelation(data):
    m = []
    t = []
    w = []
    th = []
    f = []
    sat = []
    sun = []
    for i in range (len(data)):
        if data['Day'][i] == 'Monday':
            m.append(data['Total'][i])
        elif data['Day'][i] == 'Tuesday':
            t.append(data['Total'][i])
        elif data['Day'][i] == 'Wednesday':
            w.append(data['Total'][i])
        elif data['Day'][i] == 'Thursday':
            th.append(data['Total'][i])
        elif data['Day'][i] == 'Friday':
            f.append(data['Total'][i])
        elif data['Day'][i] == 'Saturday':
            sat.append(data['Total'][i])
        else:
            sun.append(data['Total'][i])

    m = [np.mean(m),np.mean(t),np.mean(w),np.mean(th),np.mean(f),np.mean(sat), np.mean(sun)]
    print(m)
    
def inverse(data):
    Precipitation = 'Precipitation'
    plt.scatter(savgol_filter(sorteddata['Total'], len(sorteddata)-1, 2), data[Precipitation],label='Total', c='green')
    plt.scatter(savgol_filter(sorteddata['Brooklyn Bridge'], len(sorteddata) - 1, 2),data[Precipitation], label = 'Brooklyn Bridge', c='blue')
    plt.scatter(savgol_filter(sorteddata['Manhattan Bridge'], len(sorteddata)-1, 2),data[Precipitation], label = 'Manhattan Bridge', c='red')
    plt.scatter(savgol_filter(sorteddata['Williamsburg Bridge'], len(sorteddata)-1, 2),data[Precipitation], label = 'Williamsburg Bridge', c='purple')
    plt.scatter(savgol_filter(sorteddata['Queensboro Bridge'], len(sorteddata)-1, 2),data[Precipitation], label = ' Queensboro Bridge', c='black')
    plt.xlabel('Number of bikers')
    plt.ylabel('Precipitaion')
    plt.title('Precipitation vs number of bikers')
    plt.legend(loc='best')
    plt.show()

def predicttable(data, Final):
    rtotal, probability = pearsonr(data['Total'],data['Precipitation'] )
    rbrook, probability1 = pearsonr(data['Brooklyn Bridge'],data['Precipitation'])
    rman, probability2 = pearsonr(data['Manhattan Bridge'],data['Precipitation'])
    rwill, probability3 = pearsonr(data['Williamsburg Bridge'],data['Precipitation'])
    rqueen, probability4 = pearsonr(data['Queensboro Bridge'],data['Precipitation'])
    rclean, probability5 = pearsonr(Final[8], Final[3])
    
    row = ['Total', 'Clean total', 'Brooklyn Bridge', 'Manhattan Bridge', 'Williamsburg Bridge', 'Queensboro Bridge']
    cols = ['r', 'probability']
    data = [[rtotal, probability], [rclean, probability5], [rbrook, probability1], [rman, probability2], [rwill, probability3],[rqueen, probability4]]
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.axis('off')
    ax.set_aspect(.1)
    table = ax.table(cellText=data, colLabels=cols, rowLabels=row, loc='upper center',cellLoc='center')
    table.auto_set_font_size(True)
    table.scale(1.5,15.)
    plt.show()

if __name__ == '__main__' :
    import os
    import pandas as pd
    import numpy as np
    from numpy import polyfit
    import matplotlib.pyplot as plt
    import scipy as sp
    from scipy.signal import savgol_filter
    from scipy.stats import pearsonr
    from scipy.optimize import curve_fit
    import numpy as np
    import pandas as pd
    from sklearn.linear_model import Ridge
    from sklearn.model_selection import train_test_split
    from sklearn import linear_model
    from sklearn.metrics import mean_squared_error
    import matplotlib.pyplot as plt

    datapath = 'NYC_Bicycle_Counts_2016_Corrected.csv'
    data = initdata(datapath)
    Hightemp = cleandata(data['High Temp (°F)'])
    Lowtemp = cleandata(data['Low Temp (°F)'])
    Precipitation = cleandata(data['Precipitation'])
    Brooklyn = cleandata(data['Brooklyn Bridge'])
    Manhattan = cleandata(data['Manhattan Bridge'])
    Williamsburg = cleandata(data['Williamsburg Bridge'])
    Queensboro = cleandata(data['Queensboro Bridge'])
    Total = cleandata(data['Total'])
    numrain = np.sum([1 for i in data['Precipitation'] if i != 0])
    print(numrain)
    avgtemp = [(data['Low Temp (°F)'][i] + data['High Temp (°F)'][i]) / 2 for i in range(len(data))]
    Final = [avgtemp, Hightemp, Lowtemp, Precipitation, Brooklyn, Manhattan, Williamsburg, Queensboro, Total]
    data.insert(2,'Avg Temp (°F)', avgtemp, True)
    sorteddata = selectsensor(data)
    predicttraffictable(sorteddata, Final, 'Avg Temp (°F)')
    predicttraffictable(sorteddata, Final, 'Precipitation')
    predicttrafficregression(sorteddata, Final)
    #regression(data)
    dayrelation(sorteddata)
    inverse(sorteddata)
    predicttable(sorteddata, Final)
