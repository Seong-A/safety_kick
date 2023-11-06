package com.example.safety_kick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // naver map
        String naver_client_id = getString(R.string.NAVER_CLIENT_ID);
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(naver_client_id));

        // 지도 객체 받아오기
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        LatLng markerPosition = new LatLng(34.912884, 126.437832);

        Marker marker = new Marker();
        marker.setPosition(markerPosition);

        marker.setMap(naverMap);

        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                // Launch the QR code screen
                new IntentIntegrator(MapActivity.this).initiateScan();
                return true;
            }
        });

        // 카메라 위치와 줌 조절(숫자가 클수록 확대)
        CameraPosition cameraPosition = new CameraPosition(markerPosition, 17);
        naverMap.setCameraPosition(cameraPosition);
        // 줌 범위 제한
        naverMap.setMinZoom(5.0);   //최소
        naverMap.setMaxZoom(18.0);  //최대
        // 카메라 영역 제한
        LatLng northWest = new LatLng(31.43, 122.37);   //서북단
        LatLng southEast = new LatLng(44.35, 132);      //동남단
        naverMap.setExtent(new LatLngBounds(northWest, southEast));
    }

    // QR 코드 스캔 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // 스캔 결과가 없는 경우
            } else {
                // 스캔 결과가 있는 경우
                checkAndBorrowItem(result.getContents());
            }
        }
    }

    private void checkAndBorrowItem(String scannedData) {
        DatabaseReference qrcodeRef = databaseReference.child("qrcode").child("qrcode1");

        qrcodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String id = dataSnapshot.child("id").getValue(String.class);
                    String startLatitude = dataSnapshot.child("start_latitude").getValue(String.class);
                    String startLongitude = dataSnapshot.child("start_longitude").getValue(String.class);

                    if (id != null && id.equals(scannedData)) {
                        // Matching QR code found
                        // Navigate to AlcoholActivity
                        Intent intent = new Intent(MapActivity.this, AlcoholActivity.class);
                        // Pass the latitude and longitude to the next activity if needed
                        intent.putExtra("latitude", startLatitude);
                        intent.putExtra("longitude", startLongitude);
                        startActivity(intent);
                    } else {
                        // No matching QR code found
                        Toast.makeText(MapActivity.this, "No matching QR code found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // "qrcode1" does not exist
                    Toast.makeText(MapActivity.this, "QR code data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapActivity.this, "Failed to retrieve data from the database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
