/* MIT licensed */
// (c) 2010 Jesse MacFadyen, Nitobi
// Contributions, advice from : 
function FBConnect()
{
	if(window.plugins.childBrowser == null)
	{
		ChildBrowser.install();
	}
}

/**
 * Chimpped Ambasidor page
 * 
 */

FBConnect.prototype.ambassador = function()
{
    var authorize_url ="https://www.chimpped.com/index.php/chimpped_ambassador";
    window.plugins.childBrowser.showWebPage(authorize_url);
    var self = this;
    window.plugins.childBrowser.onLocationChange = function(loc){self.onLocationChange(loc);};
}


FBConnect.prototype.termsandconditions = function()
{
	 var  url="http://docs.google.com/gview?embedded=true&url="+String(window.httpclient.getBaseURL())+"/agreement/UserTerms%26Conditions.pdf";
	    console.log(url);
	    window.plugins.childBrowser.showWebPage(url);
	    var self = this;
	    window.plugins.childBrowser.onLocationChange = function(loc){self.onLocationChange(loc);};
  
   
}

/**********************************************************************************************************
 * @author Bhaskar Reddy
 * @Desc   Below functionality to open PDF document in child browser.
 *         Here PDF documents placed in server. The Base URL of server is declared in "scripts_android.js".
 *         
 *********************************************************************************************************** 
 */


FBConnect.prototype.privacyconnect = function()
{
    var authorize_url="http://docs.google.com/gview?embedded=true&url="+String(window.httpclient.getBaseURL())+"/agreement/policy.pdf";
    window.plugins.childBrowser.showWebPage(authorize_url,"License Agreement");//"http://docs.google.com/gview?embedded=true&url="+
    var self = this;
    window.plugins.childBrowser.onLocationChange = function(loc){self.onLocationChange(loc);};
}

/********************************************************************************************
 * 
 * @Desc  The below functionality to show specific vendor reviews in childbrowser. 
 * 
 * *******************************************************************************************
 */

FBConnect.prototype.reviewsConnect = function()
{
    var review_url_yelp=sessionStorage.getItem('review_url_yelp');
    var company_name= sessionStorage.getItem('companyname');
    var authorize_url  = review_url_yelp+" ?company_title= "+company_name;
    window.plugins.childBrowser.showWebPage(authorize_url,company_name);
    var self = this;
    window.plugins.childBrowser.onLocationChange = function(loc){self.onLocationChange(loc);};
}

/********************************************************************************************
 * 
 * @Desc  The below functionality to show specific vendor cancellation policy  pdf in childbrowser.
 * 
 * *******************************************************************************************
 */

FBConnect.prototype.cancellationPolicyConnect = function()
{
    var cancellationPolicyUrl=sessionStorage.getItem('cancellation_policy_url');
    console.log(cancellationPolicyUrl);
    var authorize_url  = "http://docs.google.com/gview?embedded=true&url="+cancellationPolicyUrl;
    window.plugins.childBrowser.showWebPage(authorize_url,"Cancellation Policy");
    var self = this;
    window.plugins.childBrowser.onLocationChange = function(loc){self.onLocationChange(loc);};
}



FBConnect.prototype.connect = function(client_id,redirect_uri,display)
{
	this.client_id = client_id;
	this.redirect_uri = redirect_uri;	
	var authorize_url  = "https://graph.facebook.com/oauth/authorize?";
		authorize_url += "client_id=" + client_id;
		authorize_url += "&redirect_uri=" + redirect_uri;
		authorize_url += "&display="+ ( display ? display : "touch" );
		authorize_url += "&type=user_agent";
	window.plugins.childBrowser.showWebPage(authorize_url,"Facebook Login");
	var self = this;
	window.plugins.childBrowser.onLocationChange = function(loc){self.onLocationChange(loc);};
}

FBConnect.prototype.onLocationChange = function(newLoc)
{
   /* // console.log("location changed-------------:"+newLoc);
    var args;
    if(newLoc.indexOf(this.redirect_uri) == 0)
	{
		var result = unescape(newLoc).split("#")[1];
		result = unescape(result);
		
		// TODO: Error Check
		this.accessToken = result.split("&")[0].split("=")[1];		
		//this.expiresIn = result.split("&")[1].split("=")[1];
        
        
		window.plugins.childBrowser.close();
		this.onConnect();
		
	}
    else if (newLoc.indexOf("paypal_failure")!=-1){
		alert("Payment was canceled");
        window.plugins.childBrowser.close();
	} else if (newLoc.indexOf("paypal_success")!=-1){
		args=newLoc.replace(/.*?[?]/,"");
		alert("Payment ok!"+args);
        window.plugins.childBrowser.close();
	} */
	
	
	 // console.log("location changed-------------:"+newLoc);
    var args;
    if(newLoc.indexOf(this.redirect_uri) == 0)
	{
		var result = unescape(newLoc).split("#")[1];
		result = unescape(result);
		
		// TODO: Error Check
		this.accessToken = result.split("&")[0].split("=")[1];		
		//this.expiresIn = result.split("&")[1].split("=")[1];
        
        
		window.plugins.childBrowser.close();
		this.onConnect();
		
	}
    else if (newLoc.indexOf("paypal_mobile_response_failure")!=-1){
		alert("Payment was canceled");
        window.plugins.childBrowser.close();
        $.mobile.changePage("browsecategories.html", {
                            transition: "none",
                            reverse: true,
                            changeHash: false
                            });
        
	} else if (newLoc.indexOf("paypal_mobile_response_success")!=-1){
        //for successfull response
        
    //http://122.169.249.250:8097/paypal_controller/paypal_mobile_response_success?transaction_id=09Y31938SU2124223
        
       // for failure
            
          //  http://122.169.249.250:8097/paypal_controller/paypal_mobile_response_failure
        
        
		args=newLoc.replace(/.*?[?]/,"");
        
		//alert("Payment ok!"+args);
        var splite_args=args.split('=');
        window.plugins.childBrowser.close();
        sessionStorage.setItem('conformation_code',splite_args[1]);
        $.mobile.changePage("booking_confirmed.html", {
         transition: "none",
         reverse: true,
         changeHash: false
         }); 
        
	}
	
	
	
}

/********************************************************************************************
 * 
 * @Desc  The below functionality to perform paypal payment process on childbrowser.
 * 
 * *******************************************************************************************
 */

FBConnect.prototype.payPalConnect = function()
{
    var userId=sessionStorage.getItem('userId');
    
    var conformation_code=sessionStorage.getItem('conformation_code');
    var  url="http://docs.google.com/gview?embedded=true&url="+String(window.httpclient.getBaseURL())+'/paypal_controller/call_paypal?confirmation='+conformation_code+'&user='+userId+'_User&resource_from=mobile&access_type=mobile&version=v1.5';
    console.log(url);
    window.plugins.childBrowser.showWebPage(url);
    var self = this;
    window.plugins.childBrowser.onLocationChange = function(loc){self.onLocationChange(loc);};
}




FBConnect.prototype.getMe = function()
{
    //alert("acc token for me="+this.accessToken);
    if(this.accessToken=='undefined')
    {
         alert("Please Sign in..");
    }
    else
    {
  	var url = "https://graph.facebook.com/me?access_token=" + this.accessToken;
	var req = new XMLHttpRequest();
	
	req.open("get",url,true);
	req.send(null);
	req.onerror = function(){alert("Error");};
	return req;
    }
}
FBConnect.prototype.getFriends = function()
{
    if(this.accessToken=='undefined')
    {
        alert("Please Sign in..");
    }
    else
    {
            
     // alert("acc token for frnds="+this.accessToken);
	var url = "https://graph.facebook.com/me/friends?access_token=" + this.accessToken;
	var req = new XMLHttpRequest();
	
	req.open("get",url,true);
	req.send(null);
	req.onerror = function(){alert("Error");};
	return req;
    }
}

// Note: this plugin does NOT install itself, call this method some time after deviceready to install it
// it will be returned, and also available globally from window.plugins.fbConnect
FBConnect.install = function()
{
	if(!window.plugins)
	{
		window.plugins = {};	
	}
	window.plugins.fbConnect = new FBConnect();
	return window.plugins.fbConnect;
}
/*function fbPost() {
    alert("fbpost");
    $.ajax({
           type: 'POST',
           url: "https://graph.facebook.com/me/feed",
           data: {
           message: "TESTING",
           PICTURE: "<IMAGE URL>",
           name: "<TITLE OF POST>",
           link: "<LINK TO APP>",
           caption: "<SHOWN BELOW TITLE>",
           description: "<SHOWN BELOW CAPTION>",
           access_token:this.access_token,
           format: "json"
           },
           success: function (data) {
           navigator.notification.alert("success!", null, "Thanks!")
           },dataType: "json",
           
           });
}*/

FBConnect.prototype.postFBWall = function(message, urlPost, urlPicture, callBack)
{
      console.log('inside postFBWall '+message + ' urlPost='+urlPost + ' urlPicture='+urlPicture);
    
    
    var url = 'https://graph.facebook.com/me/feed?access_token=' + this.accessToken+'&message='+message;
    
    
    if (urlPost) {
        
        url += '&link='+encodeURIComponent(urlPost);
        
    }
    
    if (urlPicture) {
        
        url += '&picture='+encodeURIComponent(urlPicture);
        
    }
    
    
    var req = this.postFBGraph(url);
    
    
    req.onload = callBack;
    
}


FBConnect.prototype.postFBGraph = function(url)

{
    
    console.log('inside postFBGraph url='+url);
    
    
    var req = new XMLHttpRequest();
    
    req.open("POST", url, true);
    
    /*req.onreadystatechange = function() {//Call a function when the state
     
     if(req.readyState == 4 && req.status == 200) {
     
     alert(req.responseText);
     
     }
     
     };*/
    
    
    req.send(null);
    
    return req;
    
}

//You also need to customize the connect function to ask the authorization to post to the user's wall :

FBConnect.prototype.connect =
function(client_id,redirect_uri,display)

{
    
    this.client_id = client_id;
    
    this.redirect_uri = redirect_uri;
    
    
    
    var authorize_url  = "https://graph.facebook.com/oauth/authorize?";
    
    authorize_url += "client_id=" +
    client_id;
    
    authorize_url += "&redirect_uri=" +
    redirect_uri;
    
    authorize_url += "&display="+ ( display ?
                                   display : "touch" );
    
    authorize_url += "&type=user_agent";
    
    //if you want to post message on the wall : publish_stream, offline_access,
    
    authorize_url +="&scope=publish_stream";
    
    
    
    window.plugins.childBrowser.showWebPage(authorize_url);
    
    var self = this;
    
    window.plugins.childBrowser.onLocationChange =
    function(loc){self.onLocationChange(loc);};
    
}
FBConnect.prototype.Logout = function() { window.plugins.childBrowser.logout();}