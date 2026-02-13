import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { portfolioApi } from '../../services/api';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorMessage } from '../../components/ErrorMessage';
import { PortfolioView } from '../../components/PortfolioView';
import { SEO } from '../../components/SEO';
import { Button } from '../../components/Button';
import type { Portfolio } from '../../types/portfolio';
import styles from './PortfolioPage.module.scss';

export function PortfolioPage() {
  const { id } = useParams<{ id: string }>();
  const [portfolio, setPortfolio] = useState<Portfolio | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchPortfolio = async () => {
      if (!id) return;
      
      setLoading(true);
      setError(null);

      try {
        const data = await portfolioApi.getPortfolio(Number(id));
        setPortfolio(data);
      } catch (err: any) {
        setError(err.message || 'Failed to load portfolio');
      } finally {
        setLoading(false);
      }
    };

    fetchPortfolio();
  }, [id]);

  const handleShare = async () => {
    const url = window.location.href;
    
    if (navigator.share) {
      try {
        await navigator.share({
          title: `${portfolio?.developerName}'s Portfolio`,
          text: portfolio?.professionalSummary,
          url,
        });
      } catch (err) {
        console.error('Error sharing:', err);
      }
    } else {
      try {
        await navigator.clipboard.writeText(url);
        alert('Link copied to clipboard!');
      } catch (err) {
        console.error('Error copying to clipboard:', err);
      }
    }
  };

  if (loading) {
    return (
      <div className={styles.container}>
        <LoadingSpinner size="lg" message="Loading portfolio..." />
      </div>
    );
  }

  if (error || !portfolio) {
    return (
      <div className={styles.container}>
        <ErrorMessage message={error || 'Portfolio not found'} />
        <div className={styles.actions}>
          <Link to="/">
            <Button variant="primary">Go Home</Button>
          </Link>
        </div>
      </div>
    );
  }

  const portfolioUrl = window.location.href;
  const canonicalUrl = portfolioUrl;

  return (
    <>
      <SEO
        title={`${portfolio.developerName} - Developer Portfolio`}
        description={portfolio.professionalSummary}
        canonicalUrl={canonicalUrl}
        ogType="profile"
      />
      
      <div className={styles.container}>
        <div className={styles.toolbar}>
          <Link to="/">
            <Button variant="outline" size="sm">← Back to Home</Button>
          </Link>
          <Button onClick={handleShare} variant="primary" size="sm">
            Share Portfolio
          </Button>
        </div>
        
        <PortfolioView portfolio={portfolio} />
      </div>
    </>
  );
}
