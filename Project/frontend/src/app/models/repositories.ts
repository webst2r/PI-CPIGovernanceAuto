export interface GithubRepository {
  id: number;
  name: string;
  mainBranch: string;
  secondaryBranches: string[];
  username: string;
  accessToken: string;
}

export type GithubRepositoryUpdateRequest = Omit<GithubRepository, "createdAt">
export type GithubRepositoryCreateRequest = Omit<GithubRepository, "id" | "createdAt">

