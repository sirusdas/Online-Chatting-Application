
/**
 * Contains methods 
 */
//below are the constants defined for the message type
CHATMESSAGE = "chatmessage";//message format is : chatmessage#from#to#message=message
NEWUSER = "newuser";//newuser#username
GROUPCHATMESSAGE="groupchatmessage";
ERRORMESSAGE="error";


var clientServerOperations ;


$( function(){
	$("#login").click(function(){
		loginUser();
	});
	
	$("#userName").keyup(function(event){
		if(event.keyCode == '13'){
			loginUser();
		}
	});
	
	$(".groupChatBox").keyup (function(event){
		if(event.keyCode == '13'){
			$(".groupChatHistory").append("Me:"+$(this).val());
			
			var $groupChatHistory = $(".groupChatHistory");
			$groupChatHistory.scrollTop($groupChatHistory.prop("scrollHeight"));

			var from =$('#userName').val();
			var message="message="+$(this).val();
			var chatMessage = GROUPCHATMESSAGE+"#"+from +"#"+message;
			
			clientServerOperations.sendMessage(chatMessage);

			$(this).val('');
			
		}
	});
	

	
});

function loginUser(){
	if($("#userName").val()==""){
		alert("Please enter user name!");
		return;
	}
	clientServerOperations = new ClientServerOperationsHandler("myChannel", onMessageReceive);
	clientServerOperations.createConnection();
	populateFriendList();	
	$(".loginDiv").hide();
	$(".container").show();
	var loginUserDetails = "Welcome "+$("#userName").val()+" <br>  <a href='#' id='logout'>Logout</a><br><br>";
	$(".loginUserDetails").html(loginUserDetails);
	
	$("#logout").click(function(){
		logout();
	});
	

}
//set onclick event of all the LI of Ul
function setSelectFriendEvent(){
	
	//set onclick event of all the LI of Ul and onclick render the popup on bottom right corner
	$(".onlineUsersUL li").click(function (){
		var userName = $(this).text();
		renderChatDiv(userName);

	});
}

/*
 * This method is responsible for rendering the popup in bottom right cornder of window
 * if window is already present it will not do anything
 * if window is hidden then it will make it visible
 * if windows is getting first time created then it will create html window with base window div as user name
 */
function renderChatDiv(userName){
	
	if( $( "#"+userName).length ){
		if(! $( "#"+userName).is(":visible")  ){
			$( "#"+userName).show();
		}
		return;
	}
	
	//create chat window where id base div is user name.
	var chatWindow = "<div id='"+userName+"' class='chatDiv'>"+
	"<div class='chatDivTitle'>"+userName+" <a href='#' style='float:right' class='chatClose'>close</a></div>"+
	"<textarea rows='12' id='chatHistory' class='chatHistory' readonly='true' cols='30' ></textarea><br>"+
	"<textarea rows='3' id='chatBox' class='chatBox' cols='30' ></textarea>"+
	"</div>";
	$(".allChatDivs").append(chatWindow);
	
	//event on close button for newly created chat window
	$("#"+userName).find(".chatClose").click(function(event){
		$(this).closest(".chatDiv").hide();
	});
	
	//set event keyup on chat box text area of this chat window
	$("#"+userName).find("#chatBox").keyup(function(event){
		
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if(keycode == '13'){
			var from =$('#userName').val();
			var to   = userName; 
			var message="message="+$(this).val();
			var chatMessage = CHATMESSAGE+"#"+from +"#"+to+"#"+message;
			clientServerOperations.sendMessage(chatMessage);
			
			var oldChatHistory = $("#"+userName).find("#chatHistory").val();
			$("#"+userName).find("#chatHistory").val(oldChatHistory +"me:"+$(this).val());
			$(this).val('');
			
			//set scroller down chatHistory text area when somthing typed and appended to the history
			var $charHistory = $("#"+userName).find("#chatHistory");
			$charHistory .scrollTop($charHistory.prop("scrollHeight"));
			
			clearInterval(interval);//clear interval added for highlighting title.

		}
		//if esc pressed in the chat box then hide the chat window 
		if (event.keyCode == 27) {
			$(this).closest(".chatDiv").hide();
		}
	});
	
	//set event click event on chat box text area of this chat window
	//change color title from orange to blue if required
	$("#"+userName).find("#chatBox").click(function(event){
		
		var chatDivHeader = $(this).siblings('.chatDivTitle');
		if(chatDivHeader.hasClass("highlightColor")){
			chatDivHeader.removeClass("highlightColor");
		}
	});
	
}

/*
 * Populate list of online users when loged in
 */
function populateFriendList(){
	
	var requestParams = {
		      channel:'myChannel'
		};

		//below param method take cares of special characters in data
		requestParams = jQuery.param(requestParams);

		//ajax to get comma separated list of online users
		$.ajax({
		  type : 'GET',//GET Or POST 
		  url  : "/BasicChatApplication/getOnlineUsersList",
		  cache: false, //get fresh copy of details.html instead of cahced one
		  data :requestParams,
		 
		  success: function(response, textStatus, jqXHR){
			  
			  if(response ==""){
				  
			  }else{
				  var users = response.split(",");
				  
				  for(var i=0;i<users.length;i++){
					  if(users[i] != $("#userName").val()){
						 $(".onlineUsersUL").append("<li><img class='onlineGreen' src='../images/onlineGreen.png'><a style='float:left;' href='#'>"+users[i]+"</a></li>");
						 $(".onlineUsersUL li").addClass("borderBottom");
					  }
				  }
				  
				  //set onclick event of all the LI of Ul
				  setSelectFriendEvent();
			  }
		  },
		  
		  error: function(jqXHR, textStatus, errorThrown){
		      console.log(
		          "The following error occured: "+
		          textStatus, errorThrown
		      );
		  }
	
		});
}


$(window).on('beforeunload', function(){
	
	//logout 
	var param = "userName="+$('#userName').val()+'&channel=myChannel';
	jQuery.ajax({url:"/BasicChatApplication/logout?"+param, async:false});
	
	if(confirm('Are you sure you want to logout chat session ?')){
		var chatMessage ="Loutout#"+$('#userName').val() ;
		clientServerOperations.sendMessage(chatMessage);
		return;
	}
	
	return;
});

function logout(){
	
	var param = "userName="+$('#userName').val()+'&channel=myChannel';
	jQuery.ajax({url:"/BasicChatApplication/logout?"+param, async:false});
	location.reload();
	
}
