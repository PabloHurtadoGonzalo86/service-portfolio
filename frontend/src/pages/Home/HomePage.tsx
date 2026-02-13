import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Input } from '../../components/Input';
import { Button } from '../../components/Button';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorMessage } from '../../components/ErrorMessage';
import { PortfolioView } from '../../components/PortfolioView';
import { SEO } from '../../components/SEO';
import { usePortfolioGenerator } from '../../hooks/usePortfolioGenerator';
import styles from './HomePage.module.scss';

export function HomePage() {
  const [username, setUsername] = useState('');
  const navigate = useNavigate();
  const { portfolio, loading, error, generatePortfolio, clearError } = usePortfolioGenerator();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim()) return;
    
    await generatePortfolio(username.trim());
  };

  const handleShare = () => {
    if (portfolio) {
      navigate(`/portfolio/${portfolio.id}`);
    }
  };

  const handleReset = () => {
    setUsername('');
    clearError();
  };

  return (
    <>
      <SEO
        title="Service Portfolio - Generate Your Developer Portfolio"
        description="Generate a professional developer portfolio from your GitHub profile using AI. Showcase your best projects, skills, and achievements."
      />
      
      <div className={styles.container}>
        <header className={styles.hero}>
          <h1 className={styles.title}>Generate Your Developer Portfolio</h1>
          <p className={styles.subtitle}>
            Transform your GitHub profile into a professional portfolio with AI
          </p>
        </header>

        {!portfolio && (
          <div className={styles.formContainer}>
            <form onSubmit={handleSubmit} className={styles.form}>
              <Input
                type="text"
                placeholder="Enter GitHub username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={loading}
                fullWidth
                label="GitHub Username"
              />
              <Button
                type="submit"
                loading={loading}
                disabled={!username.trim() || loading}
                size="lg"
                fullWidth
              >
                Generate Portfolio
              </Button>
            </form>
            
            {error && <ErrorMessage message={error} onRetry={handleReset} />}
            
            {loading && (
              <LoadingSpinner
                size="lg"
                message="Analyzing your GitHub repositories and generating your portfolio..."
              />
            )}
          </div>
        )}

        {portfolio && !loading && (
          <div className={styles.portfolioContainer}>
            <div className={styles.actions}>
              <Button onClick={handleShare} variant="primary">
                Share Portfolio
              </Button>
              <Button onClick={handleReset} variant="outline">
                Generate Another
              </Button>
            </div>
            
            <PortfolioView portfolio={portfolio} />
          </div>
        )}
      </div>
    </>
  );
}
