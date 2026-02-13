import type { Portfolio } from '../../types/portfolio';
import styles from './PortfolioView.module.scss';

interface PortfolioViewProps {
  portfolio: Portfolio;
}

export function PortfolioView({ portfolio }: PortfolioViewProps) {
  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1 className={styles.name}>{portfolio.developerName}</h1>
        <p className={styles.username}>@{portfolio.githubUsername}</p>
        <p className={styles.summary}>{portfolio.professionalSummary}</p>
      </header>

      {portfolio.topSkills.length > 0 && (
        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>Top Skills</h2>
          <div className={styles.skillTags}>
            {portfolio.topSkills.map((skill) => (
              <span key={skill} className={styles.skillTag}>
                {skill}
              </span>
            ))}
          </div>
        </section>
      )}

      {portfolio.profileHighlights.length > 0 && (
        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>Highlights</h2>
          <ul className={styles.highlights}>
            {portfolio.profileHighlights.map((highlight, index) => (
              <li key={index} className={styles.highlight}>
                {highlight}
              </li>
            ))}
          </ul>
        </section>
      )}

      {portfolio.selectedProjects.length > 0 && (
        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>Featured Projects</h2>
          <div className={styles.projects}>
            {portfolio.selectedProjects.map((project) => (
              <article key={project.repoName} className={styles.project}>
                <div className={styles.projectHeader}>
                  <h3 className={styles.projectName}>
                    <a
                      href={project.repoUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className={styles.projectLink}
                    >
                      {project.repoName}
                    </a>
                  </h3>
                  {project.category && (
                    <span className={styles.category}>{project.category}</span>
                  )}
                </div>
                <p className={styles.projectDescription}>{project.description}</p>
                {project.whyNotable && (
                  <p className={styles.notable}>
                    <strong>Why notable:</strong> {project.whyNotable}
                  </p>
                )}
                {project.techStack.length > 0 && (
                  <div className={styles.techStack}>
                    {project.techStack.map((tech) => (
                      <span key={tech} className={styles.tech}>
                        {tech}
                      </span>
                    ))}
                  </div>
                )}
              </article>
            ))}
          </div>
        </section>
      )}

      {Object.keys(portfolio.skillsByCategory).length > 0 && (
        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>Skills by Category</h2>
          <div className={styles.skillCategories}>
            {Object.entries(portfolio.skillsByCategory).map(([category, skills]) => (
              <div key={category} className={styles.skillCategory}>
                <h3 className={styles.categoryTitle}>{category}</h3>
                <div className={styles.categorySkills}>
                  {skills.map((skill) => (
                    <span key={skill} className={styles.categorySkill}>
                      {skill}
                    </span>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </section>
      )}

      <footer className={styles.footer}>
        <p className={styles.footerText}>
          Total Public Repositories: {portfolio.totalPublicRepos}
        </p>
        <p className={styles.footerText}>
          Generated: {new Date(portfolio.createdAt).toLocaleDateString()}
        </p>
      </footer>
    </div>
  );
}
