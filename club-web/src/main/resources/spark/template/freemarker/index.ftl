<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Club Login</title>
    <style type="text/css">
        .style1{text-align: justify;}
    </style>
</head>
<body>
    <div id="main">
        <div class="alert alert-info">
        	<strong>Step 1&nbsp;:&nbsp;</strong>
        	Choose which provider you wish to use to login.
        </div>
        <div>
	       	<table cellpadding="10" cellspacing="10" align="center">

				<tr>
					<td>
						<a href="socialauth?id=facebook"><img src="images/facebook_icon.png" alt="Facebook" title="Facebook" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=twitter"><img src="images/twitter_icon.png" alt="Twitter" title="Twitter" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=google"><img src="images/gmail-icon.jpg" alt="Gmail" title="Gmail" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=yahoo"><img src="images/yahoomail_icon.jpg" alt="YahooMail" title="YahooMail" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=hotmail"><img src="images/hotmail.jpeg" alt="HotMail" title="HotMail" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=linkedin"><img src="images/linkedin.gif" alt="Linked In" title="Linked In" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=foursquare"><img src="images/foursquare.jpeg" alt="FourSquare" title="FourSquare" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=myspace"><img src="images/myspace.jpeg" alt="MySpace" title="MySpace" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=mendeley"><img src="images/mendeley.jpg" alt="Mendeley" title="Mendeley" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=yammer"><img src="images/yammer.jpg" alt="Yammer" title="Yammer" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=googleplus"><img src="images/googleplus.png" alt="Google Plus" title="Google Plus" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=instagram"><img src="images/instagram.png" alt="Instagram" title="Instagram" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=flickr"><img src="images/flickr_icon.jpg" alt="Flickr" title="Flickr" border="0"></img></a>
					</td>
					<td>
						<a href="socialauth?id=github"><img src="images/github.png" alt="GITHub" title="GITHub" border="0"></img></a>
					</td>
				</tr>
			</table>
           	<br />
	        <br />
	        <br />
	        <br />
	        <br />
	        <br />
	        <br />
	        <br />
	        <br />
             <p class="additional">
            </p>
        </div>
    </div>


	<fieldset>
		<legend>Login with OpenID</legend>
		<form action="consumer" method="post">
			<div>
				<img src="http://wiki.openid.net/f/openid-16x16.gif"/>
				<input type="text" name="openid_identifier"  style="width: 50em; margin: 5px;"/>
			</div>
			<div>
				<button type="submit" name="login">Login</button>
			</div>
		</form>
	</fieldset>
</body>
</html>