<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <LINK href="/login.css" rel="stylesheet" type="text/css">
    <title>Club Login</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <div id="main">
        <div class="alert alert-info">
        	<strong>Step 1&nbsp;:&nbsp;</strong>
        	Choose which provider you wish to use to login.
        </div>
        <div>
	       	<div class="provider">
                <a href="socialauth?id=facebook"><img src="images/facebook_icon.png" alt="Facebook" title="Facebook" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=twitter"><img src="images/twitter_icon.png" alt="Twitter" title="Twitter" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=google"><img src="images/gmail-icon.jpg" alt="Gmail" title="Gmail" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=yahoo"><img src="images/yahoomail_icon.jpg" alt="YahooMail" title="YahooMail" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=hotmail"><img src="images/hotmail.jpeg" alt="HotMail" title="HotMail" border="0"/></a>
            </div>

            <div class="provider">
                <a href="socialauth?id=linkedin"><img src="images/linkedin.gif" alt="Linked In" title="Linked In" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=foursquare"><img src="images/foursquare.jpeg" alt="FourSquare" title="FourSquare" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=myspace"><img src="images/myspace.jpeg" alt="MySpace" title="MySpace" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=mendeley"><img src="images/mendeley.jpg" alt="Mendeley" title="Mendeley" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=yammer"><img src="images/yammer.jpg" alt="Yammer" title="Yammer" border="0"/></a>
            </div>

            <div class="provider">
                <a href="socialauth?id=googleplus"><img src="images/googleplus.png" alt="Google Plus" title="Google Plus" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=instagram"><img src="images/instagram.png" alt="Instagram" title="Instagram" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=flickr"><img src="images/flickr_icon.jpg" alt="Flickr" title="Flickr" border="0"/></a>
            </div>
            <div class="provider">
                <a href="socialauth?id=github"><img src="images/github.png" alt="GITHub" title="GITHub" border="0"/></a>
            </div>
        </div>
    </div>

    <br/>
    <br/>

	<fieldset>
		<legend for="openid_identifier">Login with OpenID</legend>
		<form action="consumer" method="post">

            <div class="inputField">
                <label for="openid_identifier"><img src="http://wiki.openid.net/f/openid-16x16.gif"/></label>
				<input type="text" name="openid_identifier" id="openid_identifier" style="width: calc(100% - 30px);"/>
			</div>
			<div class="action">
				<button type="submit" name="login">Login</button>
			</div>
		</form>
	</fieldset>
</body>
</html>