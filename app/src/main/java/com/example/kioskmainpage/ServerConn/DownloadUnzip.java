package com.example.kioskmainpage.ServerConn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.kioskmainpage.Myapplication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadUnzip {
    private static final String TAG = "downloadUnzip";

    String FileName ;
    //TODO: 실제 서버로 변경
    String serverURL = "http://mobilekiosk.co.kr/admin/api/start.php?mb_id="; //"http://mobilekiosk.co.kr/api_file/tmp"; //TODO: 트레픽 초과 임시 서버 : http://192.168.0.117:8080/test/ 정식 서버 : http://mobilekiosk.co.kr/api_file/tmp
    String savePath;
    String dst;

    String bizNum="";

    public DownloadUnzip(String savePath, String bizNum) {
        this.savePath = savePath;//저장 경로 지정
        this.bizNum = bizNum;
        FileName = bizNum + ".zip";
        //       getFileNamesFromServer(isBestNew);//server에서 다운 받을 파일이름들 불러옴
    }


    //웹 서버(php파일에서)에서 받아올 파일이름(=카테고리명) 읽어옴
    //TODO: 서버 만들어지면 읽어 오는 함수를 전부 바꿔야 합니다.
  /*  private void getFileNamesFromServer(final boolean isBestNew) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String filename_path;
                if(!isBestNew) //메인 메뉴일경우(
                    filename_path = "/getCategories.php";
                else//베스트 메뉴일경우
                    filename_path = "/getBestNew.php";
                try {
                    URL url = new URL(serverURL + filename_path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setConnectTimeout(1000);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                    String temp = null;
                    while ((temp = br.readLine()) != null) {
                        FileNames.add(temp);
                        Log.d(TAG, "filenames : " + temp);
                    }
                    is.close();
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    Log.d(TAG, "URL Malformed error");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d(TAG, "IO Exception error");
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    //다운 파일 이름들(=카테고리명) 리턴
 /*   public ArrayList<String> getFileNames() {
        return FileNames;
    }*/


    //서버(테스트용)에서 전체 다운로드하고 unpackZip()실행하는 부분(두개의 기능 1.다운로드(downloadZipFile) 2.다운로드 파일 압축 해제(unpackZip)
    //TODO: 서버만들어지면 수정
    public void doDownUnzip() {
        dst = savePath +"";//TODO:임시 서버 사용시엔 뒤에 /test붙이기

        Thread thread = new Thread() {
            @Override
            public void run() {
                File f = new File(dst);
                if (!f.exists()) {
                    f.mkdirs();
                }
                downloadZipFile(serverURL, dst);//zip파일들 다운로드
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String categoryFolder = FileName.substring(0, FileName.lastIndexOf('.'));

        //저장되어있는 경로에서 file을 하나씩 읽어서 압축해제, 압축해제할 폴더는 저장되어있는 경로의 parent+ "/"+categoryFolder
        if (unpackZip(dst + "/" + FileName, "/")) {
            Log.d(TAG, "unpack success : " + "From : " + dst + "/" + FileName + " To : " + "/" + categoryFolder);
        }
        else {
            Log.d(TAG, "unpack failed : " + "From : " + dst + "/" + FileName + " To : " + "/" + categoryFolder);
        }
                // 2중압축해제를 방지 위한 주석 기존 zip파일안의 zip 형식 제거
            /*
            //아까전에 압축해제할 폴더에 이미지 zip파일을 다시 압축해제.
            if (unpackZip(dst + "/" + categoryFolder + "/menu_img.zip", "/images")) {
                Log.d(TAG, "image unpack success");
            } else {
                Log.d(TAG, "image unpack failed");
            }
            */


          //  downLoadQRImage();

    }


    //zip파일 다운로드
    public void downloadZipFile(String urlStr, String destinationFolder) {

            InputStream is = null;
            OutputStream os = null;
            HttpURLConnection conn = null;
            String destinationFilePath = destinationFolder + "/" +FileName ;
            Log.d(TAG, "start download : From : " + urlStr + "/"  + " To : " + destinationFilePath);//TODO:임시서버사용시엔 "start download : From : " + urlStr + "/test/" + FileNames.get(i) + " To : " + destinationFilePath
            try {
                URL url = new URL(urlStr + bizNum);//TODO:임시서버사용시엔 urlStr + "/test/" + FileNames.get(i)
                conn = (HttpURLConnection) url.openConnection();

                is = conn.getInputStream();

                new File(destinationFilePath).createNewFile();
                Log.d(TAG, "destination : " + destinationFilePath);
                os = new FileOutputStream(destinationFilePath);

                byte data[] = new byte[4096];
                int count;

                while ((count = is.read(data)) != -1) {
                    os.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "download error");
                return;
            } finally {
                try {
                    if (os != null) os.close();
                    if (is != null) is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (conn != null) conn.disconnect();
            }
            Log.d(TAG, "download finish : " + urlStr + "/" + FileName);//TODO:임시서버사용시엔 "download finish : " + urlStr + "/test/" + FileNames.get(i)
    }

    //압축 해제하는 모듈
    //filePath는 압축을 해제할 파일의 절대경로를 필요로하고 unpackDst는 그 파일의 부모 directory 이후 더해줄 경로를 말합니다.
    //예를 들어 unpackDst가 null이라면 filePath의 부모 directory에 그대로 압축이 해제되니까 zip파일과 같은 곳에 해제가 됩니다.
    public boolean unpackZip(String filePath, String unpackDst) {

        InputStream is;
        ZipInputStream zis;
        try {
            Log.d(TAG, "unpack start : " + filePath);
            File zipfile = new File(filePath);

            String parentFolder = zipfile.getParentFile().getPath();
            String unpackFolder = parentFolder + unpackDst;

            File unpackDir = new File(unpackFolder);
            if (!unpackDir.exists()) {
                unpackDir.mkdirs();
            }

            String filename;

            Log.d(TAG, "parent folder : " + parentFolder);
            Log.d(TAG, "unpack folder : " + unpackFolder);

            is = new FileInputStream(filePath);

            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[4096];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                Log.d(TAG, "entry file name : " + filename);

                if (ze.isDirectory()) {//디렉토리인 경우 디렉토리(폴더)생성 후 스킵
                    File fmd = new File(unpackFolder + "/" + filename);
                    fmd.mkdirs();
                    continue;
                }

                //파일일 경우 엔트리 단위로 압축 해제
                FileOutputStream fout = new FileOutputStream(unpackFolder + "/" + filename);
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //테스트용 메소드
 /*   public void searchFiles(String path) {

        File f = new File(path);
        File files[] = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            Log.d(TAG, "file in : " + path + " : " + files[i].getName());
        }
    }*/

    //서버에서 qr이미지를 읽어와서 저장합니다.
 /*   public void downLoadQRImage() {
        final Thread thread = new Thread() {
            HttpURLConnection conn = null;
            String destination = savePath + "/qrImage.png";//TODO:임시서버사용시엔 /qrImage/qrImage.png  붙이기
            String folder = savePath +"";//TODO:임시서버사용시엔 뒤에/qrImage  붙이기
            @Override
            public void run() {
                try {
                    File folder_f = new File(folder);
                    if(!folder_f.exists()) folder_f.mkdirs();
                    File file = new File(destination);
                    Log.d(TAG, "dst path : " + destination);
                    URL url = new URL(serverURL + "/QRcode.png");//TODO:임시서버사용시엔 /qr/QRcode.png  붙이기
                    Log.d(TAG, "qr target dst : " + serverURL + "/QRcode.png");//TODO:임시서버사용시엔 /qr/QRcode.png  붙이기
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    Log.d(TAG, "qr dst path : " + destination);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, byteArrayOutputStream);

                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    fileOutputStream.write(byteArray);
                    is.close();
                    byteArrayOutputStream.close();
                    fileOutputStream.close();
                    //}
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

}
