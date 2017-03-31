package com.picturecat.elleray.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity implements View.OnClickListener {
    String src = "lxt.json";
    Button btn ,leftBtn, rightBtn, saveBtn;
    ImageView imageView;
    TextView mPathText;
    private ProgressBar mProgress;

    private ArrayList<String> mList ;
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        leftBtn  = (Button) findViewById(R.id.btn1);
        leftBtn.setOnClickListener(this);
        leftBtn.setVisibility(View.GONE);
        rightBtn  = (Button) findViewById(R.id.btn2);
        rightBtn.setOnClickListener(this);
        rightBtn.setVisibility(View.GONE);
        imageView = (ImageView) findViewById(R.id.image);
        mPathText = (TextView) findViewById(R.id.path);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        saveBtn = (Button) findViewById(R.id.btn3);
        saveBtn.setOnClickListener(this);
        saveBtn.setVisibility(View.GONE);

//        initData();

        StorageManager.init(this);
    }

    private void initData(){
        decode(src);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                decode(src);
                break;
            case R.id.btn1:
                mIndex  = getLeftIndex(mList, mIndex);
                showImage(imageView, mList, mIndex);
                break;
            case R.id.btn2:
                mIndex = getRightIndex(mList, mIndex);
                showImage(imageView, mList, mIndex);
                break;
            case R.id.btn3:
                saveAllPictures(mList);
                break;
        }
    }

    private void decode(final String str){
        mProgress.setVisibility(View.VISIBLE);

        rx.Observable.create(new rx.Observable.OnSubscribe<ArrayList<String>>() {
            @Override
            public void call(Subscriber<? super ArrayList<String>> subscriber) {
                ArrayList<String> list = deJson(str);
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<String>>() {
                    @Override
                    public void onCompleted() {
                        mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ArrayList<String> list) {
                        save(list);
                        mList = getSwitch(list);
                        mIndex = 0;

                        showImage(imageView, mList, mIndex);
                        leftBtn.setVisibility(View.VISIBLE);
                        rightBtn.setVisibility(View.VISIBLE);
                        saveBtn.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void showImage(ImageView imageView, ArrayList<String> list, int index){
        String path = list.get(index);
        Glide.with(MainActivity.this)
                .load(path)
                .fitCenter()
                .error(R.drawable.default_video)
                .into(imageView);

        mPathText.setText( "("+(index+1)+ "/ " + list.size() +")"  + path);

        onDownLoad(path);
    }

    private ArrayList<String> deJson(String str){


        ArrayList<String> res = new ArrayList<String>();
        try{
            InputStreamReader isr = new InputStreamReader(getAssets().open(str),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            br.close();
            isr.close();

            JSONArray js = new JSONArray(builder.toString());
            for (int i = 0; i < js.length();  i++){
                JSONObject jsonObjectSon= (JSONObject)js.opt(i);
               String _sourceStr =  jsonObjectSon.getString("_source");
                JSONObject _sourceJson = new JSONObject(_sourceStr);
                String layersStr =  _sourceJson.getString("layers");
                JSONObject layersJson = new JSONObject(layersStr);
                String httpStr =  layersJson.getString("http");
                JSONObject httpJson = new JSONObject(httpStr);
                String http_request_full_uriStr =  httpJson.getString("http.request.full_uri");
                Log.d("image_url", http_request_full_uriStr);

                res.add(http_request_full_uriStr);
            }

        }catch (Exception e){
            Log.e("error",e.toString());
        }

        return res;
    }

    private void save(ArrayList<String> str){
        String filePath = null;
        filePath = DirectoryConstants.HOME_ROOT +  src + ".txt";
//        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//        if (hasSDCard) { // SD卡根目录的hello.text
//            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + src + ".txt";
//        } else  // 系统下载缓存根目录的hello.text
//            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + src + ".txt";

            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                    file.createNewFile();
                }
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.toArray().toString().getBytes());
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        Toast.makeText(this , "已经保存为" + filePath, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> getSwitch(ArrayList<String> src){
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < src.size(); i++){
            String tmp = src.get(i);
            int index = tmp.lastIndexOf("thum");
            if(index > 0){
                tmp = tmp.substring(0, index);
            }
            int index2 = tmp.lastIndexOf("w/197");
            if(index2 > 0){
                tmp = tmp.substring(0, index2);
            }

            int index3 = tmp.lastIndexOf("w/118");
            if(index3 > 0){
                tmp = tmp.substring(0, index3);
            }

            res.add(tmp);
        }

        Collections.sort(res, new FileComparator());
        ArrayList<String> tmp = new ArrayList<String>();
        String recent  = res.get(0);
        tmp.add(recent);

        for (int i = 1; i < res.size(); i ++){
            if(!res.get(i).equals(recent)){
                recent = res.get(i);
                tmp.add(res.get(i));
            }
        }
        return tmp;
    }

    private int getLeftIndex(ArrayList<String> list, int index){
        int res = index - 1;
        if(res < 0){
            res = list.size() - 1;
        }

        return res;
    }

    private int getRightIndex(ArrayList<String > list, int index){
        int res = index + 1;
        if(res > list.size() - 1){
            res = list.size() - 1;
        }

        return res;
    }

    /**
     * 启动图片下载线程
     */
    private void onDownLoad(final String url) {
        DownLoadImageService service = new DownLoadImageService(getApplicationContext(),
                url,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(File file) {
                    }
                    @Override
                    public void onDownLoadSuccess(Bitmap bitmap) {
                        // 在这里执行图片保存方法
//                        Message message = new Message();
//                        message.what = MSG_VISIBLE;
//                        handler.sendMessageDelayed(message, delayTime);

                        Log.d("image", "save image successful, path = " + url);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(MainActivity.this, "save image : " + url+ "  successful!, (" + (mList.indexOf(url)+1) + "/" +mList.size() + ")", Toast.LENGTH_SHORT).show();
                                mPathText.setText(url +  " --> save successful., (" + (mList.indexOf(url)+1) + "/" +mList.size() + ")");
                            }
                        });

                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
//                        Message message = new Message();
//                        message.what = MSG_ERROR;
//                        handler.sendMessageDelayed(message, delayTime);
                    }
                });
        //启动图片下载线程
        new Thread(service).start();
    }

    public static class FileComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
                return compareFileName(o1, o2);
        }
    }

    public static int compareFileName(String filename, String compareName) {
        if (TextUtils.isEmpty(filename)) {
            return -1;
        } else if (TextUtils.isEmpty(compareName)) {
            return 1;
        }
        return filename.compareTo(compareName);
    }

    private void saveAllPictures(final ArrayList<String> pics){
    mProgress.setVisibility(View.VISIBLE);
        rx.Observable.create(new rx.Observable.OnSubscribe<ArrayList<String>>() {
            @Override
            public void call(Subscriber<? super ArrayList<String>> subscriber) {
                for (int i = 0; i < pics.size(); i ++){
                    onDownLoad(pics.get(i));
                }
                subscriber.onNext(pics);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<String>>() {
                    @Override
                    public void onCompleted() {
                        mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ArrayList<String> list) {
                    }
                });
    }


}
