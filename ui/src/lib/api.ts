import { useQuery } from '@tanstack/react-query';

export interface WagoLightStatus {
  status: number;
  timestamp: string;
}

export interface SiemensValues {
  istTemperatur: number;
  sollTemperatur: number;
  differenzTemperatur: number;
  timestamp: string;
}

const API_BASE_URL = 'http://localhost:8080/api';

export const useWagoLightStatus = () => {
  return useQuery({
    queryKey: ['wago-light-status'],
    queryFn: async (): Promise<WagoLightStatus> => {
      const response = await fetch(`${API_BASE_URL}/wago/latest`);
      if (!response.ok) {
        throw new Error('Failed to fetch Wago light status');
      }
      return response.json();
    },
    refetchInterval: 500,
  });
};

export const useSiemensValues = () => {
  return useQuery({
    queryKey: ['siemens-values'],
    queryFn: async (): Promise<SiemensValues> => {
      const [istResponse, sollResponse, differenzResponse] = await Promise.all([
        fetch(`${API_BASE_URL}/temperature/ist`),
        fetch(`${API_BASE_URL}/temperature/soll`),
        fetch(`${API_BASE_URL}/temperature/differenz`)
      ]);

      if (!istResponse.ok || !sollResponse.ok || !differenzResponse.ok) {
        throw new Error('Failed to fetch Siemens values');
      }

      const [istData, sollData, differenzData] = await Promise.all([
        istResponse.json(),
        sollResponse.json(),
        differenzResponse.json()
      ]);

      return {
        istTemperatur: istData.istTemperatur,
        sollTemperatur: sollData.sollTemperatur,
        differenzTemperatur: differenzData.differenzTemperatur,
        timestamp: istData.timestamp 
      };
    },
    refetchInterval: 500,
  });
}; 