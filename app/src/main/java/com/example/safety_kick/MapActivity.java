package com.example.safety_kick;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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
        // 위치(위도,경도) 객체
        LatLng location = new LatLng(34.912884, 126.437832);
        // 카메라 위치와 줌 조절(숫자가 클수록 확대)
        CameraPosition cameraPosition = new CameraPosition(location, 17);
        naverMap.setCameraPosition(cameraPosition);
        // 줌 범위 제한
        naverMap.setMinZoom(5.0);   //최소
        naverMap.setMaxZoom(18.0);  //최대
        // 카메라 영역 제한
        LatLng northWest = new LatLng(31.43, 122.37);   //서북단
        LatLng southEast = new LatLng(44.35, 132);      //동남단
        naverMap.setExtent(new LatLngBounds(northWest, southEast));
    }
}
