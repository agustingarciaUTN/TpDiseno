'use client';

import { useState } from 'react';
import { apiFetch } from '@/lib/api'; // Tu archivo
import { DtoPago, Efectivo } from '@/lib/types'; // Tus tipos

export default function RegistrarPago() {
    const [loading, setLoading] = useState(false);
    const [mensaje, setMensaje] = useState('');

    const handlePagar = async () => {
        setLoading(true);
        setMensaje('');

        try {
            // 1. Preparamos el objeto (TypeScript valida que coincida con la interfaz)
            const payload: DtoPago = {
                idPago: 0,
                montoTotal: 1500.0,
                idFactura: 10, // ID de una factura existente en tu BD
                mediosPago: [
                    {
                        tipoMedio: 'EFECTIVO', // Importante para el mapeo polimórfico en Java
                        monto: 1500.0,
                        moneda: 'PESOS_ARGENTINOS'
                    } as Efectivo
                ]
            };

            // 2. Llamada al Backend usando tu wrapper
            // Se convertirá en: POST http://localhost:8080/api/pagos/registrar
            await apiFetch('/pagos/registrar', {
                method: 'POST',
                body: payload,
            });

            setMensaje('✅ Pago registrado con éxito en el sistema.');

        } catch (error: any) {
            // Tu api.ts lanza un Error con el mensaje del backend, aquí lo capturamos
            console.error(error);
            setMensaje(`❌ Error: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-8">
            <h1 className="text-2xl font-bold mb-4">Registrar Pago</h1>

            <button
                onClick={handlePagar}
                disabled={loading}
                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
            >
                {loading ? 'Procesando...' : 'Pagar $1500 (Efectivo)'}
            </button>

            {mensaje && (
                <div className={`mt-4 p-3 rounded ${mensaje.startsWith('✅') ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                    {mensaje}
                </div>
            )}
        </div>
    );
}