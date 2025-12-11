# Sistema de Gestión Hotelera

Proyecto Full Stack con Spring Boot (backend) y Next.js (frontend).

## 🚀 Inicio Rápido

### Backend (Spring Boot)

1. **Iniciar el backend:**
   ```bash
   # Desde la raíz del proyecto (TpDiseno)
   ./mvnw spring-boot:run
   
   # O en Windows:
   mvnw.cmd spring-boot:run
   ```

   El backend estará disponible en: `http://localhost:8080`

### Frontend (Next.js)

1. **Instalar dependencias** (solo la primera vez):
   ```bash
   cd frontend-hotel
   npm install
   ```

2. **Configurar variables de entorno:**
   - Ya existe un archivo `.env.local` configurado
   - Si necesitas cambiarlo, edita: `frontend-hotel/.env.local`

3. **Iniciar el frontend:**
   ```bash
   npm run dev
   ```

   El frontend estará disponible en: `http://localhost:3000`

## 📡 Endpoints del Backend

El backend expone los siguientes endpoints API:

- **Huéspedes:** `http://localhost:8080/api/huespedes`
- **Habitaciones:** `http://localhost:8080/api/habitaciones`
- **Reservas:** `http://localhost:8080/api/reservas`
- **Estadías:** `http://localhost:8080/api/estadias`
- **Pagos:** `http://localhost:8080/api/pagos`
- **Usuarios:** `http://localhost:8080/api/usuarios`

## 🔧 Configuración

### Backend
- Puerto: `8080`
- Base de datos: PostgreSQL (Neon)
- Archivo de configuración: `src/main/resources/application.properties`
- CORS configurado para: `http://localhost:3000`

### Frontend
- Puerto: `3000`
- URL del backend: `http://localhost:8080/api`
- Archivo de configuración: `frontend-hotel/.env.local`

## 🛠️ Solución de Problemas

### Error de CORS
Si ves errores de CORS en la consola del navegador:
1. Verifica que el backend esté corriendo en el puerto 8080
2. Verifica que `CorsConfig.java` esté en la carpeta `Config`
3. Reinicia el backend

### Error de conexión
Si el frontend no puede conectarse al backend:
1. Verifica que ambos servidores estén corriendo
2. Revisa que `.env.local` tenga: `NEXT_PUBLIC_API_URL=http://localhost:8080/api`
3. Verifica la URL en la consola de desarrollo del navegador

### Base de datos
Si hay problemas con la base de datos:
1. Verifica las credenciales en `application.properties`
2. Asegúrate de que la base de datos Neon esté activa
