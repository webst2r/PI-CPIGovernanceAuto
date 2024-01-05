import {environment} from "../environments/environment";

export class AppConstant {
  public static readonly API_URL = environment.apiUrl;

  public static readonly API_PATHS = {
    AUTH: {
      LOGIN: 'auth/authenticate',
      REGISTER: 'auth/register',
    },
    FLOW_DETAILS: {
      GET_FLOW: 'api/packages/getFlow',
    },
    CREDENTIALS: {
      SAP_CPI: {
        CREATE: 'credentials/sap_cpi/save',
        UPDATE: 'credentials/sap_cpi/update',
        GET:'credentials/sap_cpi/key',
        DELETE:'credentials/sap_cpi/delete'
      },
      GITHUB:{
        CREATE: 'credentials/github/save',
        UPDATE: 'credentials/github/update',
        GET:'credentials/github/key',
        DELETE:'credentials/github/delete'
      },
      JENKINS:{
        CREATE: 'credentials/jenkins/save',
        UPDATE: 'credentials/jenkins/update',
        GET:'credentials/jenkins/key',
        DELETE:'credentials/jenkins/delete'
      }
    },
    REPOSITORIES: {
      GITHUB:{
        CREATE: 'repositories/github/save',
        UPDATE: 'repositories/github/update',
        GET:'repositories/github/key',
        DELETE:'repositories/github/delete',
        BRANCHES: 'repositories/github/branches'
      }
    }
  }

  public static readonly REGEX = {
    email: /^[_a-z0-9-+]+(\.[_a-z0-9]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/,
    password: /^(?=.*\d)(?=.*[A-Za-z])(?=.*[a-zA-Z]).{8,}$/,
    base_url_SAP_CPI: /^(ftp|http|https):\/\/[^ "]+$/,
    token_url_SAP_CPI: /^(ftp|http|https):\/\/[^ "]+$/
  }
}
