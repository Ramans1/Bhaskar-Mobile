
var client_id = "442919192462695";//appid

//489315811082220
var redir_url = "https://www.facebook.com/connect/login_success.html";//redirect url

//facebook login functionality implements here(profile.html)
function facebookLogin()
{
    alert("facebook");
    var fb = FBConnect.install();
    fb.connect(client_id,redir_url,"touch");
    fb.onConnect = onFBConnected;
    
}
// Triggers when user logged in successfully with fb.
function onFBConnected()
{
   // $.mobile.showPageLoadingMsg();
    
    //create request for retrive logged in user details
    var req = window.plugins.fbConnect.getMe();
    req.onload = checkfacebookid1;
    
}

//checking fb email id is already exists or not
function checkfacebookid1(evt) {
   // $.mobile.showPageLoadingMsg();
    
    var response = JSON.parse(evt.target.responseText);
    alert(evt.target.responseText);
    
}

//facebook logout functionality implements here(index.html)
function Logout(){ 
    var fb=FBConnect.install();
    fb.Logout();   
}