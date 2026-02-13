export interface PortfolioProject {
  repoName: string;
  repoUrl: string;
  description: string;
  techStack: string[];
  whyNotable: string;
  category: string;
}

export interface Portfolio {
  id: number;
  githubUsername: string;
  developerName: string;
  professionalSummary: string;
  topSkills: string[];
  selectedProjects: PortfolioProject[];
  skillsByCategory: Record<string, string[]>;
  profileHighlights: string[];
  totalPublicRepos: number;
  createdAt: string;
}

export interface PortfolioSummary {
  id: number;
  githubUsername: string;
  developerName: string;
  createdAt: string;
}

export interface GeneratePortfolioRequest {
  githubUsername: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
