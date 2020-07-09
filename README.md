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
[조현민](https://github.com/JHM9191)
[최여진](https://github.com/yeojini)
[이슬](https://github.com/QuartzzBlue)

## Using Technology 
![image](https://user-images.githubusercontent.com/58680458/86533100-9d20ba00-bf09-11ea-8199-4601c3727462.png)

## ERD
![image](https://user-images.githubusercontent.com/58680458/86531210-4364c380-befa-11ea-9aa4-21f377ffd9ef.png)

# UI

## 1. 공통 기능

### 1.1 회원가입

|IMG|기능|
|-----|-----|
|img|- Firebase Authentification을 사용한 Email과 핸드폰 번호 인증 <br>- (세입자) 임대인이 먼저 가입한 상태에서만 가입 가능 <br>- (세입자) 임대인이 세입자를 미리 등록했다면 해당 계약 정보 자동으로 표시됨 |


### 1.2 아이디/비밀번호 찾기

|IMG|기능|
|-----|-----|
|img|- Firebase Authentification을 사용하여 비밀번호 재설정 링크 전송|

### 1.3 로그인

|IMG|기능|
|-----|-----|
|img|- SharedPreference를 사용한 자동 로그인 설정<br>- 등록된 User 의 Category 에 따라 세입자 홈화면,  임대인 홈화면으로 분기|

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
|img||

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
|img|- 입금 내역 표시(Cloud Function 사용하여 DB 업데이트)<br>- 계약 정보 확인 및 수정<br>- 비고 란 |

## 3. 세입자
### 3.1 홈 화면

|IMG|기능|
|-----|-----|
|img|-납부 버튼 클릭 시 해당 건물 임대인에게 실시간 알림 (FCM)<br>- 납부 내역 확인<br>- 계약 정보 확인|
