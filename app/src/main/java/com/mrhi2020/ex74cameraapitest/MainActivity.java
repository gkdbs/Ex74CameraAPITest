package com.mrhi2020.ex74cameraapitest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    CameraView cv;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cv= findViewById(R.id.cv);
        iv= findViewById(R.id.iv);

        //동적퍼미션 (버전 영향없이 호환성버전으로 퍼미션 체크하기)
        String[] permissions= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int checkResult= ActivityCompat.checkSelfPermission(this, permissions[0]);
        if(checkResult== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, 0);
        }

    }

    public void clickCapture(View view) {
        cv.camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //캡쳐한 사진 데이터를 byte[]로 전달해줌.
                //이미지뷰에 보여주려면 Bitmap객체로 생성 해야만 함.
                Bitmap bm= BitmapFactory.decodeByteArray(data, 0, data.length);
                iv.setImageBitmap(bm);

                //핸드폰에 저장되게 하려면 외부저장소에 직접 byte[] data 를 .jpg로 저장[파일출력]

                File path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //파일명을 날짜로
                SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmss");
                String fileName= sdf.format(new Date()) + ".jpg";
                File file= new File(path, fileName);

                //만들어진 file경로에 byte[]데이터 출력하기 - 파일저장하기
                try {
                    FileOutputStream fos= new FileOutputStream(file);
                    fos.write(data);
                    fos.flush();
                    fos.close();

                    Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_SHORT).show();

                    //파일로 저장을 되지만 갤러리앱이나 사진앱에서 곧바로 인식하지 못함.
                    //갤러리나 사진앱에서 내가 저장한 파일을 스캔하도록!!!
                    Intent intent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.parse("file://"+file.getPath()));
                    sendBroadcast(intent);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //캡쳐하면 미리보기가 멈추므로 다시 실행되도록..
                cv.camera.startPreview();

            }
        });
    }
}