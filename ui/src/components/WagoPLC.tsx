import { useWagoLightStatus } from '../lib/api';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';

export function WagoPLC() {
  const { data, isLoading, error } = useWagoLightStatus();

  const sendControlCommand = async (command: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/wago/control?command=${command}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error('Failed to send control command');
      }
    } catch (error) {
      console.error('Error sending control command:', error);
    }
  };

  if (isLoading) {
    return (
      <Card>
        <CardContent className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card className="bg-destructive/10">
        <CardContent className="p-4 text-destructive">
          Error loading Wago PLC data: {(error as Error).message}
        </CardContent>
      </Card>
    );
  }

  // Convert number to binary string and pad with zeros to ensure 2 bytes 
  const binaryStatus = data?.status.toString(2).padStart(16, '0') || '0000000000000000';

  return (
    <Card>
      <CardHeader>
        <CardTitle>Wago 750 PLC</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-8 gap-4">
          {Array.from(binaryStatus).map((bit, index) => (
            <div key={index} className="flex flex-col items-center space-y-2">
              <div
                className={`w-12 h-12 rounded-full ${
                  bit === '1' ? 'bg-primary' : 'bg-muted'
                }`}
              />
              <span className="text-xs text-muted-foreground">
                {bit === '1' ? 'ON' : 'OFF'}
              </span>
            </div>
          ))}
        </div>
        <div className="flex justify-center space-x-4 mt-6">
          <Button
            variant="secondary"
            onClick={() => sendControlCommand(0)}
          >
            Standby
          </Button>
          <Button
            onClick={() => sendControlCommand(1)}
          >
            Mode 1
          </Button>
          <Button
            onClick={() => sendControlCommand(2)}
          >
            Mode 2
          </Button>
          <Button
            onClick={() => sendControlCommand(3)}
          >
            Mode 3
          </Button>
        </div>
        <div className="text-sm text-muted-foreground">
          Last updated: {new Date(data?.timestamp || '').toLocaleString()}
        </div>
      </CardContent>
    </Card>
  );
} 