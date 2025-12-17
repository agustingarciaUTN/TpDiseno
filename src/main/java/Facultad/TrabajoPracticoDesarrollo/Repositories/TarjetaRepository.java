     package Facultad.TrabajoPracticoDesarrollo.Repositories;

     import Facultad.TrabajoPracticoDesarrollo.Dominio.Tarjeta;
     import org.springframework.data.jpa.repository.JpaRepository;
     import org.springframework.stereotype.Repository;

     /**
      * Repositorio Spring Data JPA para la entidad {@link Tarjeta}.
      *
      * <p>Proporciona las operaciones CRUD y de paginación/herencia que implementa
      * {@link JpaRepository}. La clave primaria de {@code Tarjeta} es de tipo {@code String}
      * (por ejemplo {@code numeroTarjeta}).</p>
      *
      * <p>Notas de implementación:
      * - Las consultas y métodos comunes (findAll, findById, save, delete, etc.) son
      *   provistos por {@link JpaRepository} y su implementación la genera Spring Data
      *   en tiempo de ejecución.
      * - Si {@code Tarjeta} tiene subclases mapeadas (por ejemplo {@code TarjetaCredito}
      *   o {@code TarjetaDebito}) y se utiliza una estrategia de herencia en JPA,
      *   al persistir instancias de dichas subclases JPA realizará operaciones
      *   en la tabla base y en la(s) tabla(s) correspondiente(s) de las subclases
      *   según la estrategia configurada en la entidad ({@code @Inheritance}).</p>
      *
      * <p>Se marca con {@code @Repository} para que Spring la detecte como componente
      * de persistencia y aplique traducción de excepciones de persistencia cuando corresponda.</p>
      */
     @Repository
     public interface TarjetaRepository extends JpaRepository<Tarjeta, String> {
         // Observación: Al guardar una entidad "TarjetaCredito", JPA inserta en 'tarjeta' y 'tarjeta_credito'.
         // Al guardar una "TarjetaDebito", inserta en 'tarjeta' y 'tarjeta_debito'.
     }