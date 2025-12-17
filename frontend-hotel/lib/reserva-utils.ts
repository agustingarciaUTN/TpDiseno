import { DtoReserva } from "./types";

// Consulta si una reserva está pasada (fechaHasta < hoy)
export function esReservaPasada(reserva: DtoReserva): boolean {
  if (!reserva.fechaHasta) return false;
  const hoy = new Date();
  const hasta = new Date(reserva.fechaHasta);
  // Solo comparar fecha, no hora
  hoy.setHours(0,0,0,0);
  hasta.setHours(0,0,0,0);
  return hasta < hoy;
}

// Consulta si una reserva está linkeada a una estadía (tiene idEstadia o campo especial)
export function estaLinkeadaAEstadia(reserva: any): boolean {
  // Si el backend devuelve idEstadia o campo similar, usarlo
  return Boolean(reserva.idEstadia);
}
