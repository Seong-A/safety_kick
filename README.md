# Safety_kick
## 센서 결합 안전 전동킥보드
센서를 부착하여 음주상태와 다인 탑승을 검증하고 카메라를 통한 객체 인식으로 안전모 착용을 검증

## Project
- Develop Period ( 2023.03 ~ 2023.11 )
- Contributors ( 4 people )
- Configuration Management ( Github )

## Development Environment
- Android Studio 7.3 ( minSDK 24, targetSDK 34 )
- Library ( navigation, zxing, junit, bumptech, markeramen, Tensorflow Lite )
- Api ( Naver Map API )
- AVD ( Pixel 4 )
- Database ( Firebase )

## Hardware
- 음주측정기능 ( Arduino uno, MQ-3, buzzer, battery )
- 다인탑승방지기능 ( Arduino uno, Illuminance sensor, LED, buzzer, battery )

## WorkPlan
- Selection Topic ( 2023.03 )
- UI / UX Design , Program Plan ( 2023.04 )
- Database Design ( 2023.05 )
- Application ( 2023.06 ~ 2023.10 )
- Harware ( 2023.07 ~ 2023.08 )
- Tensorflow ( 2023.09 ~ 2023.10 )
- System Implementation ( 2023.10 )
- Test ( 2023.11 )

## Configuration
- Hardware

<img src="https://github.com/Seong-A/safety_kick/assets/83965377/1db9455f-c547-4723-b055-41a0123d2761">

- System

<img src="https://github.com/Seong-A/safety_kick/assets/83965377/25cfb244-4798-4796-a950-fd4946c3194f">

## Flow Chart
<img src="https://github.com/Seong-A/safety_kick/assets/83965377/f0644727-6159-4432-b595-a8dff4cc94d3">

## Service 
|Intro|Home|Login|Sign Up|
|:---:|:---:|:---:|:---:|
|![image](https://github.com/Seong-A/safety_kick/raw/main/assets/83965377/e76cb386-8672-4c0e-a824-c3b0358c89cb.png)|![image](https://github.com/Seong-A/safety_kick/raw/main/assets/83965377/ba1e602f-a09b-4704-8b58-2ad17be3e56b.png)|![image](https://github.com/Seong-A/safety_kick/raw/main/assets/83965377/822ccf46-2856-4261-b23b-4edb015d7948.png)|![image](https://github.com/Seong-A/safety_kick/raw/main/assets/83965377/822ccf46-2856-4261-b23b-4edb015d7948.png)|


#### 첫 화면(로고) (스플래시 화면) -> 메인페이지 -> 로그인 (일반 로그인, 구글 로그인) 및 회원가입 -> 메인페이지 이동
#### 메인페이지 (사용자 이름, 마이페이지 화면, 지도로 지쿠터 위치 확인, qr 인식하여 대여 및 반납, 고객센터)
#### 마이페이지 (사용자 이름, 회원정보수정, 주행내역, 결제수단,로그아웃)

