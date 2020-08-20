# Gmail-api example with spring boot and oauth2
Gmail-api-example provides sample code that uses google's gmail api along with google client api to send email using your gmail account.

Before using the sample, the application needs google api credentials along with refresh token from google, so that application does not need to follow the oauth2 consent flow more than once.

## To Get API credentails with refresh token from google
https://developers.google.com/identity/protocols/oauth2/web-server?authuser=1#prerequisites

N.B make sure the "access_type" is set to "offline" while making authorization request 

## After getting the api credentials and refresh token
provide the values for the properties in application.properties
```
google.api.client.id=<client id>
google.api.client.secret=<client secret>
google.api.refreshToken=<refresh token>
google.api.token.server.url=https://oauth2.googleapis.com/token
google.api.userid=<google user id>
google.api.application.name=<Application name>
```

## Run the application
Run the application and go to http://localhost:8080/mail endpoint to send email