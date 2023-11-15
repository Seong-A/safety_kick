package com.example.safety_kick;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class AlcoholActivity extends AppCompatActivity {
    private ImageView nextButton;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 2;
    private BluetoothSocket socket = null;
    private InputStream inputStream = null;
    private Handler handler = new Handler();
    private boolean okReceived = false;
    private int consecutiveOkCount = 0;
    private static final long DURATION = 10000; // 10초
    private long startTime;
    private static final int OK_VALUE = 0;
    private static final int WARNING_VALUE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol);

        nextButton = findViewById(R.id.nextButton);
        nextButton.setVisibility(View.GONE); // nextButton 초기에 숨김
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlcoholActivity.this, HelmetActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlcoholActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
            }
        });

        // Bluetooth 연결 설정을 onCreate에서 시작
        setupBluetoothConnection();
    }


    // Bluetooth 연결 및 데이터 수신을 위한 메서드
    private void setupBluetoothConnection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Bluetooth를 지원하지 않는 기기
            // 처리 로직 추가
        } else {
            // Bluetooth를 활성화하려면 사용자에게 요청
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth 권한이 있는 경우
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    // Bluetooth 활성화 상태
                    // 기기 검색 및 연결 설정

                    // Bluetooth 장치 검색
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().equals("KICK")) {
                            // 원하는 Bluetooth 장치를 찾았을 때 연결 설정

                            try {
                                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                                socket.connect(); // Bluetooth 연결 설정
                                inputStream = socket.getInputStream();
                                // 데이터 수신 루프를 시작하도록 호출
                                startDataReceiving();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                // Bluetooth 권한이 없는 경우
                // 권한 요청
                requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
            }
        }
    }

    // 데이터 수신 루프
    private void startDataReceiving() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, bytesRead, "UTF-8");

                        // 데이터 확인을 위해 로그에 출력
                        Log.d("BluetoothData", data);

                        // 데이터 처리
                        processAlcoholSensorResult(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Bluetooth 연결 및 데이터 수신 중단
    private void stopBluetoothConnection() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 Bluetooth 연결 및 데이터 수신 중단
        stopBluetoothConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티가 화면에서 숨겨질 때 Bluetooth 연결 및 데이터 수신 중단
        stopBluetoothConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 액티비티가 사용자 눈에 보이지 않게 될 때 Bluetooth 연결 및 데이터 수신 중단
        stopBluetoothConnection();
    }

    // 알콜 센서 결과를 처리하는 함수
    public void processAlcoholSensorResult(String result) {
        // 블루투스 데이터 확인을 위해 추가한 로그
        Log.d("BluetoothDataProcessed", "Processing data: " + result);

        try {
            int value = Integer.parseInt(result);

            if (value == OK_VALUE) {
                // 현재 시간을 기록
                long currentTime = System.currentTimeMillis();

                if (!okReceived) {
                    // 첫 번째 OK를 받은 경우, 시작 시간을 기록
                    startTime = currentTime;
                }

                okReceived = true;

                // 10초 동안 연속으로 OK를 받으면 nextButton을 표시
                if (currentTime - startTime >= DURATION) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nextButton.setVisibility(View.VISIBLE); // nextButton을 화면에 표시
                        }
                    });
                }
            } else if (value == WARNING_VALUE) {
                // Warning을 받았으므로 연속 OK 횟수를 초기화
                okReceived = false;
                consecutiveOkCount = 0;

                // Warning이면 토스트 메시지 표시
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AlcoholActivity.this, "음주 시에는 킥보드를 탑승할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                // nextButton 숨김
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextButton.setVisibility(View.GONE);
                    }
                });
            }
        } catch (NumberFormatException e) {
            // 정수로 변환할 수 없는 경우 무시
            Log.e("BluetoothDataProcessed", "Error parsing data: " + result);
        }
    }
}