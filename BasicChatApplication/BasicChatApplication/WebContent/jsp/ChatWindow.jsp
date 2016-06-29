<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Chat Application</title>

<script language="Javascript">//below are the constants defined for the message type
	CHATMESSAGE = "chatmessage";//message format is : chatmessage#from#to#message=message
	NEWUSER = "newuser";//newuser#username
	GROUPCHATMESSAGE="groupchatmessage";
	ERRORMESSAGE="error";
	LOGEDOUT='logedout';      
	
</script>

<script language="Javascript" type="text/javascript" src="../js/jquery1.7.1.js"></script>
<script language="Javascript" type="text/javascript" src="../js/clientServerOperationsHandler.js"></script>
<script language="Javascript" type="text/javascript" src="../js/push_client.js"></script>
<link rel="stylesheet" type="text/css" href="../css/style.css">

</head>
<body>
<h1>Chat Application</h1>

<img  src="../images/pageBackground.png" id="background-image" />
<img  src="../images/header_background.jpg" id="header-image" />



<span class="loginUserDetails"></span>
<br/>
<div id="loginDiv" class="loginDiv" align="center" >
		<p><b>User Name    : </b><input type="text" id="userName" name="userName" ></p>
		<p style="float: right;margin-right: 50px;"><input type="button" id="login" value="Login" ></p>
</div>


	
<div  class="container" >
	<div class="onlineUsersDIV" >
		<div class="boxTitle"><b>List of Online Users :</b></div>
		<ul class="onlineUsersUL"></ul><!-- This will hold all the online users -->
	</div>
	<div class="groupChat">
		<div class="boxTitle" ><b>Group chat here:</b></div>
		<textarea rows="21" id="groupChatHistory" readonly="true" class="groupChat groupChatHistory" cols="92"></textarea><br/>
		<textarea rows="3" id="groupChatBox" class="groupChatBox" cols="92"></textarea>
	</div>
	<div class="chatRoom" style="float:right;width: 200px;"></div>	
</div>

<div class="allChatDivs">	
</div>

</body>
</html>