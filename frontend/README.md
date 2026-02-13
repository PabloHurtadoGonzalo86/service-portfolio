# Service Portfolio Frontend

Modern, responsive web application for generating and sharing developer portfolios from GitHub profiles.

## Features

- 🚀 **React 19** with TypeScript for type safety
- ⚡ **Vite** for lightning-fast development
- 🎨 **SCSS Modules** for scoped, maintainable styles
- 📱 **Mobile-first responsive design**
- 🔍 **SEO optimized** with dynamic meta tags and OpenGraph support
- 🌐 **Client-side routing** with React Router 7
- 🔗 **Portfolio sharing** via public URLs
- ♿ **Accessible** UI components

## Getting Started

### Prerequisites

- Node.js 18+ (24 recommended)
- npm or yarn

### Installation

```bash
npm install
```

### Development

```bash
npm run dev
```

Open [http://localhost:5173](http://localhost:5173) in your browser.

### Build

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

## Environment Variables

Create a `.env` file in the root directory:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── Button/
│   ├── Input/
│   ├── LoadingSpinner/
│   ├── ErrorMessage/
│   ├── PortfolioView/
│   └── SEO/
├── pages/              # Page components
│   ├── Home/           # GitHub username input
│   ├── Portfolio/      # Public portfolio view
│   └── NotFound/       # 404 page
├── services/           # API client
│   └── api.ts
├── hooks/              # Custom React hooks
│   └── usePortfolioGenerator.ts
├── types/              # TypeScript types
│   └── portfolio.ts
├── styles/             # Global styles and variables
│   ├── _variables.scss
│   └── global.scss
└── App.tsx             # Main app with routing
```

## Features

### Portfolio Generation

1. Enter a GitHub username
2. The app sends a request to the backend API
3. AI analyzes all public repositories
4. Generates a professional portfolio with:
   - Professional summary
   - Top skills
   - Featured projects
   - Skills by category
   - Profile highlights

### Portfolio Sharing

- Each generated portfolio gets a unique shareable URL
- SEO optimized with meta tags for social media sharing
- Native share API support with fallback to clipboard

### Responsive Design

- Mobile-first approach
- Breakpoints: 640px (mobile), 768px (tablet), 1024px (desktop)
- Optimized layouts for all screen sizes

## Tech Stack

- **React 19** - Latest React features
- **Vite 7** - Next generation frontend tooling
- **TypeScript** - Type safety
- **React Router 7** - Client-side routing
- **SCSS** - Advanced styling with variables and mixins
- **Nginx** - Production web server (Docker)

## Docker

Build and run with Docker:

```bash
docker build -t service-portfolio-frontend .
docker run -p 80:80 service-portfolio-frontend
```

## Deployment

The frontend is deployed to Kubernetes using the manifest in `../k8s/frontend-deployment.yml`.

The CI/CD pipeline automatically:
1. Builds the Docker image on push to main
2. Pushes to GitHub Container Registry
3. Keel auto-deploys to the cluster

**Live URL:** https://portfolio.pablohgdev.com

## Contributing

1. Create a feature branch
2. Make your changes
3. Run `npm run build` to ensure it builds
4. Submit a pull request

## License

MIT
