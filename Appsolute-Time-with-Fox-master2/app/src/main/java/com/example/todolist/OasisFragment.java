package com.example.todolist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.todolist.databinding.FragmentOasisBinding;
import com.example.todolist.databinding.FragmentTodoBinding;

// TODO: 오아시스(놀이방)관련 코드

public class OasisFragment extends Fragment {
    ImageButton fNormal; ImageButton fSwim; ImageButton fSleep; ImageButton fRed; ImageButton fPilot;
    ImageButton fAngry;

    FragmentOasisBinding binding;

    int imageResources[] = {
            R.drawable.fox_swim, R.drawable.fox, R.drawable.fox_sleep, R.drawable.fox_red, R.drawable.fox_pilot,
            R.drawable.fox_angry,
    };

    Boolean[] farr = new Boolean[imageResources.length];
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentOasisBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fNormal =view.findViewById(R.id.fNormal); fSwim =view.findViewById(R.id.fSwim);
        fSleep =view.findViewById(R.id.fSleep); fRed =view.findViewById(R.id.fRed);
        fPilot =view.findViewById(R.id.fPilot); fAngry =view.findViewById(R.id.fAngry);

        farr = ((MainActivity)MainActivity.mcontext).getFoxLog();
        for(int i=0; i<imageResources.length; i++) {
            if (farr[i] == true) {

                if (i == 0) fSwim.setVisibility(View.VISIBLE);
                if (i == 1) fNormal.setVisibility(View.VISIBLE);
                if (i == 2) fSleep.setVisibility(View.VISIBLE);
                if (i == 3) fRed.setVisibility(View.VISIBLE);
                if (i == 4) fPilot.setVisibility(View.VISIBLE);
                if (i == 5) fAngry.setVisibility(View.VISIBLE);
            }
        }
        float oasisNum = (float)1332/(float)986;
        float oasisNum2 = (float)986/(float)1332;
        float X = ((MainActivity)MainActivity.mcontext).pointX;
        float Y = ((MainActivity)MainActivity.mcontext).pointY;
        float ground_width; float ground_height;
        ImageView image = view.findViewById(R.id.oasis_ground);

        if(Y/X < oasisNum) {
            ground_width  = Y*oasisNum2;
        }
        else ground_height = X*oasisNum;




    }

}