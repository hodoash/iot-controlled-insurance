package com.example.carobdnew;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Frag1 extends Fragment {
    ToggleButton toggleFirst;
    String firstData = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.frag1_layout,container,false);
        toggleFirst = (ToggleButton) view.findViewById(R.id.toggleButton);

        toggleFirst.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (toggleFirst.isChecked()) {
                    //set the disable command here
                    firstData = "firstDataOff";
                }else{
                    //set teh enabled cmd
                    firstData = "firstDataOn";
                }
                //send data
                ViewDataScreen viewDataScreen = new ViewDataScreen();
                viewDataScreen.callWiteFun(firstData);
            }
        });
        return view;
    }
}
