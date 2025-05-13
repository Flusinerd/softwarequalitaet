import { useSiemensValues } from '../lib/api';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';

export function SiemensPLC() {
  const { data, isLoading, error } = useSiemensValues();

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
          Error loading Siemens PLC data: {(error as Error).message}
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Siemens S7-1500 PLC</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="grid grid-cols-3 gap-4">
          <Card>
            <CardContent className="p-4">
              <h3 className="text-sm font-medium text-muted-foreground mb-1">Actual Value</h3>
              <p className="text-2xl font-bold">{data?.istTemperatur.toFixed(2)} °C</p>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4">
              <h3 className="text-sm font-medium text-muted-foreground mb-1">Target Value</h3>
              <p className="text-2xl font-bold">{data?.sollTemperatur.toFixed(2)} °C</p>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4">
              <h3 className="text-sm font-medium text-muted-foreground mb-1">Difference</h3>
              <p className="text-2xl font-bold">{data?.differenzTemperatur.toFixed(2)} °C</p>
            </CardContent>
          </Card>
        </div>
        <div className="text-sm text-muted-foreground">
          Last updated: {new Date(data?.timestamp || '').toLocaleString()}
        </div>
      </CardContent>
    </Card>
  );
} 