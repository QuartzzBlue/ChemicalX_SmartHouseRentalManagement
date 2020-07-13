# 집똑 JipTalk 
실시간 납부 확인, 계약 사항 파악,임대인과 세입자간의 커뮤니케이션(공지사항, 1:1 메시지) 서비스를 제공하는<br>
**스마트한 통합 임대차 관리 플랫폼** Android-Native Application 입니다

## 프로젝트 기간
2020년 5월 12일 (화)  ~ 2020년 7월 5일 (일) ***약 8주***

- 1주차 (5월 12일 ~ 5월 17일) : 프로젝트 기획 및 기획안 작성 / 요구사항 정의 / 시스템 분석
- 2주차 (5월 18일 ~ 5월 24일) : 아키텍처 설계
- 3 -  7주차 (5월 25일 ~ 6월 28일) : 개발
- 8주차 (6월 29일 ~ 7월 5일) : 파이널 보고서 작성

## 구성원 및 역할
|이름|역할|
|-----|-----|
|[조현민](https://github.com/JHM9191)|- 1:1 대화<br>- 공지사항|
|[최여진](https://github.com/yeojini)|- 임대인,세입자 홈화면<br>- 건물 추가, 건물 관리|
|[이슬](https://github.com/QuartzzBlue)|- 세입자 관리<br>- 회원가입 및 로그인 <br>- 아이디/비밀번호 찾기<br>- 설정|

## Using Technology 
![image](https://user-images.githubusercontent.com/58680458/86533100-9d20ba00-bf09-11ea-8199-4601c3727462.png)

## ERD
![image](https://user-images.githubusercontent.com/58680458/86531210-4364c380-befa-11ea-9aa4-21f377ffd9ef.png)

# UI
※이미지를 클릭하면 큰 화면으로 볼 수 있습니다.


## 1. 공통 기능

### 1.1 회원가입

|IMG|기능|
|-----|-----|
|<img src="https://user-images.githubusercontent.com/31427119/87227360-66e5ad80-c3d5-11ea-9a62-bc96b5e97b83.gif" height="350" width="200" >|- Firebase Authentification을 사용한 Email과 핸드폰 번호 인증 <br>- (세입자) 임대인이 먼저 가입한 상태에서만 가입 가능 <br>- (세입자) 임대인이 세입자를 미리 등록했다면 해당 계약 정보 자동으로 표시됨 |


### 1.2 아이디/비밀번호 찾기

|IMG|기능|
|-----|-----|
|<img src="https://user-images.githubusercontent.com/31427119/87296686-1e97ce00-c542-11ea-8ade-da6a5333906a.gif" height="350" width="200" >|- DB에서 이름과 휴대폰 번호로 아이디 검색  <br>- Firebase Authentification을 사용하여 비밀번호 재설정 링크 전송|

### 1.3 로그인

|IMG|기능|
|-----|-----|
|<img src="https://user-images.githubusercontent.com/31427119/87041133-bccb2180-c22c-11ea-8e54-f5ca73121ce4.gif" height="350" width="200" >|- SharedPreference를 사용한 자동 로그인 설정<br>- Firebase Authentification(이메일/비밀번호) 로그인 <br>- 등록된 User 의 Category 에 따라 세입자 홈화면,  임대인 홈화면으로 분기|

### 1.4 메시지

1. 1:1 메시지

|IMG|기능|
|-----|-----|
|img||

2. 공지사항

|IMG|기능|
|-----|-----|
|img||

### 1.5 설정

|IMG|기능|
|-----|-----|
|<img src="https://user-images.githubusercontent.com/31427119/87247761-a6250480-c490-11ea-9a51-e99244ee221b.gif" height="350" width="200" > <img src="https://user-images.githubusercontent.com/31427119/87247770-b5a44d80-c490-11ea-82f3-46840a2bb14a.gif" height="350" width="200" > <img src="https://user-images.githubusercontent.com/31427119/87247901-83dfb680-c491-11ea-8f2d-c92a0097bd63.gif" height="350" width="200" >|- 알림 설정 기능<br>- 유저 정보 수정<br>   (Firebase Authentification 사용)<br>- 로그아웃 기능|

## 2. 임대인

### 2.1 홈 화면

|IMG|기능|
|-----|-----|
|img|- 납부 현황 및 월 총 납입금 요약<br>- 소유 건물 및 미납 현황 파악(Recycler View)|

### 2.2 건물 추가

|IMG|기능|
|-----|-----|
|img|- 다음 도로명 주소 검색 API 사용(Firebase Hosting 서비스로 서버 호스팅, WebView)|

### 2.3 건물 관리

|IMG|기능|
|-----|-----|
|img|- 건물정보, 납부 및 임대 현황 확인 가능<br>- 미납/완납 Sorting<br>- 각 호수의 세입자 및 납부 현황, 계약 기간 한눈에 파악(Recycler View)|

### 2.4 세입자 관리

|IMG|기능|
|-----|-----|
|<img src="https://user-images.githubusercontent.com/31427119/87228417-145bbf80-c3dc-11ea-88b9-047d13f05873.gif" height="350" width="200" > <img src="https://user-images.githubusercontent.com/31427119/87229578-1164cd00-c3e4-11ea-9f8b-f09e8c2d5a4e.gif" height="350" width="200" >|- TabView<br> - 입금 내역 표시<br>   (Cloud Function 활용하여 납부일마다 청구내역서 업데이트, Recycler View)<br>- 세입자 계약 정보 확인 및 수정<br>- 비고 란 표시|

## 3. 세입자
### 3.1 홈 화면

|IMG|기능|
|-----|-----|
|img|- 납부 버튼 클릭 시 해당 건물 임대인에게 실시간 알림 (FCM)<br>- 납부 내역 확인<br>- 계약 정보 확인|
