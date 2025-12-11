"use client"

import { useState } from "react"
import { buscarHuespedes } from "@/lib/api"
import { DtoHuesped } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"

/**
 * Componente de ejemplo que muestra cómo hacer una llamada REAL al backend
 * 
 * Para usar este ejemplo:
 * 1. Asegúrate de que el backend esté corriendo (mvnw.cmd spring-boot:run)
 * 2. Importa este componente en alguna página
 * 3. Haz clic en "Probar Conexión"
 */
export function EjemploConexionBackend() {
  const [huespedes, setHuespedes] = useState<DtoHuesped[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState(false)

  const probarConexion = async () => {
    try {
      setLoading(true)
      setError(null)
      setSuccess(false)

      console.log("🔄 Intentando conectar con el backend...")
      console.log(`📡 URL: ${process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api"}`)

      // Buscar todos los huéspedes (criterio vacío)
      const resultados = await buscarHuespedes({})

      console.log("✅ Respuesta del backend:", resultados)

      setHuespedes(resultados)
      setSuccess(true)
    } catch (err: any) {
      console.error("❌ Error al conectar:", err)
      setError(err.message || "Error al conectar con el backend")
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card className="p-6 max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold mb-4">🔌 Probar Conexión Backend</h2>

      <div className="space-y-4">
        <div className="bg-gray-100 p-3 rounded">
          <p className="text-sm">
            <strong>URL del Backend:</strong>{" "}
            {process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api"}
          </p>
        </div>

        <Button onClick={probarConexion} disabled={loading} className="w-full">
          {loading ? "Conectando..." : "Probar Conexión"}
        </Button>

        {error && (
          <div className="bg-red-50 border border-red-200 rounded p-4">
            <p className="text-red-800 font-semibold">❌ Error</p>
            <p className="text-red-600 text-sm mt-1">{error}</p>
            <div className="mt-3 text-xs text-red-500">
              <p>Verificá que:</p>
              <ul className="list-disc ml-5 mt-1">
                <li>El backend esté corriendo en el puerto 8080</li>
                <li>No haya errores de CORS en la consola del navegador</li>
                <li>El archivo .env.local tenga NEXT_PUBLIC_API_URL correcto</li>
              </ul>
            </div>
          </div>
        )}

        {success && (
          <div className="bg-green-50 border border-green-200 rounded p-4">
            <p className="text-green-800 font-semibold">✅ Conexión exitosa!</p>
            <p className="text-green-600 text-sm mt-1">
              Se encontraron {huespedes.length} huéspedes en la base de datos.
            </p>

            {huespedes.length > 0 && (
              <div className="mt-4">
                <p className="text-sm font-semibold mb-2">Primeros resultados:</p>
                <div className="space-y-2">
                  {huespedes.slice(0, 3).map((h) => (
                    <div key={h.idHuesped} className="bg-white p-2 rounded text-sm">
                      <strong>{h.apellido}, {h.nombres}</strong> - {h.tipoDocumento} {h.nroDocumento}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {!error && !success && !loading && (
          <div className="bg-blue-50 border border-blue-200 rounded p-4 text-sm text-blue-800">
            <p className="font-semibold">ℹ️ Instrucciones</p>
            <ol className="list-decimal ml-5 mt-2 space-y-1">
              <li>Abrí una terminal y navegá a la carpeta del proyecto</li>
              <li>Ejecutá: <code className="bg-blue-100 px-1 rounded">mvnw.cmd spring-boot:run</code></li>
              <li>Esperá a que inicie el servidor (verás "Started TrabajoPracticoDesarrolloApplication")</li>
              <li>Hacé clic en el botón "Probar Conexión"</li>
            </ol>
          </div>
        )}
      </div>

      <div className="mt-6 pt-4 border-t">
        <p className="text-xs text-gray-500">
          💡 Tip: Abrí la consola del navegador (F12) para ver más detalles de la conexión
        </p>
      </div>
    </Card>
  )
}
