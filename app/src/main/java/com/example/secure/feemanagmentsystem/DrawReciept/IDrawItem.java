package com.example.secure.feemanagmentsystem.DrawReciept;

import android.graphics.Canvas;

/**
 * Created by daniel on 12/08/2016.
 */
public interface IDrawItem {
    void drawOnCanvas(Canvas canvas, float x, float y);

    int getHeight();
}
