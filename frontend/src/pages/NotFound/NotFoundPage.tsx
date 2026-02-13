import { Link } from 'react-router-dom';
import { Button } from '../../components/Button';
import { SEO } from '../../components/SEO';
import styles from './NotFoundPage.module.scss';

export function NotFoundPage() {
  return (
    <>
      <SEO
        title="404 - Page Not Found"
        description="The page you're looking for doesn't exist."
      />
      
      <div className={styles.container}>
        <div className={styles.content}>
          <h1 className={styles.title}>404</h1>
          <h2 className={styles.subtitle}>Page Not Found</h2>
          <p className={styles.text}>
            The page you're looking for doesn't exist or has been moved.
          </p>
          <Link to="/">
            <Button variant="primary" size="lg">
              Go Home
            </Button>
          </Link>
        </div>
      </div>
    </>
  );
}
