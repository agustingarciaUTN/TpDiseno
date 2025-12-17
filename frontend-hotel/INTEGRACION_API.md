# Guía de Integración Frontend-Backend

## ✅ Configuración Completada

Se han realizado las siguientes configuraciones para conectar el frontend con el backend:

### Backend (Spring Boot)

1. **Puerto configurado:** `8080` en [application.properties](../src/main/resources/application.properties)

2. **CORS habilitado:** Se creó [CorsConfig.java](../src/main/java/Facultad/TrabajoPracticoDesarrollo/Config/CorsConfig.java) para permitir peticiones desde:
   - `http://localhost:3000` (frontend en desarrollo)
   - `http://localhost:3001` (puerto alternativo)
   - `http://127.0.0.1:3000`

3. **Endpoints disponibles:**
   - `/api/huespedes` - Gestión de huéspedes
   - `/api/habitaciones` - Gestión de habitaciones
   - `/api/reservas` - Gestión de reservas
   - `/api/estadias` - Gestión de estadías
   - `/api/pagos` - Gestión de pagos
   - `/api/usuarios` - Gestión de usuarios

### Frontend (Next.js)

1. **Variables de entorno:** Configurado [.env.local](../.env.local)
   ```
   NEXT_PUBLIC_API_URL=http://localhost:8080/api
   ```

2. **Cliente API:** El archivo [lib/api.ts](../lib/api.ts) incluye:
   - `apiFetch()` - Función genérica para llamadas a la API
   - `buscarHuespedes()` - Buscar huéspedes
   - `verificarExistenciaHuesped()` - Verificar duplicados
   - `crearHuesped()` - Crear nuevo huésped
   - `obtenerHuespedPorId()` - Obtener un huésped específico

## 🔄 Cómo Usar la API en Tus Componentes

### Ejemplo 1: Buscar Huéspedes

```typescript
// En tu componente o página
import { buscarHuespedes } from "@/lib/api"
import { DtoHuesped } from "@/lib/types"

export default function BuscarHuespedPage() {
  const [huespedes, setHuespedes] = useState<DtoHuesped[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleBuscar = async (criterios: Partial<DtoHuesped>) => {
    try {
      setLoading(true)
      setError(null)
      
      // Llamada directa al backend
      const resultados = await buscarHuespedes(criterios)
      
      setHuespedes(resultados)
    } catch (err: any) {
      setError(err.message || "Error al buscar huéspedes")
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      {/* Tu UI aquí */}
      {loading && <p>Cargando...</p>}
      {error && <p className="text-red-500">{error}</p>}
      {huespedes.map(h => <div key={h.idHuesped}>{h.apellido}</div>)}
    </div>
  )
}
```

### Ejemplo 2: Verificar Existencia Antes de Crear

```typescript
import { verificarExistenciaHuesped, crearHuesped } from "@/lib/api"

async function handleGuardarHuesped(formData: DtoHuesped) {
  try {
    // 1. Verificar si existe
    const existe = await verificarExistenciaHuesped(
      formData.tipoDocumento,
      formData.nroDocumento
    )
    
    if (existe) {
      alert(`Ya existe un huésped con ese documento: ${existe.nombres} ${existe.apellido}`)
      return
    }
    
    // 2. Crear nuevo huésped
    const nuevoHuesped = await crearHuesped(formData)
    
    alert(`Huésped creado exitosamente con ID: ${nuevoHuesped.idHuesped}`)
    
  } catch (error: any) {
    alert(`Error: ${error.message}`)
  }
}
```

### Ejemplo 3: Uso con React Hook Form

```typescript
import { useForm } from "react-hook-form"
import { crearHuesped } from "@/lib/api"

export default function FormularioHuesped() {
  const { register, handleSubmit } = useForm<DtoHuesped>()
  
  const onSubmit = async (data: DtoHuesped) => {
    try {
      const resultado = await crearHuesped(data)
      console.log("Huésped creado:", resultado)
      // Redirigir o mostrar mensaje de éxito
    } catch (error: any) {
      console.error("Error:", error.message)
    }
  }
  
  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      {/* Campos del formulario */}
    </form>
  )
}
```

## 🔧 Reemplazar API Routes Simuladas

Actualmente tienes API routes en Next.js que simulan el backend (como [app/api/huespedes/buscar/route.ts](../app/api/huespedes/buscar/route.ts)).

Para usar el backend real:

### Opción 1: Eliminar API Routes (Recomendado)
Elimina los archivos en `app/api/` y usa directamente las funciones de `lib/api.ts` en tus componentes.

### Opción 2: Modificar API Routes para Proxy
Si quieres mantenerlas como proxy al backend:

```typescript
// app/api/huespedes/buscar/route.ts
import { NextRequest, NextResponse } from "next/server"

export async function POST(request: NextRequest) {
  try {
    const body = await request.json()
    
    // Llamar al backend real
    const response = await fetch("http://localhost:8080/api/huespedes/buscar", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    })
    
    const data = await response.json()
    return NextResponse.json(data)
    
  } catch (error) {
    return NextResponse.json({ error: "Error en la búsqueda" }, { status: 500 })
  }
}
```

**Recomendación:** Usa **Opción 1** para simplicidad y mejor performance.

## 🚀 Iniciar Ambos Servidores

1. **Terminal 1 - Backend:**
   ```bash
   cd c:\Facu\Diseno\TPCode\TpDiseno
   ./mvnw spring-boot:run
   ```

2. **Terminal 2 - Frontend:**
   ```bash
   cd frontend-hotel
   npm run dev
   ```

3. **Abrir navegador:** http://localhost:3000

## 🐛 Debugging

### Ver peticiones en el navegador
1. Abre DevTools (F12)
2. Ve a la pestaña "Network"
3. Filtra por "Fetch/XHR"
4. Busca peticiones a `localhost:8080`

### Ver respuestas del backend
Las funciones en `api.ts` automáticamente hacen `console.error()` cuando hay errores.

### Verificar CORS
Si ves errores de CORS, verifica:
1. El backend está corriendo
2. `CorsConfig.java` existe en la carpeta `Config`
3. Reinicia el backend después de crear `CorsConfig.java`

## 📚 Próximos Pasos

1. Reemplaza las API routes simuladas con llamadas directas
2. Implementa funciones similares para otros endpoints:
   - Habitaciones
   - Reservas
   - Estadías
   - Pagos
3. Agrega manejo de errores específicos
4. Implementa loading states en todos los formularios
5. Considera agregar React Query para caché de datos
