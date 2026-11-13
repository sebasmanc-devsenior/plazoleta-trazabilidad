package com.plazoleta.traceability.application.dto;

/** Muestra una duración en segundos como horas:minutos:segundos, por ejemplo 00:12:05. */
final class DurationFormat {
    private DurationFormat() {
    }

    static String hhmmss(long totalSeconds) {
        return String.format("%02d:%02d:%02d", totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60);
    }
}
