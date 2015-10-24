Create a file with the name singlessl.config.

It's contents should be:

Resources:
  sslSecurityGroupIngress:
    Type: AWS::EC2::SecurityGroupIngress
    Properties:
      GroupName: {"Ref" : "AWSEBSecurityGroup"}
      # GroupId: {"Ref" : "AWSEBSecurityGroup"}
      IpProtocol: tcp
      ToPort: 443
      FromPort: 443
      CidrIp: 0.0.0.0/0

packages:
  yum:
    mod_ssl : []

files:
  /etc/httpd/conf.d/ssl.conf:
    mode: "000755"
    owner: root
    group: root
    content: |
      LoadModule ssl_module modules/mod_ssl.so
      Listen 443
      <VirtualHost *:443>
        <Proxy *>
          Order deny,allow
          Allow from all
        </Proxy>

        SSLEngine             on
        SSLCertificateFile    "/etc/pki/tls/certs/server.crt"
        SSLCertificateKeyFile "/etc/pki/tls/certs/server.key"
        SSLCipherSuite        EECDH+AESGCM:EDH+AESGCM:AES256+EECDH:AES256+EDH
        SSLProtocol           All -SSLv2 -SSLv3
        SSLHonorCipherOrder   On

        Header always set Strict-Transport-Security "max-age=63072000; includeSubdomains; preload"
        Header always set X-Frame-Options DENY
        Header always set X-Content-Type-Options nosniff

        ProxyPass / http://localhost:8080/ retry=0
        ProxyPassReverse / http://localhost:8080/
        ProxyPreserveHost on

        RewriteEngine On
        RewriteCond %{HTTPS} !=on
        RewriteCond %{HTTP:X-Forwarded-Proto} !https [NC]
        RewriteRule ^ https://%{HTTP_HOST}%{REQUEST_URI} [L,R=301]

        LogFormat "%h (%{X-Forwarded-For}i) %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\""
        ErrorLog /var/log/httpd/elasticbeanstalk-error_log
        TransferLog /var/log/httpd/elasticbeanstalk-access_log
      </VirtualHost>

  /etc/pki/tls/certs/server.crt:
    mode: "000400"
    owner: root
    group: root
    content: |
      -----BEGIN CERTIFICATE-----
      put your ssl certificate here
      -----END CERTIFICATE-----

  /etc/pki/tls/certs/server.key:
    mode: "000400"
    owner: root
    group: root
    content: |
      -----BEGIN RSA PRIVATE KEY-----
      put your private key here
      -----END RSA PRIVATE KEY-----

  /etc/pki/tls/certs/gd_bundle.crt:
    mode: "000400"
    owner: root
    group: root
    content: |
      -----BEGIN CERTIFICATE-----
      put your certificate chain here
      -----END CERTIFICATE-----


services:
  sysvinit:
    httpd:
      enabled: true
      ensureRunning: true
      files : [/etc/httpd/conf.d/ssl.conf,/etc/pki/tls/certs/server.key,/etc/pki/tls/certs/server.crt,/etc/pki/tls/certs/gd_bundle.crt]


