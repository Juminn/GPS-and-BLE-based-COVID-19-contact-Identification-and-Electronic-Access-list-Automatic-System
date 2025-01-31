package com.example.covidcontackmanagement_mix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity {

    //
    private Button locationButton;
    private Button button2;
    private Button hostButton, userButton;
    private ImageButton imageButton;
    private Switch storageLocationSwitch;
    private Switch bluetoothSwitch;
    private TextView textView;
    private EditText editTextView;
    private String test2;
    private String access_token=null;
    private String refresh_token=null;
    private String userID;
    private int hostOrUser=-1;
    private boolean globalLocationChecked=false;
    private boolean globalBluetoothChecked=false;
    private boolean globalAdvertiserChecked=false;
    private String hostLocation=null;
    //
    private int gps_num = 300;
    private double[] longtitudeSet = new double[gps_num];
    private double[] latitudeSet = new double[gps_num];
    private double latitudeMedian;
    private double longtitudeMedian;
    private String intime;
    private String outtime;
    private String mugunghwa;
    private String gpsString;
    private String beforeLocation = "";
    private int sequence = 0;
    private boolean isMove = true;
    private OkHttpClient client=new OkHttpClient();

    private BluetoothAdapter mBluetoothAdapter;
    private Intent bltScanService;
    private Intent locationStorageService;
    private AdvertiserFragment advertiserFragment=null ;//= new AdvertiserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button2 = (Button) findViewById(R.id.button2);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        hostButton = (Button) findViewById(R.id.hostButton);
        userButton = (Button) findViewById(R.id.userButton);
        textView = (TextView) findViewById(R.id.titleTextView);
        editTextView = (EditText) findViewById(R.id.editTextView);
        locationButton = (Button) findViewById(R.id.locationButton);

        Requests request = new Requests("http://115.21.52.248:8080/location/GPS");

        storageLocationSwitch = (Switch)findViewById(R.id.storageLocationSwitch);
        bluetoothSwitch = (Switch)findViewById(R.id.bluetoothSwitch);





        //MediaPlayer player = MediaPlayer.create(this, R.raw.beep);
        //player.start();










        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);





    }
    //로그인 요청
    public String confirmAT(){
        System.out.println("test10 : ");
        OkHttpClient client = new OkHttpClient();
        try{
            String url="";
            if(hostOrUser==0){
                 url="http://115.21.52.248:8080/host/access?access_token="+access_token;
            }else if(hostOrUser==1){
                 url="http://115.21.52.248:8080/kakao/access?access_token="+access_token;
            }
            Request.Builder builder=new Request.Builder().url(url).get();
            Request request= builder.build();
            Response response= client.newCall(request).execute();

            if(response.body().string().equals("200")){
                ResponseBody body=response.body();
                if(body!=null){
                    System.out.println("body!=null");
                }else{
                    System.out.println("정보없음!");
                }
                return "";
            }
            else{
                if(hostOrUser==0){
                    url="http://115.21.52.248:8080/host/refresh?refresh_token="+refresh_token;
                }else if(hostOrUser==1) {
                    url = "http://115.21.52.248:8080/kakao/refresh?refresh_token=" + refresh_token;
                }
                builder=new Request.Builder().url(url).get();
                request=builder.build();
                response=client.newCall(request).execute();
                if(response.body().string().equals("200"))
                    System.out.println("response Success");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        //access_token=null;
        return "";
    }
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();


            /*
            System.out.println(provider  + "   " + latitude + "   " + longitude);

            longtitudeSet[sequence] = longitude;
            latitudeSet[sequence] = latitude;


            if(sequence == gps_num-1){

                float[] roundedLongatitudeSet = new float[gps_num];
                float[] roundedlatitudeSet = new float[gps_num];
                int i = 0;
                for(i = 0; i<gps_num; i++ ){
                    roundedLongatitudeSet[i] =  Float.parseFloat(String.format("%.4f", longtitudeSet[i]));
                    roundedlatitudeSet[i] =  Float.parseFloat(String.format("%.4f", latitudeSet[i]));
                }
                float latitudeModeRatio = modeRatio(roundedlatitudeSet);
                float longtitudeModeRatio = modeRatio(roundedLongatitudeSet);

                if( latitudeModeRatio < 0.5 && longtitudeModeRatio < 0.5){
                    isMove = true;
                }
                else{
                    isMove = false;
                    double latitudeMedian = getMedian(latitudeSet);
                    double longtitudeMedian = getMedian(longtitudeSet);

                    System.out.println("위도: " + latitudeMedian + "\n" + longtitudeMedian);
                }

                sequence = 0;
            }
            else{
                sequence++;
            }

             */
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
    // 소수의 최빈값의 비율을 리턴
    public int modeRatio(float[] arr) {

        float[] mode = new float[gps_num];

        float[] value = new float[gps_num];
        int[] valueCount = new int[gps_num];
        int valueVariation = 0;
        boolean valueExist = false;

        for(int i = 0; i<arr.length; i++){
            for(int j = 0; j<valueVariation; j++) {
                if (arr[i] == value[j]) {
                    valueCount[j]++;
                    valueExist = true;
                }
            }
            if(valueExist == false){
                value[valueVariation] = arr[i];
                valueCount[valueVariation]++;
                valueVariation++;
            }
            else valueExist = false;

        }

        float modeNum = 0; // 최빈수
        int modeCnt = 0; // 최빈수가 출현한 횟수

        for(int i=0; i<valueVariation; i++) {
            if (modeCnt < valueCount[i]) {
                modeCnt = valueCount[i];
                modeNum = value[i];
            }
        }
        System.out.println("최빈수 : " + modeNum + "    cnt : " + modeCnt);

        return modeCnt;
    }
    //소수의 중앙값 리턴
    private static double getMedian(double[] arrayDouble) {

        // TODO Auto-generated method stub


        Arrays.sort(arrayDouble);//오름차순 정렬


        int size = arrayDouble.length;

        double result;

        if (size % 2 == 0) { //배열크기가 짝수일경우

            int m = size / 2;

            int n = size / 2 - 1;

            result = (float) (arrayDouble[m] + arrayDouble[n]) / 2; //중앙값 2개의  평균

        } else { //배열크기가 홀수인경우

            int m = size / 2;

            result = arrayDouble[m]; //중앙값

        }
        //System.out.print(result);
        return result;
    }


    //gps도분초로 바꾸기
    private List<String> gpsToDMS(double latit, double longit){
        Double [][] DMS =new Double[10][6];
        String [] DMSS=new String[9];
        boolean xpm=true;
        boolean ypm=true;
        double er=0.1;
        int XD; int XM; double XS; int YD; int YM; double YS;
        String gps="";
        XD=(int)latit;
        if(XD<0) {
            xpm=false;
        }else{
            xpm=true;
        }
        XD=Math.abs(XD);

        latit=Math.abs(latit);
        XM=(int)(((latit)-(double)XD)*60);
        XS=((latit-XD)*60-(double)XM)*60;
        XS=((double)Math.round(XS*10)/10);
        YD=(int)longit;

        if(YD<0) {
            ypm=false;
        }else{
            ypm=true;
        }
        YD=Math.abs(YD);
        longit=Math.abs(longit);
        YM=(int)(((longit)-YD)*60);
        YS=((longit-YD)*60-YM)*60;
        YS=((double)Math.round(YS*10)/10);
        if(XS-er<0){
            if(XM-1<0){
                DMS[7][0]=(double)XD-1;
                DMS[8][0]=(double)XD-1;
                DMS[9][0]=(double)XD-1;
                DMS[7][1]=(double)(60+(XM-1));
                DMS[8][1]=(double)(60+(XM-1));
                DMS[9][1]=(double)(60+(XM-1));
                DMS[7][2]=(60+(XS-er));
                DMS[8][2]=(60+(XS-er));
                DMS[9][2]=(60+(XS-er));
            }else{
                DMS[7][0]=(double)XD;
                DMS[8][0]=(double)XD;
                DMS[9][0]=(double)XD;
                DMS[7][1]=(double)((XM-1));
                DMS[8][1]=(double)((XM-1));
                DMS[9][1]=(double)((XM-1));
                DMS[7][2]=(60+(XS-er));
                DMS[8][2]=(60+(XS-er));
                DMS[9][2]=(60+(XS-er));
            }
        }else{
            DMS[7][0]=(double)XD;
            DMS[8][0]=(double)XD;
            DMS[9][0]=(double)XD;
            DMS[7][1]=(double)((XM));
            DMS[8][1]=(double)((XM));
            DMS[9][1]=(double)((XM));
            DMS[7][2]=((XS-er));
            DMS[8][2]=((XS-er));
            DMS[9][2]=((XS-er));
        }
        if(XS+er>60){
            if(XM+1>60){
                DMS[1][0]=(double)XD+1;
                DMS[2][0]=(double)XD+1;
                DMS[3][0]=(double)XD+1;
                DMS[1][1]=(double)((XM+1)-60);
                DMS[2][1]=(double)((XM+1)-60);
                DMS[3][1]=(double)((XM+1)-60);
                DMS[1][2]=((XS+er)-60);
                DMS[2][2]=((XS+er)-60);
                DMS[3][2]=((XS+er)-60);
            }else{
                DMS[1][0]=(double)XD;
                DMS[2][0]=(double)XD;
                DMS[3][0]=(double)XD;
                DMS[1][1]=(double)((XM+1));
                DMS[2][1]=(double)((XM+1));
                DMS[3][1]=(double)((XM+1));
                DMS[1][2]=((XS+er)-60);
                DMS[2][2]=((XS+er)-60);
                DMS[3][2]=((XS+er)-60);
            }
        }else{
            DMS[1][0]=(double)XD;
            DMS[2][0]=(double)XD;
            DMS[3][0]=(double)XD;
            DMS[1][1]=(double)((XM));
            DMS[2][1]=(double)((XM));
            DMS[3][1]=(double)((XM));
            DMS[1][2]=((XS+er));
            DMS[2][2]=((XS+er));
            DMS[3][2]=((XS+er));
        }
        if(YS-er<0){
            if(YM-1<0){
                DMS[1][3]=(double)YD-1;
                DMS[4][3]=(double)YD-1;
                DMS[7][3]=(double)YD-1;
                DMS[1][4]=(double)(60+(YM-1));
                DMS[4][4]=(double)(60+(YM-1));
                DMS[7][4]=(double)(60+(YM-1));
                DMS[1][5]=(60+(YS-er));
                DMS[4][5]=(60+(YS-er));
                DMS[7][5]=(60+(YS-er));
            }else{
                DMS[1][3]=(double)YD;
                DMS[4][3]=(double)YD;
                DMS[7][3]=(double)YD;
                DMS[1][4]=(double)((YM-1));
                DMS[4][4]=(double)((YM-1));
                DMS[7][4]=(double)((YM-1));
                DMS[1][5]=(60+(YS-er));
                DMS[4][5]=(60+(YS-er));
                DMS[7][5]=(60+(YS-er));
            }
        }else{
            DMS[1][3]=(double)YD;
            DMS[4][3]=(double)YD;
            DMS[7][3]=(double)YD;
            DMS[1][4]=(double)((YM));
            DMS[4][4]=(double)((YM));
            DMS[7][4]=(double)((YM));
            DMS[1][5]=((YS-er));
            DMS[4][5]=((YS-er));
            DMS[7][5]=((YS-er));
        }
        if(YS+er>60){
            if(YM+1>60){
                DMS[3][3]=(double)YD+1;
                DMS[6][3]=(double)YD+1;
                DMS[9][3]=(double)YD+1;
                DMS[3][4]=(double)((YM+1)-60);
                DMS[6][4]=(double)((YM+1)-60);
                DMS[9][4]=(double)((YM+1)-60);
                DMS[3][5]=((YS+er)-60);
                DMS[6][5]=((YS+er)-60);
                DMS[9][5]=((YS+er)-60);
            }else{
                DMS[3][3]=(double)YD;
                DMS[6][3]=(double)YD;
                DMS[9][3]=(double)YD;
                DMS[3][4]=(double)((YM+1));
                DMS[6][4]=(double)((YM+1));
                DMS[9][4]=(double)((YM+1));
                DMS[3][5]=((YS-er)-60);
                DMS[6][5]=((YS-er)-60);
                DMS[9][5]=((YS-er)-60);
            }
        }else{
            DMS[3][3]=(double)YD;
            DMS[6][3]=(double)YD;
            DMS[9][3]=(double)YD;
            DMS[3][4]=(double)((YM));
            DMS[6][4]=(double)((YM));
            DMS[9][4]=(double)((YM));
            DMS[3][5]=((YS+er));
            DMS[6][5]=((YS+er));
            DMS[9][5]=((YS+er));
        }
        DMS[4][0]=(double)XD;
        DMS[4][1]=(double)XM;
        DMS[4][2]=(double)XS;
        DMS[6][0]=(double)XD;
        DMS[6][1]=(double)XM;
        DMS[6][2]=(double)XS;

        DMS[2][3]=(double)YD;
        DMS[2][4]=(double)YM;
        DMS[2][5]=(double)YS;
        DMS[8][3]=(double)YD;
        DMS[8][4]=(double)YM;
        DMS[8][5]=(double)YS;

        DMS[5][0]=(double)XD;
        DMS[5][1]=(double)XM;
        DMS[5][2]=XS;
        DMS[5][3]=(double)YD;
        DMS[5][4]=(double)YM;
        DMS[5][5]=YS;
        for(int i=1;i<=9;i++){
            DMSS[i-1]="";
            DMSS[i-1]+=String.valueOf((int)Math.round(DMS[i][0]));
            DMSS[i-1]+="°";
            DMSS[i-1]+=String.valueOf((int)Math.round(DMS[i][1]));
            DMSS[i-1]+="\'";
            DMSS[i-1]+=String.valueOf(((double)Math.round(DMS[i][2]*100)/100));
            DMSS[i-1]+="\"";
            if(xpm){
                DMSS[i-1]+="N ";
            }else{
                DMSS[i-1]+="S ";
            }
            DMSS[i-1]+=String.valueOf((int)Math.round(DMS[i][3]));
            DMSS[i-1]+="°";
            DMSS[i-1]+=String.valueOf((int)Math.round(DMS[i][4]));
            DMSS[i-1]+="\'";
            DMSS[i-1]+=String.valueOf(((double)Math.round(DMS[i][5]*100)/100));
            DMSS[i-1]+="\"";
            if(ypm){
                DMSS[i-1]+="E";
            }else{
                DMSS[i-1]+="W";
            }
            //System.out.println(DMSS[i-1]);
        }
        return new ArrayList<String>(Arrays.asList(DMSS));
    }

    //gps를 무궁화코드로 변환
    private String getGpsMgh(String gps){
        try{
            String url="http://115.21.52.248:3000/location?gps="+gps;
            Request.Builder builder=new Request.Builder().url(url).get();
            Request request= builder.build();
            Response response= client.newCall(request).execute();

            if(response.isSuccessful()){
                ResponseBody body=response.body();
                if(body!=null){
                    //System.out.println(body.string());
                    return body.string();
                }else{
                    System.out.println("Error");
                }
            }
            return "";
        }catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private String dmsArrTomugunghwas(List<String> dmsArr){
        String mugunghwas = getGpsMgh(dmsArr.get(0));
        for(int i = 1; i < 9; i++){
            mugunghwas += "/" + getGpsMgh(dmsArr.get(i));
        }
        System.out.println("무궁화s: "+ mugunghwas);

        return mugunghwas;
    }


    @Override
    protected void onPause(){
        super.onPause();
        saveState();

    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("userid2: " + userID);

        restoreState();
        if(advertiserFragment==null) {
            System.out.println("ad null");
            advertiserFragment = new AdvertiserFragment();
        }
        if(access_token!=null&&hostOrUser==0)
            advertiserFragment.inputUserID(userID);
        //껏다켯을때 화면
        if(hostOrUser==0) {
            System.out.println("case100 : "+hostOrUser);
            imageButton.setVisibility(View.GONE);
            button2.setVisibility(View.VISIBLE);
            storageLocationSwitch.setVisibility(View.GONE);
            bluetoothSwitch.setVisibility(View.GONE);
            hostButton.setVisibility(View.GONE);
            userButton.setVisibility(View.GONE);
            textView.setText("Host");
            editTextView.setVisibility(View.VISIBLE);
            locationButton.setVisibility(View.VISIBLE);
            editTextView.setText(hostLocation);

        }
        else if(hostOrUser==1){
            System.out.println("case100 : "+hostOrUser);
            imageButton.setVisibility(View.GONE);
            button2.setVisibility(View.VISIBLE);
            storageLocationSwitch.setVisibility(View.VISIBLE);
            bluetoothSwitch.setVisibility(View.VISIBLE);
            hostButton.setVisibility(View.GONE);
            userButton.setVisibility(View.GONE);
            textView.setText("User");
        }
        //블루투스 광고시작
        Context context = MainActivity.this;

//        if (savedInstanceState == null) {

            mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                    .getAdapter();

            // Is Bluetooth, gps supported on this device?

            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }

            if (mBluetoothAdapter != null) {

                // Is Bluetooth turned on?
                if (mBluetoothAdapter.isEnabled()) {

                    // Are Bluetooth Advertisements supported on this device?
                    if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

                        // Everything is supported and enabled, load the fragments.
                        if(hostOrUser==0&&(!editTextView.getText().toString().equals("")) ){
                            setupFragments();
                        }

                    } else {

                        // Bluetooth Advertisements are not supported.
                        showErrorText(R.string.bt_ads_not_supported);
                    }
                } else {

                    // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
                }
            } else {

                // Bluetooth is not supported.
                showErrorText(R.string.bt_not_supported);
            }
//        }

        //블루투스 광고 끝



        //블루투스 스캐너 포그라운드시작

        bltScanService = new Intent(this, MybltScanService.class);
        //startService(bltScanService);


        //위치저장 포그라운드시작

        locationStorageService = new Intent(this, MylocationStorageService.class);

        //startService(locationStorageService);
        //포그라운드 끝


        // 위치 저장,블루투스 자동 체크인 체크되어있었으면 다시 체크한상태로..
        if(globalLocationChecked==true){
            storageLocationSwitch.setChecked(true);
        }
        if(globalBluetoothChecked==true){
            bluetoothSwitch.setChecked(true);
        }
        //onclick 버튼들
        locationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                hostLocation = editTextView.getText().toString();
                setupFragments();
                try {
                    Thread th = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        try{
                                            confirmAT();
                                            OkHttpClient client = new OkHttpClient();
                                            String url = "http://115.21.52.248:8080/host/update?hostID=" + userID + "&&loc=" + hostLocation;
                                            Request.Builder builder = new Request.Builder().url(url).get();
                                            Request request = builder.build();
                                            Response response = client.newCall(request).execute();
                                            if (response.body().string().equals("200")) {
                                                System.out.println("loc trans success");
                                            }
                                            System.out.println("locationButton : " + hostLocation);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    th.start();
                    th.join();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        hostButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(hostOrUser==-1) {
                    hostOrUser = 0;
                    userButton.setEnabled(false);
                    imageButton.setEnabled(true);
                    imageButton.setVisibility(View.VISIBLE);

                }
                else if(hostOrUser==0){
                    hostOrUser=-1;
                    userButton.setEnabled(true);
                    imageButton.setVisibility(View.GONE);
                }
            }
        });
        userButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(hostOrUser==-1) {
                    hostOrUser = 1;
                    hostButton.setEnabled(false);
                    imageButton.setEnabled(true);
                    imageButton.setVisibility(View.VISIBLE);
                }
                else if(hostOrUser==1){
                    hostOrUser=-1;
                    hostButton.setEnabled(true);
                    imageButton.setVisibility(View.GONE);
                }
            }
        });


            if (access_token == null) {
//                new Thread() {
//                    public void run() {
//                        if(hostOrUser==0)
//                            test2 = new Requests("http://115.21.52.248:8080/host/connect").getLoginUrl();
//                        else if(hostOrUser==1)
//                            test2 = new Requests("http://115.21.52.248:8080/kakao/connect").getLoginUrl();
//                    }
//                }.start();
                //버튼 클릭리스너
                System.out.println("onResume login : ");
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(hostOrUser==0) {
                            //advertiserFragment = new AdvertiserFragment();
                            if(!editTextView.getText().toString().equals(""))
                                setupFragments();
                        }
                        try {
                            Thread th = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (hostOrUser == 0)
                                        test2 = new Requests("http://115.21.52.248:8080/host/connect").getLoginUrl();
                                    else if (hostOrUser == 1)
                                        test2 = new Requests("http://115.21.52.248:8080/kakao/connect").getLoginUrl();
                                }
                            });
                            th.start();
                            th.join();
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        System.out.println("onResume login : " + access_token);
                        System.out.println("onResume hostOrUser : " + hostOrUser);
                        Intent intent = new Intent(getApplicationContext(), WebViewPage.class);
                        intent.putExtra("my_data", test2);
                        intent.putExtra("hostOrUser",hostOrUser);
                        System.out.println("test2 : "+test2);


                        startActivityForResult(intent, 100);
                    }


                });

            } else {
//                new Thread() {
//                    public void run() {
//                        if(hostOrUser==0)
//                        test2 = new Requests("http://115.21.52.248:8080/host/logout?id=").kakaoLogout(userID, access_token);
//                        else if(hostOrUser==1)
//                            test2 = new Requests("http://115.21.52.248:8080/kakao/logout?id=").kakaoLogout(userID, access_token);
//                    }
//                }.start();
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //access_token 해제하는 코드
                       try {
                           Thread th = new Thread() {
                               public void run() {
                                   confirmAT();
                                   if (hostOrUser == 0)
                                       test2 = new Requests("http://115.21.52.248:8080/host/logout?id=").kakaoLogout(userID, access_token);
                                   else if (hostOrUser == 1)
                                       test2 = new Requests("http://115.21.52.248:8080/kakao/logout?id=").kakaoLogout(userID, access_token);
                                   access_token = null;
                               }
                           };
                           th.start();
                           th.join();
                       }catch(Exception e){
                           e.printStackTrace();
                       }

                        Intent intent = new Intent(getApplicationContext(), WebViewPage.class);
                        intent.putExtra("my_data", test2);
                        intent.putExtra("hostOrUser",hostOrUser);
                        System.out.println("onResume 로그아웃버튼 클릭 : " + test2);
                        startActivityForResult(intent, 200);


                    }
                });

            }

        //위치저장 껏다켜기
        storageLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub


                if(isChecked == true){
                    System.out.println("access_token: " + access_token);
                    locationStorageService.putExtra("userID", userID);
                    locationStorageService.putExtra("access_token",access_token);
                    locationStorageService.putExtra("refresh_token",refresh_token);
                    startService(locationStorageService);
                    globalLocationChecked = isChecked;
                }
                else{
                    stopService(locationStorageService);
                    globalLocationChecked = isChecked;
                }
            }
        });

        //블루투스 껏다켜기
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // TODO Auto-generated method stub

                if(isChecked == true){
                    bltScanService.putExtra("userID", userID);
                    startService(bltScanService);
                    globalBluetoothChecked = isChecked;
                }
                else{
                    stopService(bltScanService);
                    globalBluetoothChecked = isChecked;
                }

            }
        });

    }

    //블루투스 함수 시작
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {

                    // Bluetooth is now Enabled, are Bluetooth Advertisements supported on
                    // this device?
                    if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

                        // Everything is supported and enabled, load the fragments.
                            if(hostOrUser==0) {
                                setupFragments();
                            }

                    } else {

                        // Bluetooth Advertisements are not supported.
                        showErrorText(R.string.bt_ads_not_supported);
                    }
                } else {

                    // User declined to enable Bluetooth, exit the app.
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 100:
                access_token = data.getStringExtra("access_token");
                refresh_token = data.getStringExtra("refresh_token");
                userID = data.getStringExtra("userID");
                if(hostOrUser==0) {
                    System.out.println("case100 : "+hostOrUser);
                    imageButton.setVisibility(View.GONE);
                    button2.setVisibility(View.VISIBLE);
                    storageLocationSwitch.setVisibility(View.GONE);
                    bluetoothSwitch.setVisibility(View.GONE);
                    hostButton.setVisibility(View.GONE);
                    userButton.setVisibility(View.GONE);
                    textView.setText("Host");
                    editTextView.setVisibility(View.VISIBLE);
                    locationButton.setVisibility(View.VISIBLE);
                }
                else if(hostOrUser==1){
                    System.out.println("case100 : "+hostOrUser);
                    imageButton.setVisibility(View.GONE);
                    button2.setVisibility(View.VISIBLE);
                    storageLocationSwitch.setVisibility(View.VISIBLE);
                    bluetoothSwitch.setVisibility(View.VISIBLE);
                    hostButton.setVisibility(View.GONE);
                    userButton.setVisibility(View.GONE);
                    textView.setText("User");
                }
                break;
            case 200:
                access_token = data.getStringExtra("access_token");
                refresh_token = data.getStringExtra("refresh_token");
                userID = data.getStringExtra("userID");
                // 로그아웃하고나서

                button2.setVisibility(View.GONE);
                storageLocationSwitch.setVisibility(View.GONE);
                bluetoothSwitch.setVisibility(View.GONE);
                hostButton.setVisibility(View.VISIBLE);
                userButton.setVisibility(View.VISIBLE);
                hostButton.setEnabled(true);
                userButton.setEnabled(true);
                imageButton.setVisibility(View.GONE);
//                textView.setVisibility(View.GONE);
                editTextView.setVisibility(View.GONE);
                locationButton.setVisibility(View.GONE);

                clearPrefs();
                storageLocationSwitch.setChecked(false);
                bluetoothSwitch.setChecked(false);
                if(hostOrUser==0){
                    removeFragments();
                    advertiserFragment.logoutAdvertising();
                }
                hostOrUser=-1;
                userID=null;
                hostLocation=null;
               // advertiserFragment.stopAdvertising();
                if(hostOrUser==0){
                    advertiserFragment.stopAdvertising();
                }else{
                    stopService(bltScanService);
                    stopService(locationStorageService);
                }
//                if(bltScanService!=null) {
//                    bltScanService.putExtra("error", -1);
//                }
//                if(locationStorageService!=null) {
//                    locationStorageService.putExtra("error", -1);
//                }
                onStop();
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        /*
        ScannerFragment scannerFragment = new ScannerFragment();
        // Fragments can't access system services directly, so pass it the BluetoothAdapter
        scannerFragment.setBluetoothAdapter(mBluetoothAdapter);
        transaction.replace(R.id.scanner_fragment_container, scannerFragment);
        */
        transaction.replace(R.id.advertiser_fragment_container, advertiserFragment);
        transaction.commit();
    }
    private void removeFragments(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


        transaction.replace(R.id.advertiser_fragment_container, advertiserFragment);
        transaction.hide(advertiserFragment);
        //transaction.remove(advertiserFragment);
        transaction.commit();

    }

    private void showErrorText(int messageId) {

        TextView view = (TextView) findViewById(R.id.error_textview);
        view.setText(getString(messageId));
    }


    //블루투스 함수 끝끝

    //State관리 함수
    protected void saveState(){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userID",userID);
        editor.putString("access_token",access_token);
        editor.putBoolean("globalLocationChecked",globalLocationChecked);
        editor.putBoolean("globalBluetoothChecked",globalBluetoothChecked);
        editor.putBoolean("globalAdvertiserChecked",globalAdvertiserChecked);
        editor.putString("hostLocation",hostLocation);
        editor.putInt("hostOrUser",hostOrUser);
        editor.commit();
    }
    protected void restoreState(){
        SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
        if((pref!=null)&&(pref.contains("userID")) ){
            userID = pref.getString("userID","");
        }
        if((pref!=null)&&(pref.contains("access_token")) ){
            access_token = pref.getString("access_token","");
        }
        if((pref!=null)&&(pref.contains("globalLocationChecked"))){
            globalLocationChecked = pref.getBoolean("globalLocationChecked",false);
        }
        if((pref!=null)&&(pref.contains("globalBluetoothChecked"))){
            globalBluetoothChecked = pref.getBoolean("globalBluetoothChecked",false);
        }
        if((pref!=null)&&(pref.contains("globalAdvertiserChecked"))){
            globalAdvertiserChecked = pref.getBoolean("globalAdvertiserChecked",false);
        }
        if((pref!=null)&&(pref.contains("hostLocation"))){
            hostLocation = pref.getString("hostLocation","");
        }
        if((pref!=null)&&(pref.contains("hostOrUser"))){
            hostOrUser = pref.getInt("hostOrUser",-1);
        }
    }
    protected void clearPrefs(){
        SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

}

