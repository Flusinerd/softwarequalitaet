import { createFileRoute } from '@tanstack/react-router'
import { WagoPLC } from '../components/WagoPLC'
import { SiemensPLC } from '../components/SiemensPLC'

export const Route = createFileRoute('/')({
  component: App,
})

function App() {
  return (
    <div className="min-h-screen bg-background py-8">
      <div className="container mx-auto px-4">
        <h1 className="text-3xl font-bold text-foreground mb-8">PLC Monitoring Dashboard</h1>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <WagoPLC />
          <SiemensPLC />
        </div>
      </div>
    </div>
  )
}
