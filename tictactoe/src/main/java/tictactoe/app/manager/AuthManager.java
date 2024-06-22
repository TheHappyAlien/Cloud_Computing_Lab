package tictactoe.app.manager;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;

public class AuthManager {

    private static final String REGION = "us-east-1";

    public static Boolean UserAuthorized(String token) {

        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
                .withRegion(REGION)
                .build();
        
        GetUserRequest getUserRequest = new GetUserRequest()
                .withAccessToken(token);

        try{
            cognitoIdentityProvider.getUser(getUserRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
       
        return true;
    }
}
