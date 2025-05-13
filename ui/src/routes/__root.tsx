import { Outlet, createRootRoute } from '@tanstack/react-router'
import { ThemeProvider } from '../components/ThemeProvider'
import { ThemeToggle } from '../components/ThemeToggle'

export const Route = createRootRoute({
  component: () => (
    <ThemeProvider>
      <ThemeToggle />
      <Outlet />
    </ThemeProvider>
  ),
})
