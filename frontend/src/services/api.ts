import type { Portfolio, PortfolioSummary, GeneratePortfolioRequest } from '../types/portfolio';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

class ApiError extends Error {
  status: number;
  data?: unknown;
  
  constructor(status: number, message: string, data?: unknown) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new ApiError(
      response.status,
      errorData.message || response.statusText,
      errorData
    );
  }
  return response.json();
}

export const portfolioApi = {
  async generatePortfolio(username: string): Promise<Portfolio> {
    const response = await fetch(`${API_BASE_URL}/portfolio/generate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ githubUsername: username } as GeneratePortfolioRequest),
    });
    return handleResponse<Portfolio>(response);
  },

  async getPortfolio(id: number): Promise<Portfolio> {
    const response = await fetch(`${API_BASE_URL}/portfolio/${id}`);
    return handleResponse<Portfolio>(response);
  },

  async listPortfolios(): Promise<PortfolioSummary[]> {
    const response = await fetch(`${API_BASE_URL}/portfolio`);
    return handleResponse<PortfolioSummary[]>(response);
  },
};

export { ApiError };
