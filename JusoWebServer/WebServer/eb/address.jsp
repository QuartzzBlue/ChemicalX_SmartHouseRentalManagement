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
    //load�Լ��� �̿��Ͽ� core��ũ��Ʈ�� �ε��� �Ϸ�� ��, �����ȣ ���񽺸� �����մϴ�.
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