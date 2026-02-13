import { useId } from 'react';
import styles from './Input.module.scss';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  fullWidth?: boolean;
}

export function Input({
  label,
  error,
  fullWidth = false,
  className = '',
  id,
  ...props
}: InputProps) {
  const generatedId = useId();
  const inputId = id || generatedId;
  
  const classes = [
    styles.input,
    error && styles.error,
    fullWidth && styles.fullWidth,
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <div className={fullWidth ? styles.fullWidth : ''}>
      {label && (
        <label htmlFor={inputId} className={styles.label}>
          {label}
        </label>
      )}
      <input id={inputId} className={classes} {...props} />
      {error && <span className={styles.errorMessage}>{error}</span>}
    </div>
  );
}
