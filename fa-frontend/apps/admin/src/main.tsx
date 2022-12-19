import { Suspense } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter as Router, useRoutes } from 'react-router-dom';

import './globals.scss';

import routes from '~react-pages';
import { PageLoading } from '@fa/ui';

// fontawesome icon
import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';

library.add(fas);

// console.log(routes);

function App() {
  return <Suspense fallback={<PageLoading />}>{useRoutes(routes)}</Suspense>;
}

const app = createRoot(document.getElementById('app')!);

app.render(
  <Router>
    <App />
  </Router>,
);
