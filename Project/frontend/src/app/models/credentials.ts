export interface SapCpiCredentials {
  id: number;
  name: string;
  baseUrl: string;
  tokenUrl: string;
  clientId: string;
  clientSecret: string;
  createdAt: string;
}

export interface GithubCredentials {
  id: number;
  name: string;
  username: string;
  accessToken: string;
}

export interface JenkinsCredentials {
  id: number;
  name: string;
  username: string;
  accessToken: string;
}

export type SapCpiCredentialsUpdateRequest = Omit<SapCpiCredentials, "createdAt">
export type SapCpiCredentialsCreateRequest = Omit<SapCpiCredentials, "id" | "createdAt">

export type GithubCredentialsUpdateRequest = Omit<GithubCredentials, "createdAt">
export type GithubCredentialsCreateRequest = Omit<GithubCredentials, "id" | "createdAt">

export type JenkinsCredentialsUpdateRequest = Omit<JenkinsCredentials, "createdAt">
export type JenkinsCredentialsCreateRequest = Omit<JenkinsCredentials, "id" | "createdAt">
