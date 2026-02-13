import { useState } from 'react';
import { portfolioApi, ApiError } from '../services/api';
import type { Portfolio } from '../types/portfolio';

interface UsePortfolioGeneratorResult {
  portfolio: Portfolio | null;
  loading: boolean;
  error: string | null;
  generatePortfolio: (username: string) => Promise<void>;
  clearError: () => void;
}

export function usePortfolioGenerator(): UsePortfolioGeneratorResult {
  const [portfolio, setPortfolio] = useState<Portfolio | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const generatePortfolio = async (username: string) => {
    setLoading(true);
    setError(null);
    setPortfolio(null);

    try {
      const result = await portfolioApi.generatePortfolio(username);
      setPortfolio(result);
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const clearError = () => setError(null);

  return {
    portfolio,
    loading,
    error,
    generatePortfolio,
    clearError,
  };
}
