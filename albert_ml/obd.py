#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Mar  1 16:55:43 2021
@author: leftee
"""

import pickle
import pandas as pd
import numpy as np
#import matplotlib as plt
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.feature_selection import SelectKBest, chi2, f_classif
from sklearn import preprocessing

# obdData1="opel_corsa_01_clean.csv"
# obdData2="peugeot_207_01_clean.csv"
# df1=pd.read_csv(obdData1,sep = ';')
# df2=pd.read_csv(obdData2,sep = ';')
# frames=[df1,df2]
totalDf=pd.read_csv("machine.csv",sep=',')#pd.concat(frames)#
# totalDf=pd.read_csv("machine.csv")
# totalDf=totalDf.sample(frac = 1)
cleanData=totalDf.dropna()


cleanup_nums = {"drivingStyle": {"EvenPaceStyle": 0, "AggressiveStyle": 1 }}
cleanData = cleanData.replace(cleanup_nums)

dataset=cleanData.values
columns_=cleanData.columns
print(columns_)
newCol=np.delete(columns_,[14,15,16])# columns_.delete()
X=dataset[:,:-3]
# X=pd.DataFrame(data=X,columns=newCol)
# X=np.delete(X,"AltitudeVariation",1)
# X=X.delete('AltitudeVariation')
# X=np.delete(X,"VerticalAcceleration",1)
# X=X.delete('VerticalAcceleration')
# y=pd.to_numeric(dataset[:,-1])
y=dataset[:,-1]
y=y.astype('int')

print(X.shape)
print(X)
print(y)

X_new = SelectKBest(f_classif, k=7).fit_transform(X, y)

# X_new=np.delete(X_new,[0,4,7,8,9,10,11])
# X_new = pd.DataFrame(data=X_newTemp)
# Get columns to keep and create new dataframe with those only
# cols = X_new get_support(indices=True)
# features_df_new = X.iloc[:,cols]
# mask = X_new.get_support()
# new_features = X.columns[mask]
print(X_new.shape)
# print(columns_)
# print("test was done here")
# print(X_new.get_support(indices=True))
# X.columns[X_new.get_support(indices=True)]
# X.columns[X_new.get_support(indices=True)].tolist()
# print(X.columns.shape)
# vector_names = list(X.columns[X_new.get_support(indices=True)])
# print(vector_names)
# print (features_df_new)
# X_new_ds=[
#     X["AltitudeVariation"],
#     X["VehicleSpeedInstantaneous"],
#     X["VehicleSpeedAverage"],
#     X["VehicleSpeedVariation"],
#     X["LongitudinalAcceleration"],
#     X["MassAirFlow"],
#     X["VerticalAcceleration"]
#     ]
X_new_ds=X
# X_new_ds=np.delete(X,,1)



# X_train,X_test,y_train,y_test=train_test_split(X,y,test_size=0.2)
# print("train",X_train.shape,y_train.shape)
# print("test",X_test.shape,y_test.shape)

Xnew_train,Xnew_test,y_train,y_test=train_test_split(X_new,y,test_size=0.2)
print("train",Xnew_train.shape,y_train.shape)
print("test",Xnew_test.shape,y_test.shape)

#from sklearn import preprocessing
from sklearn.model_selection import cross_val_score, cross_val_predict
from sklearn.metrics import accuracy_score, classification_report
from sklearn.metrics import confusion_matrix, roc_auc_score

def print_score(clf, X_train, X_test, y_train, y_test, train=True):
    '''
    v0.1 Follow the scikit learn library format in terms of input
    print the accuracy score, classification report and confusion matrix of classifier
    '''
    lb = preprocessing.LabelBinarizer()
    lb.fit(y_train)
    if train:
        '''
        training performance
        '''
        res = clf.predict(X_train)
        print("Train Result:\n")
        print("accuracy score: {0:.4f}\n".format(accuracy_score(y_train, 
                                                                res)))
        print("Classification Report: \n {}\n".format(classification_report(y_train, 
                                                                            res)))
        print("Confusion Matrix: \n {}\n".format(confusion_matrix(y_train, 
                                                                  res)))
        print("ROC AUC: {0:.4f}\n".format(roc_auc_score(lb.transform(y_train), 
                                                      lb.transform(res))))

        res = cross_val_score(clf, X_train, y_train, cv=10, scoring='accuracy')
        print("Average Accuracy: \t {0:.4f}".format(np.mean(res)))
        print("Accuracy SD: \t\t {0:.4f}".format(np.std(res)))
        
    elif train==False:
        '''
        test performance
        '''
        res_test = clf.predict(X_test)
        print("Test Result:\n")        
        print("accuracy score: {0:.4f}\n".format(accuracy_score(y_test, 
                                                                res_test)))
        print("Classification Report: \n {}\n".format(classification_report(y_test, 
                                                                            res_test)))
        print("Confusion Matrix: \n {}\n".format(confusion_matrix(y_test, 
                                                                  res_test)))   
        print("ROC AUC: {0:.4f}\n".format(roc_auc_score(lb.transform(y_test), 
                                                      lb.transform(res_test)))

        )
                                                      
                                                      
        
        
        
# rf_clf = RandomForestClassifier(random_state=42, n_estimators=50)
# rf_clf.fit(X_train, y_train)

# print_score(rf_clf, X_train, X_test, y_train, y_test, train=True)
# print("\n******************************\n")
# print_score(rf_clf, X_train, X_test, y_train, y_test, train=False)

print(Xnew_train)
# print("unknown is hererrereerer")
rf_clf2 = RandomForestClassifier(random_state=40)#n_estimators=50 
rf_clf2.fit(Xnew_train, y_train)

print_score(rf_clf2, Xnew_train, Xnew_test, y_train, y_test, train=True)
print("\n******************************\n")
print_score(rf_clf2, Xnew_train, Xnew_test, y_train, y_test, train=False)

# myaccScr_=accuracy_score(y_test,res_test)
# confMtrx_=confusion_matrix(y_test,res_test)

pickle.dump(rf_clf2,open('obdModel.pkl','wb'))

