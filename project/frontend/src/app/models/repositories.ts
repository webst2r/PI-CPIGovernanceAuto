import {GithubCredentials} from "./credentials";

export interface GithubRepository {
  id: number;
  name: string;
  mainBranch: string;
  secondaryBranches: string[];
  githubCredentials: GithubCredentials;
}

export type GithubRepositoryUpdateRequest = Omit<GithubRepository, "id" | "createdAt">;
export type GithubRepositoryCreateRequest = Omit<GithubRepository, "id" | "createdAt">;
