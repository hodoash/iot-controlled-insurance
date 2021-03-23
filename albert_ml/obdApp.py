#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Mar  1 17:10:47 2021

@author: leftee
"""

import numpy as np
from flask import Flask, request, jsonify, render_template
import pickle


app=Flask(__name__)
model=pickle.load(open('obdModel.pkl','rb'))

headers=("Name", "Dergerb","Predicted Style")
data=(
    ("rafg","fdbvfdb","dfvdfb"),
    ("rafg","fdbvfdb","dfvdfb"),
    ("rafg","fdbvfdb","dfvdfb"),
    ("rafg","fdbvfdb","dfvdfb"),
    ("rafg","fdbvfdb","dfvdfb"),
)

@app.route('/')
def home():
    return render_template('index.html',headers=headers,data=data)

@app.route('/predict',methods=['POST'])
def predict():
    #for GUI rendering
    features=[float(x) for x in request.form.values()]
    clean_featurs=[np.array(features)]
    predict=model.predict(clean_featurs)
    
    #pValue = round(predict[0],2)
    pValue='{0:.{1}f}'.format(predict[0][1], 2)
    
    if pValue>str(0.5):
        return render_template('index.html',predict_text="Aggresive mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue))
    else:
        return render_template('index.html',predict_text="Safe mode of driving.\n Probability of aggresive(unsafe) driving is{}".format(pValue))

#  <!-- <section class="tableSec">
     

#     </section> -->

if __name__ == '__main__':
    app.run()