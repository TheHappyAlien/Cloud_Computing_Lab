import {
    CognitoUserPool,
    CognitoUserSession,
    AuthenticationDetails,
    CognitoUser,
  } from "amazon-cognito-identity-js";

   import "./aws_exports"
import { awsCogitoCredentials } from "./aws_exports";
  
  const poolData = {
    UserPoolId: awsCogitoCredentials.USER_POOL_ID,
    ClientId: awsCogitoCredentials.CLINET_ID,
  };
  
  const userPool = new CognitoUserPool(poolData);
  
  export function loginCogito(
    email: string,
    password: string
  ): Promise<CognitoUserSession> {

    const authenticationData = {
      Username: email,
      Password: password,
    };
    const authenticationDetails = new AuthenticationDetails(authenticationData);
    const userData = {
      Username: email,
      Pool: userPool,
    };
    const cognitoUser = new CognitoUser(userData);

    return new Promise((resolve, reject) => {
      cognitoUser.authenticateUser(authenticationDetails, {
        onSuccess: (result: any) => {
          resolve(result);
        },
        onFailure: (err: any) => {
          reject(err);
        },
      });
    });
  }
  
  export function register(email: string, password: string) {
    return new Promise((resolve, reject) => {
      userPool.signUp(email, password, [], [], (err: any, data: any) => {
        if (err) {
          console.log("err", err);
          reject(err);
        } else {
          resolve(data?.user);
        }
      });
    });
  }
  
  export function logout() {
    const cognitoUser = userPool.getCurrentUser();
    cognitoUser?.signOut();
    return Promise.resolve();
  }