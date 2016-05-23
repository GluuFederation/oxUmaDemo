# oxUmaDemo
UMA Demo - demonstrates Gluu Server UMA feature.

Please check both LIVE sample RP (https://kantara.gluu.org/rp/rp.html) and RS (https://kantara.gluu.org/rs/rs.html).

# References
- [UMA Demo Video](http://www.gluu.co/uma-demo-video)
- [UMA Requesting Party Sample Live](https://kantara.gluu.org/rp/rp.html)
- [UMA Resource Server Sample Live](https://kantara.gluu.org/rs/rs.html)
- [Gluu Server](http://gluu.org)

# Local configuration

In case local installation is indeed please note:

- Configurations for both RP and RS are loaded from <TOMCAT_HOME>/conf directory.
- Lets assume that CE Server is installed on https://uma-as.com 
- Make sure you've deployed RP to tomcat/webapps/rp (https://uma-as.com/rp, it can be also totally another host https://uma-rp.com/rp )
- Make sure you've deployed RS to tomcat/webapps/rs (https://uma-as.com/rs, it can be also totally another host https://uma-rs.com/rs)

If these three steps are done correctly then it's as easy to configure as described below :) :

## Resource Server

Configure <TOMCAT_HOME>/conf/rs-protect.json (and optionally <TOMCAT_HOME>/conf/rs-protect-config.json if you modified RS WS endpoints, if not modified then leave it unchanged).

For rs-protect.json specify PAT client credentials: For this login to your server (https://uma-as.com) and add new client with parameters (for this select OpendId Configuration -> Clients -> Add client):

- Name : oxUma Demo RS (or any other name you like)
- Application type : web
- Authentication method for the Token Endpoint: client_secret_basic
- Redirect Login URIs: <your rs redirect uri> (https://uma-as.com/rs/rs.html)
- scopes: openid and uma_protection (uma_protection indicates that it is PAT)
- Response types: token code id_token

Now populate rs-protect.json with client id and secret you've just added.

Next register client for RP (AAT) with difference :

- scopes openid and uma_authorization (uma_authorization indicates that it is AAT)
- Redirect Login URIs: <your rp redirect uri> (https://uma-as.com/rp)

Now populate <TOMCAT_HOME>/conf/oxuma-rp-conf.json with client id and secret you've just added.

(Don't forget restart tomcat after changes in configurations to force reload them.)



