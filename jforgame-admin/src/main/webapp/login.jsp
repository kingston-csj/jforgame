<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
<title>游戏后台登录</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script src="js/jquery-1.5.2.min.js" type="text/javascript"></script>
<link type="text/css" href="css/login.css" rel="stylesheet" />
</head>
<body>
	<div id="login">
		<div id="title">Game Administrator</div>
		<div id="loginInfo">
			<br>
			<div>
				account <input id="account" type="text">
			</div>
			<div>
				<span id="accountTips"></span>
			</div>
			<br>
			<div>
				<span style="margin-right: 7px"> password <input
					id="password" type="password">
				</span>
			</div>
			<br>
			<div>
				<span id="passwordTips"></span>
			</div>
			<div>
				<span id="loginFailureTips" style="color: red"></span>
			</div>
			<div>
				<input id="submit" type="button" value="login">
			</div>
		</div>
	</div>

	<script type="text/javascript">
    $("#account").focus(function() {
    	 //清空提示语
        $("#loginFailureTips").html("")
        $("#accountTips").html("")
    })
    
    $("#password").focus(function() {
    	 //清空提示语
        $("#loginFailureTips").html("")
        $("#passwordTips").html("")
    })

    $("#submit").click(function() {
    	 if (!$("#account").val()) {
    		 $("#accountTips").html("请输入账号")
             return;
         }
    	 if (!$("#password").val()) {
    		 $("#passwordTips").html("请输入密码")
    		 return;
    	 }

         $.ajax({
        	 url:'login',
        	 type: "POST",
        	 dataType:'json',
        	 contentType:'application/x-www-form-urlencoded; charset=UTF-8',
        	 data:{
        		 account:$("#account").val(),
        		 password:$("#password").val()
        	 },
         	 success:function(data){
         		  if(data.code > 0){
                      location.href = "index.jsp";
                  }else{
                      document.getElementById("loginFailureTips").innerHTML="输入的账号或密码不正确";
                  }
         	 },
         	 error:function(data) {
         		 console.info("fail")
         	 }
         }) 
    })
</script>
</body>
</html>
