<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
<!-- <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script>
  new daum.Postcode({
    oncomplete: function(data) {
      if(data.userSelectedType=="R"){
        if(data.autoJibunAddress) {
                window.Android.setAddress(data.zonecode, data.autoJibunAddress, data.buildingName);
        } else {
                window.Android.setAddress(data.zonecode, data.jibunAddress, data.buildingName);
        }
      }
      else{
        window.Android.setAddress(data.zonecode, data.jibunAddress, data.buildingName);
      }
 
    },
  width : '100%',
  height : '100%',
 
  }).open();
</script>
 -->
 
 <script src="http://dmaps.daum.net/map_js_init/postcode.v2.js?autoload=false"></script>
 <script>
    //load함수를 이용하여 core스크립트의 로딩이 완료된 후, 우편번호 서비스를 실행합니다.
    daum.postcode.load(function(){
        new daum.Postcode({
            oncomplete: function(data) {
            	if(data.userSelectedType=="R"){
                    if(data.autoJibunAddress) {
                            window.Android.setAddress(data.zonecode, data.autoJibunAddress, data.buildingName);
                    } else {
                            window.Android.setAddress(data.zonecode, data.jibunAddress, data.buildingName);
                    }
                  }
                  else{
                    window.Android.setAddress(data.zonecode, data.jibunAddress, data.buildingName);
                  }
             }
        }).open();
    });
</script>
 
 </body>
</html>