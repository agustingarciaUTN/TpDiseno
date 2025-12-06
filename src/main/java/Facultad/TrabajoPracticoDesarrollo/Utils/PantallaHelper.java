package Facultad.TrabajoPracticoDesarrollo.Utils;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;

import java.util.List;

public class PantallaHelper {

    public static String centrarTexto(String texto, int ancho) {
        if (texto == null) texto = "";
        if (texto.length() >= ancho) return texto.substring(0, ancho); // Recortar si es muy largo

        int padding = (ancho - texto.length()) / 2;
        String pad = " ".repeat(padding);
        String resultado = pad + texto + pad;

        // Ajuste fino si la división no fue exacta
        while (resultado.length() < ancho) {
            resultado += " ";
        }
        return resultado;
    }

    // Método que imprime la fila superior con los TIPOS agrupados
    public void imprimirEncabezadoTipos(List<Habitacion> habitacionesOrdenadas) {
        // Espacio vacío sobre la columna de fechas (13 espacios)
        System.out.print("             ");

        int i = 0;
        while (i < habitacionesOrdenadas.size()) {
            Habitacion actual = habitacionesOrdenadas.get(i);
            String tipoActual = actual.getTipoHabitacion().getDescripcion(); // O .name() si prefieres

            // Contar cuántas habitaciones consecutivas son de este mismo tipo
            int contador = 0;
            for (int j = i; j < habitacionesOrdenadas.size(); j++) {
                if (habitacionesOrdenadas.get(j).getTipoHabitacion() == actual.getTipoHabitacion()) {
                    contador++;
                } else {
                    break;
                }
            }

            // Calcular el ancho total de este grupo
            // Cada celda de habitación ocupa 12 caracteres: "| " (2) + 9 (texto) + " " (1)
            int anchoGrupo = contador * 12;

            // Imprimir el nombre del tipo centrado en ese ancho, con bordes
            // Usamos CYAN para destacar el tipo
            System.out.print(Colores.CYAN + "|" + centrarTexto(tipoActual, anchoGrupo - 1) + Colores.RESET);

            // Saltar el índice
            i += contador;
        }
        System.out.println("|"); // Cerrar la línea

        // Imprimir una línea separadora decorativa debajo de los tipos
        System.out.print("             ");
        i = 0;
        while (i < habitacionesOrdenadas.size()) {
            Habitacion actual = habitacionesOrdenadas.get(i);
            int contador = 0;
            for (int j = i; j < habitacionesOrdenadas.size(); j++) {
                if (habitacionesOrdenadas.get(j).getTipoHabitacion() == actual.getTipoHabitacion()) contador++;
                else break;
            }
            // Dibuja "-----------"
            System.out.print("+" + "-".repeat((contador * 12) - 1));
            i += contador;
        }
        System.out.println("+");
    }
}
