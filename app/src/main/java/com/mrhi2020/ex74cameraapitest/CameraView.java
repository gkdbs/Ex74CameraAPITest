package com.mrhi2020.ex74cameraapitest;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;

//SurfaceView : 고속버퍼 뷰 - 고속으로 화면을 그려주는 뷰
public class CameraView extends SurfaceView {

    SurfaceHolder holder; //서피스뷰를 화면에 보여주는 객체
    //카메라 H/W를 제어하는 객체
    Camera camera;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        holder= getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                //카메라 하드웨어를 제어하는 객체 얻어오기
                camera= Camera.open(0); //카메라 조리개 열기 [0:back, 1:front]

                //카메라객체에게 미리보기 설정
                try {
                    camera.setPreviewDisplay(holder);

                    //원래 카메라는 가로찍기만 가능함..
                    //프리뷰 90도 회전
                    camera.setDisplayOrientation(90);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                //미리보기 시작!!
                camera.startPreview();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                //카메라 미리보기 종료
                camera.stopPreview();

                //카메라 닫기
                camera.release();
                camera=null;
            }
        });
    }
}
