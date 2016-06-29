/* Contains class for handling all the server client related following operation.
 * 1)Opening ajax connection to server
 * 2)Sending message to server
 * 3)Receiving messages from server
 * 4)Reinitilizing connections
 *  
 */

function ClientServerOperationsHandler(ch, m){

	this.channel = ch;
	this.ajax = getAjaxClient();
	this.onMessage = m;

	// stick a reference to "this" into the ajax client, so that the handleMessage 
	// function can access the push client - its "this" is an XMLHttpRequest object
	// rather than the push client, coz thats how javascript works!
	this.ajax.ClientServerOperationsHandler = this;
	
	
	function getAjaxClient(){
		/*
		 * Gets the ajax client
		 * http://en.wikipedia.org/wiki/XMLHttpRequest
		 * http://www.w3.org/TR/XMLHttpRequest/#responsetext
		 */
	    var client = null;
	    try{
			// Firefox, Opera 8.0+, Safari
			client = new XMLHttpRequest();
		}catch (e){
			// Internet Explorer
			try{
				client = new ActiveXObject("Msxml2.XMLHTTP");
			}catch (e){
				client = new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
		return client;
	};
	
	/** 
	 * pass in a callback and a channel.  
	 * the callback should take a string, 
	 * which is the latest version of the model
	 * 
	 *  This method is called on login and it will register user to server
	 */
	ClientServerOperationsHandler.prototype.createConnection = function(){

		try{
			var params = escape("channel") + "=" + escape(this.channel) +"&"+ escape("userName")+ "=" + escape($("#userName").val());
			var url = "/BasicChatApplication/login?" + params;
			this.ajax.onreadystatechange = handleMessage;
			this.ajax.open("GET",url,true); //true means async, which is the safest way to do it
			this.ajax.send(null);
			
		}catch(e){
			alert(e);
		}
	};
	
	/**
	 * If any message to be send to the server call this method to send the message
	 * note :all the messages 
	 */
	ClientServerOperationsHandler.prototype.sendMessage = function (message){
		
		
		try{
		
			var requestParams = {
					message:message,
					"channel":'myChannel'
			      
			};
			//below param method take cares of special characters in data
			requestParams = jQuery.param(requestParams);
			
			$.ajax({
				  type : 'GET',//GET Or POST 
				  url  : '/BasicChatApplication/publish',
				  cache: false, //get fresh copy of details.html instead of cahced one
				  data :requestParams,
				  
				  // callback handler that will be called on error
				  error: function(jqXHR, textStatus, errorThrown){
				      // log the error to the console
				      console.log(
				          "The following error occured: "+
				          textStatus, errorThrown
				      );
				  }
				 
				});
		
		}catch(e){
			alert(e);
		}
	};

	var oldResponseMessage ="";
	function handleMessage() {
		//states are:
		//	0 (Uninitialized)	The object has been created, but not initialized (the open method has not been called).
		//	1 (Open)	The object has been created, but the send method has not been called.
		//	2 (Sent)	The send method has been called. responseText is not available. responseBody is not available.
		//	3 (Receiving)	Some data has been received. responseText is not available. responseBody is not available.
		//	4 (Loaded)
		try{
			
			if(this.readyState == 0){
				//this.ClientServerOperationsHandler.onMessage("0/-/-");
			}else if (this.readyState == 1){
				//this.ClientServerOperationsHandler.onMessage("1/-/-");
			}else if (this.readyState == 2){
				//this.ClientServerOperationsHandler.onMessage("2/-/-");
			}else if (this.readyState == 3){
				//for chunked encoding, we get the newest version of the entire response here, 
				//rather than in readyState 4, which is more usual.
				if (this.status == 200){
					//this.ClientServerOperationsHandler.onMessage("3/200/" + this.responseText.substring(this.responseText.lastIndexOf("|")));
					
					var newMessage = this.responseText;
					newMessage = newMessage.replace(oldResponseMessage, "");
					
					this.ClientServerOperationsHandler.onMessage( newMessage );
					
					oldResponseMessage = this.responseText;
					//alert(" Response 3 -200: "+ newMessage );
				}else{
					//this.ClientServerOperationsHandler.onMessage("3/200/" + this.responseText.substring(this.responseText.lastIndexOf("|")));
					
					var newMessage = this.responseText;
					newMessage = newMessage.replace(oldResponseMessage, "");
					
					this.ClientServerOperationsHandler.onMessage( newMessage );
					oldResponseMessage = this.responseText;
				}
			}else if (this.readyState == 4){
				if (this.status == 200){

					//this.ClientServerOperationsHandler.onMessage("3/200/" + this.responseText.substring(this.responseText.lastIndexOf("|")));
					
					var newMessage = this.responseText;
					newMessage = newMessage.replace(oldResponseMessage, "");
					this.ClientServerOperationsHandler.onMessage( newMessage );
					//alert(" Response 4 -200: "+ newMessage );
					//the connection is now closed.
					//start again - we were just disconnected!
					this.ClientServerOperationsHandler.createConnection();
					
					oldResponseMessage = this.responseText;
					

				}else{
					this.ClientServerOperationsHandler.onMessage("4/" + this.status + "/-");
					
				}
			}
			this.responseText = "";
		}catch(e){
			alert(e);
		}
	};
}



function onMessageReceive(model){
	//format of chat message is so split it as chatmessage#from#to#message=message
	var messages = model.split("#");
	
	switch(messages[0]){
		case CHATMESSAGE: 
						  var messageFrom =messages[1];
						  renderChatDiv(messageFrom);
						  var message =messages[3].split("=")[1];//get message
						  message = messageFrom +":"+message;
						  var oldMessages = $("#"+messageFrom).find("#chatHistory").val();
						  $("#"+messageFrom).find("#chatHistory").val(oldMessages  +""+message);
						  
						  if(! $( "#"+messageFrom).is(":visible")  ){
								$( "#"+messageFrom).show();
						  }
						  
						  //set scroller down ofchatHistory text area
						  var $charHistory = $("#"+messageFrom).find("#chatHistory");
						  $charHistory .scrollTop($charHistory.prop("scrollHeight"));
						  
						  blinkChatBox($("#"+messageFrom));

						  newTitle =messageFrom+ " says "+message;
						  
						  if(window_focused == false){
							  interval = setInterval(changeTitle, 1000);
						  }
						  
						  
						  
		break;
		case NEWUSER :
					  
					  $(".onlineUsersUL").append("<li><img class='onlineGreen' src='../images/onlineGreen.png'><a style='float:left;' href='#'>"+messages[1]+"</a></li>");
					  //;("Added New User is: "+messages[1]);
					  	
					  setSelectFriendEvent();
		break;
		case GROUPCHATMESSAGE:
			  
			  var messageFrom =messages[1];
			  var message =messages[2].split("=")[1];//get message
			  message = messageFrom +":"+message;
			 $(".groupChatHistory").append(message);
			 var $groupChatHistory = $(".groupChatHistory");
			 $groupChatHistory.scrollTop($groupChatHistory.prop("scrollHeight"));
			break;
		case ERRORMESSAGE:
			 var errorMessage =messages[1];
			alert(errorMessage);
			location.reload();
			break;
		case LOGEDOUT :
			$('.onlineUsersUL > li > a').each(function () {
				var userLoggedout =messages[1];
				if(this.innerText == userLoggedout){
					$(this).parent().remove();	
				}
				
			});
				
			break;
	}
	
	
}

function blinkChatBox(element){
	element.fadeOut('fast', function(){
	    $(this).fadeIn('fast', function(){
	        
	    });
	});
	element.find(".chatDivTitle").addClass("highlightColor");
}

/**
 * Below logic is for blincking 
 */
var isOldTitle = true;
var title = "Chat Application";
var newTitle = "newTitle";
var interval = null;
var window_focused =false;


function changeTitle() {
    document.title = isOldTitle ? title : newTitle;
    isOldTitle = !isOldTitle;
}

$(window).focus(function () {
    clearInterval(interval);
    $("title").text(title);
    window_focused =true;
    
});

$(window).blur(function () {
    window_focused =false;
});

