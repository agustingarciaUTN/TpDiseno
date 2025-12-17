import { DtoReserva } from "./types";

// MODIFICADO: Ahora acepta { fechaHasta?: string } en lugar de exigir todo el DtoReserva
// Esto permite usarlo tanto con DtoReserva como con ReservaRow
export function esReservaPasada(reserva: { fechaHasta?: string }): boolean {
  if (!reserva.fechaHasta) return false;
  
  const hoy = new Date();
  const hasta = new Date(reserva.fechaHasta);
  
  // Solo comparar fecha, no hora
  hoy.setHours(0,0,0,0);
  hasta.setHours(0,0,0,0);
  
  return hasta < hoy;
}

export function estaLinkeadaAEstadia(reserva: any): boolean {
  // Si el backend devuelve idEstadia o campo similar, usarlo
  return Boolean(reserva.idEstadia);
}