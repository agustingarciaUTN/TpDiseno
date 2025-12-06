package Facultad.TrabajoPracticoDesarrollo.Usuario;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Usuario;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;

public interface DaoUsuarioInterfaz {
    // Para persistir, recibimos la entidad completa con el hash ya calculado por el Gestor
    boolean persistir(Usuario usuario) throws PersistenciaException;

    // Buscamos por nombre para el login
    DtoUsuario buscarPorNombre(String nombre) throws PersistenciaException;

    // Si necesitamos modificar contraseña
    boolean modificar(Usuario usuario) throws PersistenciaException;
}