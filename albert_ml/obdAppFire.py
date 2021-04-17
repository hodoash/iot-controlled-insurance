#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Mar  1 17:10:47 2021

@author: leftee
"""

import numpy as np
import pandas as pd
from flask import Flask, request, jsonify, render_template
import pickle
import pyrebase
# import json


app=Flask(__name__)
model=pickle.load(open('obdModel.pkl','rb'))

firebaseConfig = {
  "apiKey": "AIzaSyBKaqMEKd5VWlZOm-mP3TYD9Tv_UlHkblg",
  "authDomain": "obd-driving-data.firebaseapp.com",
  "databaseURL": "https://obd-driving-data-default-rtdb.firebaseio.com",
  "projectId": "obd-driving-data",
  "storageBucket": "obd-driving-data.appspot.com",
  "messagingSenderId": "186160107498",
  "appId": "1:186160107498:web:a0600268693670508dec93",
  "measurementId": "G-VHNLKPM0YK"
}

firebase=pyrebase.initialize_app(firebaseConfig)
db=firebase.database()

things=db.child("readings").get()#child("4259").get()      testReadings   
# tempData=[]
# for thing in things.each():
#     tempData.
# tempData=dict(things.val());
# del tempData['traffic']
# del tempData['roadSurface']
# del tempData['VerticalAcceleration']
# del tempData['AltitudeCariation']
# print (tempData)
# print(things.val())
database=[]

for thing in things.each():
    # print(thing.val())
    database.append(thing.val())
    #database.append(list(thing.val().items()))
    # print(database)

# dataForPred=database.copy()
print(len(database))
df_=pd.DataFrame.from_dict(database)
print(len(df_))
df_.replace('',0, inplace=True) #np.nan
# df=df_.dropna()
df=df_.copy()
print(len(df))
print(df.head())

# print(df)

# styleData_=[row[14] for row in database]
# styleDataClean_=[row[1] for row in styleData_]
# safeNo=styleDataClean_.count('EvenPaceStyle')
# AggreNo=styleDataClean_.count('AggressiveStyle')
# print(safeNo)
styleData_=df['drivingStyle'].value_counts()
allStyleData=[(styleData_['EvenPaceStyle']),(styleData_['AggressiveStyle'])]
allStyleLab=[("EvenPaceStyle"),("AggressiveStyle")]
# print(styleData_)
# safeNo=styleData_.count('EvenPaceStyle')
# AggreNo=styleData_.count('AggressiveStyle')
# print(styleData_[styleData_ == "EvenPaceStyle"].value_counts())

# predData=df.drop('AltitudeVariation')
# predData=predData.drop('VerticalAcceleration')
# predData=predData.drop("roadSurface")#change to predData
# predData=predData.drop('traffic')

predData=df.copy()
predValues=predData['drivingStyle'].copy()#store predicted driving styles
# del predData["AltitudeVariation"]
# del predData["VerticalAcceleration"]
del predData["roadSurface"]
del predData["traffic"]



# predData=predData.drop('drivingStyle')
del predData["drivingStyle"]
# X_new=np.delete(X_new,[0,4,7,8,9,10,11])
del predData["AltitudeVariation"]
del predData["VehicleSpeedVariation"]#["VehicleSpeedVariation"]
del predData["LongitudinalAcceleration"]
del predData["EngineLoad"]
del predData["ManifoldAbsolutePressure"]
del predData["IntakeAirTemperature"]#predData["MassAirFlow"]
del predData["VerticalAcceleration"]

predDataTest=pd.DataFrame()
predDataTest["VehicleSpeedInstantaneous"]=predData["VehicleSpeedInstantaneous"]
predDataTest["VehicleSpeedAverage"]=predData["VehicleSpeedAverage"]
predDataTest["VehicleSpeedVariance"]=predData["VehicleSpeedVariance"]
predDataTest["EngineCoolantTemperature"]=predData["EngineCoolantTemperature"]
predDataTest["EngineRPM"]=predData["EngineRPM"]
predDataTest["MassAirFlow"]=predData["MassAirFlow"]
predDataTest["FuelConsumptionAverage"]=predData["FuelConsumptionAverage"]


print(predData.head())
print("this is predData")
# features=predData.iloc[1]
# float_row=[float(x) for x in features]
# final=[np.array(float_row)]
# print("this is the row")
# print(float_row)
# print("this is final")
# print(final)
# prediction=model.predict_proba(final)
# print("this is the prediction")
# print(prediction)

# features= [float(x) for x in predData.iloc[1]]
# #print(features)
# clean_featurs=[np.array(features)]
# predict=model.predict(clean_featurs)
    
# #pValue = round(predict[0],2)
# # pValue='{0:.{1}f}'.format(predict[0][1], 2)
# print(predict)
# print("predicted successsfulll")

modelPTest=[]
modelProba=[]
allPredictions=pd.DataFrame()

def predAllValues():
    for i in range(0,len(predDataTest)):#changed from 1   
        features=predDataTest.iloc[i]
        float_row=[float(x) for x in features]
        final=[np.array(float_row)]
        # print("this is the row")
        # print(float_row)
        # print("this is final")
        # print(final)
        prediction=model.predict_proba(final)
        # print("this is the prediction")
        print(prediction)
        # pValue='{0:.{1}f}'.format(prediction[0][1], 2)
        evenPVal=prediction[0][0]
        aggrePVal=prediction[0][1]
        # print(evenPVal)

        if evenPVal>0.5:
            modelPTest.append("EvenPaceStyle")
            modelProba.append(evenPVal)
        # # return render_template('index.html',predict_text="Aggresive mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue))
        else:
            modelPTest.append("AggessiveStyle")
            modelProba.append(aggrePVal)
            # modelPTest.append("AggressiveStyle")
    #     # return render_template('index.html',predict_text="Safe mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue))
    # print(modelPTest)
    allPredictions["predictedProba"]=modelProba
    allPredictions["predictedLabels"]=modelPTest
    allPredictions["actualLabels"]=predValues
    print(predValues)
    print("table")
    print(len(allPredictions))
    # allPredictions.replace('', np.nan, inplace=True)
    # allPredictions=allPredictions.dropna()
    
    # predAcc=model.accuracy_score(predAllValues,modelPTest)
    # print("acc of model : ")
    # print(predAcc)

def joinPredToData():
    pass


# myData=[
#     (safeNo,'EvenPaceStyle'),
#     (AggreNo,"Aggresive drivng style")
# ]
# allStyleData=[row[1] for row in styleData_]
# allStyleLab=[row[0] for row in styleData_]



def useallData():
    print(things.val())


def iterateData():
    pass
    #for thing in things.each():
        # print(thing.val)
        #dataset.append(thing.val)
        
predAllValues()
headers=tuple(allPredictions.columns.values)#("Name", "Dergerb","Predicted Style")
data=(
    tuple(allPredictions.values)
    # ("rafg","fdbvfdb","1"),
    # ("rafg","fdbvfdb","1"),
    # ("rafg","fdbvfdb","2"),
    # ("rafg","fdbvfdb","2"),
    # ("rafg","fdbvfdb","1"),
)


@app.route('/')
def home():
   
    
    # print(modelPTest)
    return render_template('index.html',headers=headers,data=data, labels=allStyleLab,values=allStyleData)

# @app.route('/predict',methods=['POST'])
predict_text=""
#background process happening without any refreshing
# @app.route('/background_process_test')
# def background_process_test():
#     # print ("Hello")
#     predict()
#     return ("nothing")

onePrediction=pd.DataFrame()
@app.route('/predict',methods=['POST','GET'])
def predict():
    #for GUI rendering
    if request.method == 'POST':
        # for key, val in request.form.items():
        #     print(key,val)
            #features=
        features=[float(x) for key,x in request.form.items()]#.form.values()
        clean_featurs=[np.array(features)]
        predict=model.predict_proba(clean_featurs)
        pValue=predict[0][1]

        if(len(predict)!=0):
            if pValue>0.5:
                predict_text="Aggresive mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue) 
            else:
                predict_text="Safe mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue)

            return jsonify({'predict_text' : predict_text})
        
        # if pValue>0.5:
        #     predict_text="Aggresive mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue)
            
        # else:
        #     predict_text="Safe mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue)

    return jsonify({'predict_error' : "error in conversion. check input"})
        # print(features)
        # onePrediction["VehicleSpeedAverage"]=request.form['VehicleSpeedAverage']
        # print(onePrediction)
        # onePrediction["LongitudinalAcceleration"]=request.form['LongitudinalAcceleration']
        # onePrediction["EngineLoad"]=request.form['EngineLoad']
        # onePrediction["VerticalAcceleration"]=request.form['VerticalAcceleration']
        # onePrediction["FuelConsumptionAverage"]=request.form['FuelConsumptionAverage']
        # onePrediction["VehicleSpeedInstantaneous"]=request.form['VehicleSpeedInstantaneous']
        # onePrediction["VehicleSpeedVariance"]=request.form['VehicleSpeedVariance']
        
    #     print(onePrediction)
        
    #     predict=model.predict_proba(onePrediction)
    #     print(predict)
    #     pValue=predict[0][1]
    #     if pValue>str(0.5):
    #         predict_text="Aggresive mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue)
    #     else:
    #         predict_text="Safe mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue)
    # return("true")
        
        # print(request.)
        #req=request.get_data()
        #print(VehicleSpeedAverage)
        #print("vehicle sped data from form")
    # features=[float(x) for x in request.data()]#.form.values()
    # clean_featurs=[np.array(features)]
    # predict=model.predict_proba(clean_featurs)
    
    # #pValue = round(predict[0],2)
    # pValue=predict[0][1]
    
    # if pValue>str(0.5):
    #     predict_text="Aggresive mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue)
    # else:
    #    predict_text="Safe mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue)


if __name__ == '__main__':
    app.run()