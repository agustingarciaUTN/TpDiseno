package Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs;
import Facultad.TrabajoPracticoDesarrollo.Dominio.NotaDeCredito;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoNotaDeCredito;

public interface DaoInterfazNotaDeCredito {
    boolean persistirNota(NotaDeCredito nota) throws PersistenciaException;
    DtoNotaDeCredito obtenerPorNumero(String numero);
}